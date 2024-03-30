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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
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
        Navigator(RecipeCreateS1Screen) { navigator ->
            val currentStep = navigator.lastItem as RecipeCreateStepScreen
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
                                pNavigator.pop()
                            }
                        },
                    ) {
                        Icon(if(currentStep.nextStep != null) Icons.AutoMirrored.Filled.ArrowForward else Icons.Filled.Done, contentDescription = Res.string.create)
                    }
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(Res.string.create_recipe, style = MaterialTheme.typography.headlineLarge)
                    SlideTransition(navigator) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            it.Content()
                        }
                    }
                }
            }
        }

        when(state) {
            is AppState.Loading -> {
                LoadingDialog()
            }
            is RecipeCreateScreenModel.State.Success -> {
                AlertDialog(
                    onDismissRequest = {
                        pNavigator.pop()
                    },
                    title = { Text(Res.string.recipe_created) },
                    text = { Text(Res.string.recipe_created_message) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                pNavigator.pop()
                            }
                        ) {
                            Text("Ok")
                        }
                    }
                )
            }
        }

    }

}