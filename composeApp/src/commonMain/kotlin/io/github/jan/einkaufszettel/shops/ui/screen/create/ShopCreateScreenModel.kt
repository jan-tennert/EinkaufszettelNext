package io.github.jan.einkaufszettel.shops.ui.screen.create

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.models.UserImportScreenModel
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageData
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageReader
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
import io.github.jan.einkaufszettel.shops.data.remote.ShopApi
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.Auth
import io.github.vinceglb.filekit.core.PlatformFile
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
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    val authorizedUsers = mutableStateListOf<String>()

    fun createShop() {
        screenModelScope.launch {
            mutableState.value = AppState.Loading
            runCatching {
                val iconPath = "${Clock.System.now().toEpochMilliseconds()}.${imageData.value!!.extension}"
                shopApi.uploadIcon(iconPath, imageData.value!!.data)
                shopApi.createShop(
                    name = name.value,
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

    fun setName(name: String) {
        _name.value = name
    }

    fun resetContent() {
        _name.value = ""
        _imageData.value = null
        authorizedUsers.clear()
    }

}