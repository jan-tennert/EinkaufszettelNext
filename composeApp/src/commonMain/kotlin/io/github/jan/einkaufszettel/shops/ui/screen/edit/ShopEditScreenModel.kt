package io.github.jan.einkaufszettel.shops.ui.screen.edit

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.models.UserImportScreenModel
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
import io.github.jan.einkaufszettel.shops.data.remote.ShopApi
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopEditScreenModel(
    private val shopId: Long,
    private val shopApi: ShopApi,
    private val shopDataSource: ShopDataSource,
    profileApi: ProfileApi,
    profileDataSource: ProfileDataSource
): UserImportScreenModel(profileApi, profileDataSource) {

    sealed interface State: AppState {
        data object Success : State
    }

    val shop = shopDataSource.getShopById(shopId).stateIn(screenModelScope, SharingStarted.Eagerly, null)
    val userProfiles = profileDataSource.getProfiles().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun updateShop(
        name: String,
        authorizedUsers: List<String>,
    ) {
        screenModelScope.launch {
            mutableState.value = AppState.Loading
            runCatching {
                shopApi.editShop(
                    id = shopId,
                    newName = name,
                    authorizedUsers = authorizedUsers
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

}