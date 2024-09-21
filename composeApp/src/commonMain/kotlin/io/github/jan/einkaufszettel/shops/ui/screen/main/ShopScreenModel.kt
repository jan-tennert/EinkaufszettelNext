package io.github.jan.einkaufszettel.shops.ui.screen.main

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.app.ui.AppStateModel
import io.github.jan.einkaufszettel.shops.data.local.ProductDataSource
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
import io.github.jan.einkaufszettel.shops.data.remote.ShopApi
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SessionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopScreenModel(
    private val shopDataSource: ShopDataSource,
    private val productDataSource: ProductDataSource,
    private val shopApi: ShopApi,
    auth: Auth
): AppStateModel() {

    val shopFlow = shopDataSource.getAllShops().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())
    val userIdFlow = auth.sessionStatus
        .filter { it is SessionStatus.Authenticated }
        .map { (it as SessionStatus.Authenticated).session.user?.id }
        .stateIn(screenModelScope, SharingStarted.Eagerly, null)

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