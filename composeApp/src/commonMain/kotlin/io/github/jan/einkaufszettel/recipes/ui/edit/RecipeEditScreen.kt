package io.github.jan.einkaufszettel.recipes.ui.edit

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getNavigatorScreenModelT
import io.github.jan.einkaufszettel.recipes.ui.detail.RecipeDetailScreen
import io.github.jan.einkaufszettel.recipes.ui.steps.RecipeModifyS1Screen
import io.github.jan.einkaufszettel.recipes.ui.steps.RecipeModifyStepScreen
import io.github.jan.einkaufszettel.root.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.root.ui.dialog.LoadingDialog
import org.koin.core.parameter.parametersOf

class RecipeEditScreen(
    private val recipeId: Long
): AppStateScreen<RecipeEditScreenModel> {

    @Composable
    override fun createScreenModel(): RecipeEditScreenModel {
        val pNavigator = LocalNavigator.currentOrThrow
        return pNavigator.getNavigatorScreenModelT<RecipeEditScreenModel>(tag = recipeId.toString(), parameters = { parametersOf(recipeId) })
    }

    @Composable
    override fun Content(screenModel: RecipeEditScreenModel, state: AppState) {
        val pNavigator = LocalNavigator.currentOrThrow
        val recipe by screenModel.recipe.collectAsStateWithLifecycle()
        if(recipe == null) {
            LoadingCircle()
            return
        }
        LaunchedEffect(Unit) {
            screenModel.setName(recipe!!.name)
            screenModel.ingredients.addAll(recipe!!.ingredients)
            recipe!!.steps?.let { screenModel.instructionState.setHtml(it) }
        }
        Navigator(RecipeModifyS1Screen(recipe!!.id, recipe!!.imagePath)) { navigator ->
            val currentStep = navigator.lastItem as RecipeModifyStepScreen
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            if(currentStep.nextStep != null) {
                                navigator.push(currentStep.nextStep!!)
                            } else {
                                screenModel.editRecipe(
                                    name = screenModel.name.value,
                                    steps = screenModel.instructionState.toHtml(),
                                    ingredients = screenModel.ingredients.toList(),
                                    imageData = screenModel.imageData.value,
                                    oldImagePath = recipe!!.imagePath
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
            is RecipeEditScreenModel.State.Success -> {
                RecipeEditedDialog {
                    screenModel.resetState()
                    screenModel.resetContent()
                    pNavigator.replace(RecipeDetailScreen(state.id))
                }
            }
        }

    }

    @Composable
    private fun RecipeEditedDialog(
        onClose: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(Res.string.recipe_edited) },
            text = { Text(Res.string.recipe_edited_message) },
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