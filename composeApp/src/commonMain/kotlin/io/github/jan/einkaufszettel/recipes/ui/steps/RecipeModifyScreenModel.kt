package io.github.jan.einkaufszettel.recipes.ui.steps

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.screenModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import io.github.jan.einkaufszettel.app.ui.AppStateModel
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.recipes.data.remote.RecipeApi
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageData
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageReader
import io.github.jan.supabase.auth.Auth
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class RecipeModifyScreenModel(
    protected val recipeApi: RecipeApi,
    protected val recipeDataSource: RecipeDataSource,
    private val localImageReader: LocalImageReader,
    protected val auth: Auth,
): AppStateModel() {

    private val _imageData = MutableStateFlow<LocalImageData?>(null)
    private val _name = MutableStateFlow("")
    private val _showIngredientsDialog = MutableStateFlow(false)
    val ingredients = mutableStateListOf<String>()
    val name = _name.asStateFlow()
    val imageData = _imageData.asStateFlow()
    val instructionState = RichTextState()
    val showIngredientsDialog = _showIngredientsDialog.asStateFlow()

    fun importNativeFile(file: PlatformFile) {
        screenModelScope.launch {
            runCatching {
                localImageReader.platformFileToLocalImage(file)
            }.onSuccess {
                _imageData.value = it
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun setName(name: String) {
        _name.value = name
    }

    fun setShowIngredientsDialog(show: Boolean) {
        _showIngredientsDialog.value = show
    }

    fun resetContent() {
        _name.value = ""
        ingredients.clear()
        instructionState.clear()
        _showIngredientsDialog.value = false
    }

}