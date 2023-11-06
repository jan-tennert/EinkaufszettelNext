package io.github.jan.einkaufszettel.ui.screen.app

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.data.local.ShopDataSource
import io.github.jan.einkaufszettel.data.remote.ProductApi
import io.github.jan.einkaufszettel.data.remote.ProfileApi
import io.github.jan.einkaufszettel.data.remote.ShopApi
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.launch

class AppScreenModel(
    private val profileDataSource: ProfileDataSource,
    private val productApi: ProductApi,
    private val productDataSource: ProductDataSource,
    private val shopDataSource: ShopDataSource,
    private val profileApi: ProfileApi,
    private val shopApi: ShopApi
): StateScreenModel<AppScreenModel.State>(State.Idle) {

    sealed interface State {

        data object Idle: State
        data object Loading: State
        data object NetworkError: State
        data class Error(val message: String): State

    }

    fun refresh(silent: Boolean) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            runCatching {
                refreshProducts()
                refreshShops()
            }.onFailure {
                if (!silent) {
                    when(it) {
                        is RestException -> mutableState.value = State.Error(it.message ?: "")
                        else -> mutableState.value = State.NetworkError
                    }
                } else {
                    mutableState.value = State.Idle
                }
            }.onSuccess {
                mutableState.value = State.Idle
            }
        }
    }

    private suspend fun refreshProducts() {
        val oldShops = productDataSource.retrieveAllProducts()
        val newShops = productApi.retrieveProducts()
        val oldProfiles = profileDataSource.retrieveAllProfiles()
        val profilesToFetch = newShops.map { listOf(it.userId, it.doneBy) }.flatten().filterNotNull().filter { profileId ->
            oldProfiles.none { profile ->
                profile.id == profileId
            }
        }
        val shopsToDelete = oldShops.filter { oldShop ->
            newShops.none { newShop ->
                newShop.id == oldShop.id
            }
        }
        productDataSource.insertAll(newShops)
        productDataSource.deleteAll(shopsToDelete.map { it.id })
        fetchUserProfiles(profilesToFetch)
    }

    private suspend fun refreshShops() {
        val oldShops = shopDataSource.retrieveAllShops()
        val newShops = shopApi.retrieveShops()
        val shopsToDelete = oldShops.filter { oldShop ->
            newShops.none { newShop ->
                newShop.id == oldShop.id
            }
        }
        shopDataSource.insertAll(newShops)
        shopDataSource.deleteAll(shopsToDelete.map { it.id })
    }

    private fun fetchUserProfiles(ids: List<String>) {
        screenModelScope.launch {
            runCatching {
                profileApi.retrieveProfiles(ids)
            }.onSuccess {
                profileDataSource.insertProfiles(it)
            }
        }
    }

    fun resetState() {
        mutableState.value = State.Idle
    }

}