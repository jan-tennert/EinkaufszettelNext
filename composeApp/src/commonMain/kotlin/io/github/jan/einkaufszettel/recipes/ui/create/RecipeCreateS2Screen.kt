package io.github.jan.einkaufszettel.recipes.ui.create

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.root.ui.component.LocalImage

object RecipeCreateS2Screen : RecipeCreateStepScreen {

    override val index: Int = 1

    override val nextStep: RecipeCreateStepScreen = RecipeCreateS3Screen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.parent!!.getNavigatorScreenModel<RecipeCreateScreenModel>()
        val imageData by model.imageData.collectAsStateWithLifecycle()
        var showImageDialog by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier.padding(8.dp),
        ) {
            LocalImage(
                imageData,
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, MaterialTheme.colorScheme.onSurface)
                    .clickable { showImageDialog = true }
            )
        }
        FilePicker(
            show = showImageDialog,
            fileExtensions = listOf("png", "jpg", "jpeg"),
            onFileSelected = {
                showImageDialog = false
                it?.platformFile?.let { file -> model.importNativeFile(file) }
            }
        )
    }

}