package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.ShopDataSource
import io.github.jan.einkaufszettel.data.remote.ShopApi
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopScreenModel(
    private val shopDataSource: ShopDataSource,
    private val shopApi: ShopApi
): StateScreenModel<ShopScreenModel.State>(State.Idle) {

    sealed interface State {
        data object Idle: State
        data object Loading : State
        data object NetworkError : State
        data class Error(val message: String) : State
    }

    val shops = shopDataSource.getAllShops().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    fun refreshShops(silent: Boolean) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            runCatching {
                val oldShops = shopDataSource.retrieveAllShops()
                val newShops = shopApi.retrieveShops()
                val shopsToDelete = oldShops.filter { oldShop ->
                    newShops.none { newShop ->
                        newShop.id == oldShop.id
                    }
                }
                shopDataSource.insertAll(newShops)
                shopDataSource.deleteAll(shopsToDelete.map { it.id })
            }.onFailure {
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

    fun deleteShop(shopId: Long) {
        screenModelScope.launch {
            runCatching {
                shopApi.deleteShop(shopId)
            }.onSuccess {
                shopDataSource.deleteById(shopId)
            }
        }
    }

}