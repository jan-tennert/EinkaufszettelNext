package io.github.jan.einkaufszettel.app.ui

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.PlatformNetworkContext
import io.github.jan.einkaufszettel.cards.data.local.CardsDataSource
import io.github.jan.einkaufszettel.cards.data.remote.CardsApi
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.einkaufszettel.profile.data.remote.ProfileDto
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.recipes.data.remote.RecipeApi
import io.github.jan.einkaufszettel.shops.data.local.ProductDataSource
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
import io.github.jan.einkaufszettel.shops.data.remote.ProductApi
import io.github.jan.einkaufszettel.shops.data.remote.ShopApi
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppScreenModel(
    private val profileDataSource: ProfileDataSource,
    private val productApi: ProductApi,
    private val productDataSource: ProductDataSource,
    private val shopDataSource: ShopDataSource,
    private val recipeDataSource: RecipeDataSource,
    private val recipeApi: RecipeApi,
    private val profileApi: ProfileApi,
    private val shopApi: ShopApi,
    private val cardApi: CardsApi,
    private val cardDataSource: CardsDataSource
): StateScreenModel<AppScreenModel.State>(State.Idle) {

    sealed interface State {

        data object Idle: State
        data object Loading: State
        data object NetworkError: State
        data class Error(val message: String): State

    }

    enum class RefreshType{
        PRODUCTS,
        SHOPS,
        RECIPES,
        CARDS,
        ALL
    }

    fun refresh(silent: Boolean, type: RefreshType = RefreshType.ALL, endRefresh: () -> Unit = {}) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            withContext(PlatformNetworkContext) {
                runCatching {
                    val oldProfiles = profileDataSource.retrieveAllProfiles()
                    val profilesToFetch = mutableListOf<String>()
                    if(type in listOf(RefreshType.PRODUCTS, RefreshType.ALL))
                        refreshProducts(oldProfiles).also { profilesToFetch.addAll(it) }
                    if(type in listOf(RefreshType.SHOPS, RefreshType.ALL))
                        refreshShops(oldProfiles).also { profilesToFetch.addAll(it) }
                    if(type in listOf(RefreshType.RECIPES, RefreshType.ALL))
                        refreshRecipes()
                    if(type in listOf(RefreshType.CARDS, RefreshType.ALL))
                        refreshCards()
                    if(profilesToFetch.isNotEmpty()) {
                        fetchUserProfiles(profilesToFetch)
                    }
                }.onFailure {
                    it.printStackTrace()
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
                endRefresh()
            }
        }
    }

    private suspend fun refreshProducts(oldProfiles: List<ProfileDto>): List<String> {
        val oldProducts = productDataSource.retrieveAllProducts()
        val newProducts = productApi.retrieveProducts()
        val profilesToFetch = newProducts.map { listOf(it.userId, it.doneBy) }.flatten().filterNotNull().filter { profileId ->
            oldProfiles.none { profile ->
                profile.id == profileId
            }
        }
        val productsToDelete = oldProducts.filter { oldShop ->
            newProducts.none { newShop ->
                newShop.id == oldShop.id
            }
        }
        productDataSource.deleteAll(productsToDelete.map { it.id })
        productDataSource.insertAll(newProducts)
        return profilesToFetch
    }

    private suspend fun refreshShops(oldProfiles: List<ProfileDto>): List<String> {
        val oldShops = shopDataSource.retrieveAllShops()
        val newShops = shopApi.retrieveShops().map {
            val old = oldShops.find { oldShop -> oldShop.id == it.id }
            val collapsed = old?.collapsed ?: false
            val pinned = old?.pinned ?: false
            it.copy(collapsed = collapsed, pinned = pinned)
        }
        val profilesToFetch = newShops.map { listOf(it.ownerId) + it.authorizedUsers }.flatten().filterNotNull().filter { profileId ->
            oldProfiles.none { profile ->
                profile.id == profileId
            }
        }
        val shopsToDelete = oldShops.filter { oldShop ->
            newShops.none { newShop ->
                newShop.id == oldShop.id
            }
        }
        shopDataSource.deleteAll(shopsToDelete.map { it.id })
        shopDataSource.insertAll(newShops)
        return profilesToFetch
    }

    private suspend fun refreshRecipes() {
        val oldRecipes = recipeDataSource.retrieveAllRecipes()
        val newRecipes = recipeApi.retrieveRecipes()
        val recipesToDelete = oldRecipes.filter { oldRecipe ->
            newRecipes.none { newRecipe ->
                newRecipe.id == oldRecipe.id
            }
        }
        recipeDataSource.deleteAll(recipesToDelete.map { it.id })
        recipeDataSource.insertAll(newRecipes)
    }

    private suspend fun refreshCards() {
        val oldCards = cardDataSource.retrieveAllCards()
        val newCards = cardApi.retrieveCards()
        val cardsToDelete = oldCards.filter { oldCard ->
            newCards.none { newCard ->
                newCard.id == oldCard.id
            }
        }
        cardDataSource.deleteAll(cardsToDelete.map { it.id })
        cardDataSource.insertCards(newCards)
    }

    private fun fetchUserProfiles(ids: List<String>) {
        screenModelScope.launch {
            runCatching {
                profileApi.retrieveProfiles(ids)
            }.onSuccess {
                profileDataSource.insertProfiles(it)
            }
        }
    }

    fun resetState() {
        mutableState.value = State.Idle
    }

}