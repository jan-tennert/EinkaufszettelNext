package io.github.jan.einkaufszettel.settings.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.auth.data.remote.AuthenticationApi
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.shops.data.local.ProductDataSource
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
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