package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.PlatformNetworkContext
import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.remote.ProductApi
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail.ShopDetailScreenModel
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.GoTrue
import kotlinx.coroutines.launch

open class ShopProductScreenModel(
    private val goTrue: GoTrue,
    private val productApi: ProductApi,
    private val productDataSource: ProductDataSource
): StateScreenModel<ShopProductScreenModel.State>(State.Idle) {

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data object NetworkError : State
        data class Error(val message: String) : State
    }

    fun changeDoneStatus(productId: Long, done: Boolean) {
        screenModelScope.launch(PlatformNetworkContext) {
            mutableState.value = State.Loading
            productDataSource.setLoading(productId, true)
            runCatching {
                productApi.changeDoneStatus(productId, if (done) goTrue.currentUserOrNull()?.id else null)
            }.onSuccess {
                productDataSource.changeDoneStatus(productId, it.doneBy, it.doneSince)
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = State.Error(it.message ?: "")
                    else -> mutableState.value = State.NetworkError
                }
            }.onSuccess {
                mutableState.value = State.Idle
            }
            productDataSource.setLoading(productId, false)
        }
    }

    fun deleteProduct(productId: Long) {
        screenModelScope.launch(PlatformNetworkContext) {
            mutableState.value = State.Loading
            runCatching {
                productApi.deleteProduct(productId)
            }.onSuccess {
                productDataSource.deleteProductById(productId)
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = State.Error(it.message ?: "")
                    else -> mutableState.value = State.NetworkError
                }
            }.onSuccess {
                mutableState.value = State.Idle
            }
        }
    }

    fun editContent(productId: Long, content: String) {
        screenModelScope.launch(PlatformNetworkContext) {
            mutableState.value = State.Loading
            productDataSource.setLoading(productId, true)
            runCatching {
                productApi.editContent(productId, content)
            }.onSuccess {
                productDataSource.editContent(productId, content)
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = State.Error(it.message ?: "")
                    else -> mutableState.value = State.NetworkError
                }
            }.onSuccess {
                mutableState.value = State.Idle
            }
            productDataSource.setLoading(productId, false)
        }
    }

    fun createProduct(shopId: Long, content: String) {
        screenModelScope.launch(PlatformNetworkContext) {
            mutableState.value = State.Loading
            runCatching {
                productApi.createProduct(shopId, content, goTrue.currentUserOrNull()?.id ?: error("User not logged in"))
            }.onSuccess {
                productDataSource.insertProduct(it)
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = State.Error(it.message ?: "")
                    else -> mutableState.value = State.NetworkError
                }
            }.onSuccess {
                mutableState.value = State.Idle
            }
        }
    }

}