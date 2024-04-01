package io.github.jan.einkaufszettel.cards.ui.screen.main

import cafe.adriel.voyager.core.model.screenModelScope
import einkaufszettel.GetAllCards
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateModel
import io.github.jan.einkaufszettel.cards.data.local.CardsDataSource
import io.github.jan.einkaufszettel.cards.data.remote.CardsApi
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardsScreenModel(
    private val cardsDataSource: CardsDataSource,
    private val cardsApi: CardsApi,
    private val auth: Auth
): AppStateModel() {

    sealed interface State: AppState {
        data object DeleteSuccess : State
    }

    val cards = cardsDataSource.getAllCards().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())
    val userIdFlow = auth.sessionStatus
        .filter { it is SessionStatus.Authenticated }
        .map { (it as SessionStatus.Authenticated).session.user?.id }
        .stateIn(screenModelScope, SharingStarted.Eagerly, null)
    private val _showDeleteDialog = MutableStateFlow<GetAllCards?>(null)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    fun showDeleteDialog(card: GetAllCards?) {
        _showDeleteDialog.value = card
    }

    fun deleteCard(cardId: Long, imagePath: String) {
        screenModelScope.launch {
            mutableState.value = AppState.Loading
            runCatching {
                cardsApi.deleteCard(cardId)
                cardsApi.deleteImage(imagePath)
            }.onSuccess {
                cardsDataSource.deleteCardById(cardId)
                mutableState.value = State.DeleteSuccess
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
            }
        }
    }

}