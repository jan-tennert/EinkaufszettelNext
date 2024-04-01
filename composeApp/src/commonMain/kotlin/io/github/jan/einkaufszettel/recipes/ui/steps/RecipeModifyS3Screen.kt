package io.github.jan.einkaufszettel.recipes.ui.steps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getNavigatorScreenModelT
import io.github.jan.einkaufszettel.recipes.ui.create.RecipeCreateScreenModel
import io.github.jan.einkaufszettel.recipes.ui.edit.RecipeEditScreenModel
import org.koin.core.parameter.parametersOf

class RecipeModifyS3Screen(
    private val oldRecipeId: Long? = null,
    private val oldRecipeImage: String? = null
): RecipeModifyStepScreen {

    override val index: Int = 2
    override val nextStep: RecipeModifyStepScreen? = null

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = if(oldRecipeId != null) {
            navigator.parent!!.getNavigatorScreenModelT<RecipeEditScreenModel>(tag = oldRecipeId.toString(), parameters = { parametersOf(oldRecipeId) })
        } else {
            navigator.parent!!.getNavigatorScreenModel<RecipeCreateScreenModel>()
        }
        val showIngredientDialog by model.showIngredientsDialog.collectAsStateWithLifecycle()
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(Res.string.ingredients, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.Center))
            IconButton(onClick = { model.setShowIngredientsDialog(true) }, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(Icons.Filled.Add, Res.string.add)
            }
        }
        if(model.ingredients.isEmpty()) {
            Text(Res.string.no_ingredients, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(8.dp)
            ) {
                model.ingredients.forEach {  //no items because there could be duplicates
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            ListItem(
                                headlineContent = { Text(it) },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                trailingContent = {
                                    IconButton(onClick = { model.ingredients.remove(it) }) {
                                        Icon(Icons.Filled.Delete, Res.string.delete)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if(showIngredientDialog) {
            IngredientDialog(
                onAdd = model.ingredients::add,
                close = { model.setShowIngredientsDialog(false) }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun IngredientDialog(
        onAdd: (String) -> Unit,
        close: () -> Unit
    ) {
        var ingredient by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = close,
            title = { Text(Res.string.add_ingredient) },
            text = {
                OutlinedTextField(
                    value = ingredient,
                    onValueChange = { ingredient = it },
                    label = { Text(Res.string.ingredient) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onAdd(ingredient)
                    ingredient = ""
                    close()
                }) {
                    Text(Res.string.add)
                }
            },
        )
    }

}
