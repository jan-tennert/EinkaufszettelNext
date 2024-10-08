package io.github.jan.einkaufszettel.recipes.ui.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
import io.github.jan.einkaufszettel.app.ui.components.ChildTopBar
import io.github.jan.einkaufszettel.getNavigatorScreenModel
import io.github.jan.einkaufszettel.recipes.ui.detail.RecipeDetailScreen
import io.github.jan.einkaufszettel.recipes.ui.steps.RecipeModifyS1Screen
import io.github.jan.einkaufszettel.recipes.ui.steps.RecipeModifyStepScreen
import io.github.jan.einkaufszettel.root.ui.dialog.LoadingDialog

object RecipeCreateScreen: AppStateScreen<RecipeCreateScreenModel> {

    @Composable
    override fun createScreenModel(): RecipeCreateScreenModel {
        val pNavigator = LocalNavigator.currentOrThrow
        return pNavigator.getNavigatorScreenModel<RecipeCreateScreenModel>()
    }

    @Composable
    override fun Content(screenModel: RecipeCreateScreenModel, state: AppState) {
        val pNavigator = LocalNavigator.currentOrThrow
        Navigator(RecipeModifyS1Screen(null)) { navigator ->
            val currentStep = navigator.lastItem as RecipeModifyStepScreen
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            if(currentStep.nextStep != null) {
                                navigator.push(currentStep.nextStep!!)
                            } else {
                                screenModel.createRecipe(
                                    name = screenModel.name.value,
                                    steps = screenModel.instructionState.toHtml(),
                                    ingredients = screenModel.ingredients.toList(),
                                    imageData = screenModel.imageData.value,
                                    private = false
                                )
                            }
                        },
                    ) {
                        Icon(if(currentStep.nextStep != null) Icons.AutoMirrored.Filled.ArrowForward else Icons.Filled.Done, contentDescription = Res.string.create)
                    }
                },
                topBar = {
                    ChildTopBar(Res.string.create_recipe, pNavigator)
                }
            ) {
                SlideTransition(navigator, Modifier.padding(it)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        it.Content()
                    }
                }
            }
        }

        when(state) {
            is AppState.Loading -> {
                LoadingDialog()
            }
            is RecipeCreateScreenModel.State.Success -> {
                RecipeCreatedDialog {
                    screenModel.resetState()
                    screenModel.resetContent()
                    pNavigator.replace(RecipeDetailScreen(state.id))
                }
            }
        }

    }

    @Composable
    private fun RecipeCreatedDialog(
        onClose: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(Res.string.recipe_created) },
            text = { Text(Res.string.recipe_created_message) },
            confirmButton = {
                TextButton(
                    onClick = onClose
                ) {
                    Text("Ok")
                }
            }
        )
    }

}