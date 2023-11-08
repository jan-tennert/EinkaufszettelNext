package io.github.jan.einkaufszettel.ui.screen.app.tabs.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.data.local.ShopDataSource
import io.github.jan.einkaufszettel.data.remote.AuthenticationApi
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val authenticationApi: AuthenticationApi,
    private val recipeDataSource: RecipeDataSource,
    private val productDataSource: ProductDataSource,
    private val shopDataSource: ShopDataSource
): ScreenModel {

    fun logout() {
        screenModelScope.launch {
            runCatching {
                recipeDataSource.clearRecipes()
                productDataSource.clearProducts()
                shopDataSource.clear()
                authenticationApi.logout()
            }.onFailure {
                it.printStackTrace()
            }.onSuccess {
                //add state here
            }
        }
    }

}