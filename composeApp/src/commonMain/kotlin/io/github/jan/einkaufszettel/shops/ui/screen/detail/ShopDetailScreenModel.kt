package io.github.jan.einkaufszettel.shops.ui.screen.detail

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.shops.data.local.ProductDataSource
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
import io.github.jan.einkaufszettel.shops.data.remote.ProductApi
import io.github.jan.einkaufszettel.shops.ui.screen.ShopProductScreenModel
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ShopDetailScreenModel(
    private val shopId: Long,
    productApi: ProductApi,
    productDataSource: ProductDataSource,
    shopDataSource: ShopDataSource,
    auth: Auth
): ShopProductScreenModel(auth, productApi, productDataSource) {

    val productFlow = productDataSource.getAllProducts()
        .map { it.filter { product -> product.shopId == shopId } }
        .stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    val shopFlow = shopDataSource.getShopById(shopId).stateIn(screenModelScope, SharingStarted.Eagerly, null)

    fun createProduct(content: String) = createProduct(shopId, content)

}