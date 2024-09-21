package io.github.jan.einkaufszettel.settings.ui

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateModel
import io.github.jan.einkaufszettel.auth.data.remote.AuthenticationApi
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.shops.data.local.ProductDataSource
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
import io.github.jan.supabase.auth.Auth
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
): AppStateModel() {

    sealed interface State: AppState {
        data object PasswordChanged : State
        data class ProfileUpdated(val name: String) : State
    }

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

    fun changePassword(newPassword: String) {
        mutableState.value = AppState.Loading
        screenModelScope.launch {
            runCatching {
                auth.updateUser {
                    this.password = newPassword
                }
            }.onFailure {
                it.printStackTrace()
            }.onSuccess {
                mutableState.value = State.PasswordChanged
            }
        }
    }

    fun updateProfile(name: String) {
        screenModelScope.launch {
            runCatching {
                profileApi.updateOwnProfile(name)
            }.onFailure {
                it.printStackTrace()
            }.onSuccess {
                mutableState.value = State.ProfileUpdated(name)
                profileDataSource.insertProfile(it)
            }
        }
    }

}