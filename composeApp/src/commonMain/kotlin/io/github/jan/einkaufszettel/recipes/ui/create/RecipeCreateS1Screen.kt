package io.github.jan.einkaufszettel.recipes.ui.create

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.recipes.ui.create.components.RichTextStyleRow

object RecipeCreateS1Screen: RecipeCreateStepScreen {

    override val index: Int = 0

    override val nextStep: RecipeCreateStepScreen = RecipeCreateS2Screen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.parent!!.getNavigatorScreenModel<RecipeCreateScreenModel>()
        val name by model.name.collectAsStateWithLifecycle()
        OutlinedTextField(
            value = name,
            onValueChange = model::setName,
            label = { Text(Res.string.name) },
            singleLine = true,
        )
        RichTextStyleRow(
            state = model.instructionState,
            showLinkDialog = {}
        )
        OutlinedRichTextEditor(
            state = model.instructionState,
            label = { Text(Res.string.instructions) },
            modifier = Modifier.fillMaxHeight().padding(bottom = 16.dp)
        )
    }

}