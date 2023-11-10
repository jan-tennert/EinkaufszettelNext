package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.remote.ProductApi
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.ShopProductScreenModel
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ShopDetailScreenModel(
    private val shopId: Long,
    productApi: ProductApi,
    productDataSource: ProductDataSource,
    auth: Auth
): ShopProductScreenModel(auth, productApi, productDataSource) {

    val productFlow = productDataSource.getAllProducts()
        .map { it.filter { product -> product.shopId == shopId } }
        .stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun createProduct(content: String) = createProduct(shopId, content)

    override fun resetState() {
        mutableState.value = State.Idle
    }

}