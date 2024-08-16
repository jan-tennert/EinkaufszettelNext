package io.github.jan.einkaufszettel.recipes.ui.steps

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getNavigatorScreenModel
import io.github.jan.einkaufszettel.getNavigatorScreenModelT
import io.github.jan.einkaufszettel.recipes.data.remote.RecipeApi
import io.github.jan.einkaufszettel.recipes.ui.create.RecipeCreateScreenModel
import io.github.jan.einkaufszettel.recipes.ui.edit.RecipeEditScreenModel
import io.github.jan.einkaufszettel.root.ui.component.LocalImage
import io.github.jan.supabase.storage.publicStorageItem
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import org.koin.core.parameter.parametersOf

class RecipeModifyS2Screen(
    private val oldRecipeId: Long? = null,
    private val oldRecipeImage: String? = null
): RecipeModifyStepScreen {

    override val index: Int = 1

    override val nextStep: RecipeModifyStepScreen = RecipeModifyS3Screen(oldRecipeId = oldRecipeId, oldRecipeImage = oldRecipeImage)

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = if(oldRecipeId != null) {
            navigator.parent!!.getNavigatorScreenModelT<RecipeEditScreenModel>(tag = oldRecipeId.toString(), parameters = { parametersOf(oldRecipeId) })
        } else {
            navigator.parent!!.getNavigatorScreenModel<RecipeCreateScreenModel>()
        }
        val imageData by model.imageData.collectAsStateWithLifecycle()
        val launcher = rememberFilePickerLauncher(mode = PickerMode.Single, type = PickerType.Image) {
            it?.let { file -> model.importNativeFile(file) }
        }
        Box(
            modifier = Modifier.padding(8.dp),
        ) {
            if(imageData == null && oldRecipeImage != null) {
                val request = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(publicStorageItem(RecipeApi.BUCKET_ID, oldRecipeImage))
                    .crossfade(true)
                    .build()
                AsyncImage(
                    model = request,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, MaterialTheme.colorScheme.onSurface)
                        .clickable { launcher.launch() }
                )
            } else {
                LocalImage(
                    imageData,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, MaterialTheme.colorScheme.onSurface)
                        .clickable { launcher.launch() }
                )
            }
        }
    }

}