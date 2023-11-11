package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.PlatformNetworkContext
import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.remote.ProductApi
import io.github.jan.einkaufszettel.ui.screen.app.AppState
import io.github.jan.einkaufszettel.ui.screen.app.AppStateModel
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.launch

open class ShopProductScreenModel(
    private val auth: Auth,
    private val productApi: ProductApi,
    private val productDataSource: ProductDataSource
): AppStateModel() {

    fun changeDoneStatus(productId: Long, done: Boolean) {
        screenModelScope.launch(PlatformNetworkContext) {
            mutableState.value = AppState.Loading
            productDataSource.setLoading(productId, true)
            runCatching {
                productApi.changeDoneStatus(productId, if (done) auth.currentUserOrNull()?.id else null)
            }.onSuccess {
                productDataSource.changeDoneStatus(productId, it.doneBy, it.doneSince)
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
            }.onSuccess {
                mutableState.value = AppState.Idle
            }
            productDataSource.setLoading(productId, false)
        }
    }

    fun deleteProduct(productId: Long) {
        screenModelScope.launch(PlatformNetworkContext) {
            mutableState.value = AppState.Loading
            productDataSource.setLoading(productId, true)
            runCatching {
                productApi.deleteProduct(productId)
            }.onSuccess {
                productDataSource.deleteProductById(productId)
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
                productDataSource.setLoading(productId, false)
            }.onSuccess {
                mutableState.value = AppState.Idle
            }
        }
    }

    fun editContent(productId: Long, content: String) {
        screenModelScope.launch(PlatformNetworkContext) {
            mutableState.value = AppState.Loading
            productDataSource.setLoading(productId, true)
            runCatching {
                productApi.editContent(productId, content)
            }.onSuccess {
                productDataSource.editContent(productId, content)
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
            }.onSuccess {
                mutableState.value = AppState.Idle
            }
            productDataSource.setLoading(productId, false)
        }
    }

    fun createProduct(shopId: Long, content: String) {
        screenModelScope.launch(PlatformNetworkContext) {
            mutableState.value = AppState.Loading
            runCatching {
                productApi.createProduct(shopId, content, auth.currentUserOrNull()?.id ?: error("User not logged in"))
            }.onSuccess {
                productDataSource.insertProduct(it)
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
            }.onSuccess {
                mutableState.value = AppState.Idle
            }
        }
    }

}