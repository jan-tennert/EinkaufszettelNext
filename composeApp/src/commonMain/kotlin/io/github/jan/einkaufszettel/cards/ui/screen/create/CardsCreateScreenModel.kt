package io.github.jan.einkaufszettel.cards.ui.screen.create

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.models.UserImportScreenModel
import io.github.jan.einkaufszettel.cards.data.local.CardsDataSource
import io.github.jan.einkaufszettel.cards.data.remote.CardsApi
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageData
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageReader
import io.github.jan.supabase.auth.Auth
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CardsCreateScreenModel(
    profileDataSource: ProfileDataSource,
    profileApi: ProfileApi,
    private val auth: Auth,
    private val cardsDataSource: CardsDataSource,
    private val cardsApi: CardsApi,
    private val localImageReader: LocalImageReader
) : UserImportScreenModel(profileApi, profileDataSource) {

    sealed interface State : AppState {
        data object CreateSuccess : State
    }

    val userProfiles = profileDataSource.getProfiles()
        .stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())
    val authorizedUsers = mutableStateListOf<String>()
    private val _imageData = MutableStateFlow<LocalImageData?>(null)
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()
    val imageData = _imageData.asStateFlow()

    fun createCard() {
        screenModelScope.launch {
            mutableState.value = AppState.Loading
            runCatching {
                val imagePath = imageData.let {
                    val imagePath =
                        "${Clock.System.now().toEpochMilliseconds()}.${imageData.value!!.extension}"
                    cardsApi.uploadImage(imagePath, imageData.value!!.data)
                    imagePath
                }
                cardsApi.createCard(
                    imagePath,
                    description.value,
                    auth.currentUserOrNull()?.id ?: "",
                    authorizedUsers.toList()
                )
            }.onSuccess {
                cardsDataSource.insertCard(it)
                mutableState.value = State.CreateSuccess
            }.onFailure {
                it.printStackTrace()
                mutableState.value = AppState.NetworkError
            }
        }
    }

    fun importNativeFile(file: PlatformFile) {
        screenModelScope.launch {
            runCatching {
                localImageReader.platformFileToLocalImage(file)
            }.onSuccess {
                _imageData.value = it
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun resetContent() {
        _imageData.value = null
        _description.value = ""
        authorizedUsers.clear()
    }

    fun setDescription(description: String) {
        _description.value = description
    }
}