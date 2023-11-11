package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.create

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.data.local.ShopDataSource
import io.github.jan.einkaufszettel.data.local.image.LocalImageData
import io.github.jan.einkaufszettel.data.local.image.LocalImageReader
import io.github.jan.einkaufszettel.data.remote.ProfileApi
import io.github.jan.einkaufszettel.data.remote.ShopApi
import io.github.jan.einkaufszettel.ui.screen.app.AppState
import io.github.jan.einkaufszettel.ui.screen.app.tabs.models.UserImportScreenModel
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ShopCreateScreenModel(
    private val shopApi: ShopApi,
    profileDataSource: ProfileDataSource,
    profileApi: ProfileApi,
    private val shopDataSource: ShopDataSource,
    private val auth: Auth,
    private val localImageReader: LocalImageReader
): UserImportScreenModel(profileApi, profileDataSource) {

    sealed interface State: AppState {
        data object Success : State
    }

    val userProfiles = profileDataSource.getProfiles().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())
    private val _imageData = MutableStateFlow<LocalImageData?>(null)
    val imageData = _imageData.asStateFlow()

    fun createShop(
        name: String,
        iconData: LocalImageData,
        authorizedUsers: List<String>,
    ) {
        screenModelScope.launch {
            mutableState.value = AppState.Loading
            runCatching {
                val iconPath = "${Clock.System.now().toEpochMilliseconds()}.${iconData.extension}"
                shopApi.uploadIcon(iconPath, iconData.data)
                shopApi.createShop(
                    name = name,
                    iconUrl = shopApi.getIconUrl(iconPath),
                    authorizedUsers = authorizedUsers,
                    ownerId = auth.currentUserOrNull()?.id ?: ""
                )
            }.onSuccess {
                shopDataSource.insertShop(it)
                mutableState.value = State.Success
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
            }
        }
    }

    fun importNativeFile(file: Any) {
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

}