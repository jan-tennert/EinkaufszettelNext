package io.github.jan.einkaufszettel.ui.screen.app

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.PlatformNetworkContext
import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.data.local.ShopDataSource
import io.github.jan.einkaufszettel.data.remote.ProductApi
import io.github.jan.einkaufszettel.data.remote.ProfileApi
import io.github.jan.einkaufszettel.data.remote.ShopApi
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    enum class RefreshType{
        PRODUCTS,
        SHOPS,
        ALL
    }

    fun refresh(silent: Boolean, type: RefreshType = RefreshType.ALL) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            withContext(PlatformNetworkContext) {
                runCatching {
                    if(type in listOf(RefreshType.PRODUCTS, RefreshType.ALL))
                        refreshProducts()
                    if(type in listOf(RefreshType.SHOPS, RefreshType.ALL))
                        refreshShops()
                }.onFailure {
                    it.printStackTrace()
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
    }

    private suspend fun refreshProducts() {
        val oldProducts = productDataSource.retrieveAllProducts()
        val newProducts = productApi.retrieveProducts()
        val oldProfiles = profileDataSource.retrieveAllProfiles()
        val profilesToFetch = newProducts.map { listOf(it.userId, it.doneBy) }.flatten().filterNotNull().filter { profileId ->
            oldProfiles.none { profile ->
                profile.id == profileId
            }
        }
        val productsToDelete = oldProducts.filter { oldShop ->
            newProducts.none { newShop ->
                newShop.id == oldShop.id
            }
        }
        productDataSource.deleteAll(productsToDelete.map { it.id })
        productDataSource.insertAll(newProducts)
        fetchUserProfiles(profilesToFetch)
    }

    private suspend fun refreshShops() {
        val oldShops = shopDataSource.retrieveAllShops()
        val newShops = shopApi.retrieveShops().map {
            val old = oldShops.find { oldShop -> oldShop.id == it.id }
            val collapsed = old?.collapsed ?: false
            val pinned = old?.pinned ?: false
            it.copy(collapsed = collapsed, pinned = pinned)
        }
        val shopsToDelete = oldShops.filter { oldShop ->
            newShops.none { newShop ->
                newShop.id == oldShop.id
            }
        }
        shopDataSource.deleteAll(shopsToDelete.map { it.id })
        shopDataSource.insertAll(newShops)
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