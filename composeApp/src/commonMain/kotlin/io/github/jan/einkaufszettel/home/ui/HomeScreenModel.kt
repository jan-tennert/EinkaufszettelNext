package io.github.jan.einkaufszettel.home.ui

import cafe.adriel.voyager.core.model.screenModelScope
import einkaufszettel.GetAllProducts
import io.github.jan.einkaufszettel.shops.data.local.ProductDataSource
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
import io.github.jan.einkaufszettel.shops.data.remote.ProductApi
import io.github.jan.einkaufszettel.shops.data.remote.ShopDto
import io.github.jan.einkaufszettel.shops.ui.screen.ShopProductScreenModel
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

typealias ShopAndProduct = Pair<ShopDto, List<GetAllProducts>>

class HomeScreenModel(
    productDataSource: ProductDataSource,
    private val shopDataSource: ShopDataSource,
    productApi: ProductApi,
    auth: Auth
): ShopProductScreenModel(auth, productApi, productDataSource) {

    val shopAndProductFlow: StateFlow<List<ShopAndProduct>> = productDataSource.getAllProducts().combine(shopDataSource.getAllShops()) { products, shops ->
        products
            .asSequence()
            .groupBy { it.shopId }
                .map { (shopId, products) ->
                    val shop = shops.find { it.id == shopId }
                    shop to products
                }
                .filter { it.first != null && it.second.isNotEmpty() }
                .map { it.first!! to it.second }
                .sortedBy { !it.first.pinned }
            .toList()
    }.stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun changeShopCollapse(shopId: Long, collapse: Boolean) {
        screenModelScope.launch {
            runCatching {
                shopDataSource.changeCollapsed(shopId, collapse)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun changeShopPinned(shopId: Long, pin: Boolean) {
        screenModelScope.launch {
            runCatching {
                shopDataSource.changePinned(shopId, pin)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

}