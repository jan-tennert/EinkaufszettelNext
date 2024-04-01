package io.github.jan.einkaufszettel.recipes.ui.edit

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.recipes.data.remote.RecipeApi
import io.github.jan.einkaufszettel.recipes.ui.steps.RecipeModifyScreenModel
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageData
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageReader
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class RecipeEditScreenModel(
    private val recipeId: Long,
    recipeApi: RecipeApi,
    recipeDataSource: RecipeDataSource,
    localImageReader: LocalImageReader,
    auth: Auth,
): RecipeModifyScreenModel(recipeApi, recipeDataSource, localImageReader, auth) {

    sealed interface State: AppState {
        data class Success(val id: Long) : State
    }

    val recipe = recipeDataSource.getRecipeById(recipeId).stateIn(screenModelScope, SharingStarted.Eagerly, null)

    fun editRecipe(
        name: String?,
        imageData: LocalImageData?,
        oldImagePath: String?,
        steps: String?,
        ingredients: List<String>?,
    ) {
        mutableState.value = AppState.Loading
        screenModelScope.launch {
            runCatching {
                val imagePath = imageData?.let {
                    val imagePath = "${Clock.System.now().toEpochMilliseconds()}.${imageData.extension}"
                    recipeApi.uploadImage(imagePath, imageData.data)
                    imagePath
                }
                if(oldImagePath != null && imageData != null) {
                    recipeApi.deleteImage(oldImagePath)
                }
                recipeApi.editRecipe(recipeId, name, imagePath, ingredients, steps)
            }.onSuccess {
                recipeDataSource.insertRecipe(it)
                mutableState.value = State.Success(it.id)
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