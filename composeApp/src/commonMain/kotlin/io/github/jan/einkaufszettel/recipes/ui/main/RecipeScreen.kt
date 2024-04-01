package io.github.jan.einkaufszettel.recipes.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppScreenModel
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
import io.github.jan.einkaufszettel.app.ui.BlankScreen
import io.github.jan.einkaufszettel.app.ui.components.CreateButton
import io.github.jan.einkaufszettel.app.ui.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.recipes.ui.create.RecipeCreateScreen
import io.github.jan.einkaufszettel.recipes.ui.create.components.RecipeList
import io.github.jan.einkaufszettel.recipes.ui.detail.RecipeDetailScreen
import io.github.jan.einkaufszettel.root.ui.dialog.LoadingDialog
import io.github.jan.einkaufszettel.shops.ui.components.VerticalDivider
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

object RecipeScreen : AppStateScreen<RecipeScreenModel> {

    @Composable
    override fun createScreenModel(): RecipeScreenModel {
        return getScreenModel<RecipeScreenModel>()
    }
    @Composable
    override fun Content(screenModel: RecipeScreenModel, state: AppState) {
        Navigator(BlankScreen) { navigator ->
            RefreshScope(navigator.parent!!, AppScreenModel.RefreshType.RECIPES) {
                Row {
                    val listState = rememberLazyGridState()
                    Scaffold(
                        modifier = Modifier.fillMaxHeight().weight(1f),
                        floatingActionButton = {
                            if(navigator.lastItem !is RecipeCreateScreen && navigator.lastItem !is RecipeDetailScreen) {
                                CreateButton(
                                    extended = listState.firstVisibleItemIndex == 0,
                                    onClick = {
                                        if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                                            navigator.parent!!.push(RecipeCreateScreen)
                                        } else {
                                            navigator.replace(RecipeCreateScreen)
                                        }
                                    }
                                )
                            }
                        },
                    ) {
                        RecipeList(screenModel, navigator.parent!!, listState)
                    }

                    AnimatedVisibility(
                        visible = navigator.lastItem !is BlankScreen,
                        enter = slideInHorizontally { it },
                        exit = slideOutHorizontally { it },
                    ) {
                        Row(
                            Modifier.fillMaxHeight().fillMaxWidth(0.3f)
                        ) {
                            VerticalDivider(Modifier.fillMaxHeight())
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                CurrentScreen()
                            }
                        }
                    }
                }
            }
        }

        if(state is RecipeScreenModel.State.DeleteSuccess) {
            AlertDialog(
                onDismissRequest = screenModel::resetState,
                confirmButton = {
                    TextButton(
                        onClick = screenModel::resetState
                    ) {
                        Text("Ok")
                    }
                },
                title = {
                    Text(Res.string.recipe_deleted)
                },
                text = {
                    Text(Res.string.recipe_deleted_message)
                }
            )
        } else if(state is AppState.Loading) {
            LoadingDialog()
        }
    }

}