package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.main

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.local.ShopDataSource
import io.github.jan.einkaufszettel.data.remote.ShopApi
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
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
): StateScreenModel<ShopScreenModel.State>(State.Idle) {

    sealed interface State {
        data object Idle: State
        data object Loading : State
        data object NetworkError : State
        data class Error(val message: String) : State
    }

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