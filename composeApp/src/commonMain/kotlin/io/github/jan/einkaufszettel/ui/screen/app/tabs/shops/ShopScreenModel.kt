package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.local.ShopDataSource
import io.github.jan.einkaufszettel.data.remote.ShopApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopScreenModel(
    private val shopDataSource: ShopDataSource,
    private val productDataSource: ProductDataSource,
    private val shopApi: ShopApi
): StateScreenModel<ShopScreenModel.State>(State.Idle) {

    sealed interface State {
        data object Idle: State
        data object Loading : State
        data object NetworkError : State
        data class Error(val message: String) : State
    }

    val shopFlow = shopDataSource.getAllShops().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun deleteShop(shopId: Long) {
        screenModelScope.launch {
            runCatching {
                shopApi.deleteShop(shopId)
            }.onSuccess {
                productDataSource.deleteAllInShop(shopId)
                shopDataSource.deleteById(shopId)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

}