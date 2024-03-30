package io.github.jan.einkaufszettel.recipes.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppScreenModel
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
import io.github.jan.einkaufszettel.app.ui.components.CreateButton
import io.github.jan.einkaufszettel.app.ui.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.recipes.ui.components.RecipeCard
import io.github.jan.einkaufszettel.recipes.ui.components.RecipeCardDefaults
import io.github.jan.einkaufszettel.recipes.ui.create.RecipeCreateScreen
import io.github.jan.einkaufszettel.recipes.ui.detail.RecipeDetailScreen
import io.github.jan.einkaufszettel.root.ui.dialog.LoadingDialog
import io.github.jan.einkaufszettel.shops.ui.components.VerticalDivider
import io.github.jan.einkaufszettel.shops.ui.screen.main.BlankScreen
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
                        }
                    ) {
                        RecipeList(screenModel, navigator.parent!!)
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
                text = {
                    Text(Res.string.recipe_deleted)
                }
            )
        } else if(state is AppState.Loading) {
            LoadingDialog()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun RecipeList(
        screenModel: RecipeScreenModel,
        navigator: Navigator
    ) {
        val recipes by screenModel.recipeFlow.collectAsStateWithLifecycle()
        val searchQuery by screenModel.searchQuery.collectAsStateWithLifecycle()
        val filteredRecipes by derivedStateOf {
            recipes.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
        val userId by screenModel.userIdFlow.collectAsStateWithLifecycle()
        val showDeleteDialog by screenModel.showDeleteDialog.collectAsStateWithLifecycle()
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DockedSearchBar(
                active = false,
                onActiveChange = {  },
                onSearch = {  },
                query = searchQuery,
                onQueryChange = screenModel::onSearchQueryChanged,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text(Res.string.search) },
                modifier = Modifier.fillMaxWidth()
            ) { }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(RecipeCardDefaults.WIDTH),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredRecipes, { it.id }) {
                    RecipeCard(
                        recipe = it,
                        isOwner = it.creatorId == userId,
                        modifier = Modifier.width(RecipeCardDefaults.WIDTH).height(
                            RecipeCardDefaults.HEIGHT).padding(RecipeCardDefaults.PADDING),
                        onClick = {
                            navigator.push(RecipeDetailScreen(it.id))
                        },
                        onDelete = {
                            screenModel.onShowDeleteDialogChanged(it)
                        }
                    )
                }
            }
        }

        if(showDeleteDialog != null) {
            DeleteDialog(
                onDismiss = { screenModel.onShowDeleteDialogChanged(null) },
                onDelete = {
                    screenModel.deleteRecipe(showDeleteDialog!!.id, showDeleteDialog?.imagePath)
                }
            )
        }
    }

    @Composable
    private fun DeleteDialog(
        onDismiss: () -> Unit,
        onDelete: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        onDismiss()
                    }
                ) {
                    Text(Res.string.delete)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(Res.string.cancel)
                }
            },
            title = {
                Text(Res.string.delete_recipe)
            },
            text = {
                Text(Res.string.delete_recipe_text)
            }
        )
    }


}