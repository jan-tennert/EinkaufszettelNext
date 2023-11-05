package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.data.remote.ProductApi
import io.github.jan.einkaufszettel.data.remote.ProfileApi
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopDetailScreenModel(
    private val shopId: Long,
    private val productApi: ProductApi,
    private val productDataSource: ProductDataSource,
    private val profileDataSource: ProfileDataSource,
    private val profileApi: ProfileApi
): StateScreenModel<ShopDetailScreenModel.State>(State.Idle) {

    sealed interface State {
        data object Idle: State
        data object Loading : State
        data object NetworkError : State
        data class Error(val message: String) : State
    }

    val productFlow = productDataSource.getAllProducts()
        .map { it.filter { product -> product.shopId == shopId } }
        .stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun refreshProducts(silent: Boolean) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            runCatching {
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

    private fun fetchUserProfiles(ids: List<String>) {
        screenModelScope.launch {
            runCatching {
                profileApi.retrieveProfiles(ids)
            }.onSuccess {
                profileDataSource.insertProfiles(it)
            }
        }
    }

}