package io.github.jan.einkaufszettel.cards.ui.screen.edit

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.models.UserImportScreenModel
import io.github.jan.einkaufszettel.cards.data.local.CardsDataSource
import io.github.jan.einkaufszettel.cards.data.remote.CardsApi
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardEditScreenModel(
    private val cardId: Long,
    private val cardsApi: CardsApi,
    private val cardsDataSource: CardsDataSource,
    profileApi: ProfileApi,
    profileDataSource: ProfileDataSource
): UserImportScreenModel(profileApi, profileDataSource) {

    sealed interface State: AppState {
        data object Success : State
    }

    val card = cardsDataSource.getCardById(cardId).stateIn(screenModelScope, SharingStarted.Eagerly, null)
    val userProfiles = profileDataSource.getProfiles().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun updateCard(description: String, authorizedUsers: List<String>) {
        screenModelScope.launch {
            mutableState.value = AppState.Loading
            runCatching {
                cardsApi.editCard(cardId, description, authorizedUsers.filter { it.isNotBlank() })
            }.onSuccess {
                cardsDataSource.insertCard(it)
                mutableState.value = State.Success
            }.onFailure {
                it.printStackTrace()
                when (it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
            }
        }
    }

}