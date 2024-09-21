package io.github.jan.einkaufszettel.recipes.ui.main

import cafe.adriel.voyager.core.model.screenModelScope
import einkaufszettel.GetAllRecipes
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateModel
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.recipes.data.remote.RecipeApi
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeScreenModel(
    private val recipeDataSource: RecipeDataSource,
    private val recipeApi: RecipeApi,
    auth: Auth
): AppStateModel() {

    sealed interface State: AppState {
        data object DeleteSuccess : State
    }

    val recipeFlow = recipeDataSource.getAllRecipes().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())
    val userIdFlow = auth.sessionStatus
        .filter { it is SessionStatus.Authenticated }
        .map { (it as SessionStatus.Authenticated).session.user?.id }
        .stateIn(screenModelScope, SharingStarted.Eagerly, null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _showDeleteDialog = MutableStateFlow<GetAllRecipes?>(null)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onShowDeleteDialogChanged(recipe: GetAllRecipes?) {
        _showDeleteDialog.value = recipe
    }

    fun deleteRecipe(
        id: Long,
        imagePath: String?
    ) {
        mutableState.value = AppState.Loading
        screenModelScope.launch {
            runCatching {
                recipeApi.deleteRecipe(id)
                if (imagePath != null) {
                    recipeApi.deleteImage(imagePath)
                }
            }.onSuccess {
                recipeDataSource.deleteRecipe(id)
                mutableState.value = State.DeleteSuccess
            }.onFailure {
                it.printStackTrace()
                when(it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
            }
        }
    }

}