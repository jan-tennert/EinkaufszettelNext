package io.github.jan.einkaufszettel.settings.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.auth.data.remote.AuthenticationApi
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.shops.data.local.ProductDataSource
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val authenticationApi: AuthenticationApi,
    private val recipeDataSource: RecipeDataSource,
    private val productDataSource: ProductDataSource,
    private val shopDataSource: ShopDataSource,
    private val profileDataSource: ProfileDataSource,
    private val profileApi: ProfileApi,
    private val auth: Auth
): ScreenModel {

    val userProfile = profileDataSource.getOwnProfile().stateIn(screenModelScope, SharingStarted.Eagerly, null)

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