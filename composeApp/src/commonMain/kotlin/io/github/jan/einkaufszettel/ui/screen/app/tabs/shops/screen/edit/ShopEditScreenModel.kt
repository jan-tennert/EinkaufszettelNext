package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.edit

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.data.local.ShopDataSource
import io.github.jan.einkaufszettel.data.remote.ProfileApi
import io.github.jan.einkaufszettel.data.remote.ShopApi
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopEditScreenModel(
    private val shopId: Long,
    private val shopApi: ShopApi,
    private val shopDataSource: ShopDataSource,
    private val profileApi: ProfileApi,
    private val profileDataSource: ProfileDataSource
): StateScreenModel<ShopEditScreenModel.State>(State.Idle) {

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data object NetworkError : State
        data class Error(val message: String) : State
        data object Success : State
    }

    val shop = shopDataSource.getShopById(shopId).stateIn(screenModelScope, SharingStarted.Eagerly, null)
    val userProfiles = profileDataSource.getProfiles().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun updateShop(
        name: String,
        authorizedUsers: List<String>,
    ) {
        screenModelScope.launch {
            mutableState.value = State.Loading
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
                    is RestException -> mutableState.value = State.Error(it.message ?: "")
                    else -> mutableState.value = State.NetworkError
                }
            }
        }
    }

    fun importUser(id: String) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            runCatching {
                profileApi.retrieveProfile(id)
            }.onSuccess {
                if(it != null) {
                    profileDataSource.insertProfile(it)
                    mutableState.value = State.Idle
                } else {
                    mutableState.value = State.Error(Res.string.user_not_found)
                }
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = State.Error(it.message ?: "")
                    else -> mutableState.value = State.NetworkError
                }
            }
        }
    }

    fun resetState() {
        mutableState.value = State.Idle
    }

}