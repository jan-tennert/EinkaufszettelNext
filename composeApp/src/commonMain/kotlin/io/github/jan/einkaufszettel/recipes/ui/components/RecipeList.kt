package io.github.jan.einkaufszettel.recipes.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.components.DeleteDialog
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.recipes.ui.detail.RecipeDetailScreen
import io.github.jan.einkaufszettel.recipes.ui.edit.RecipeEditScreen
import io.github.jan.einkaufszettel.recipes.ui.main.RecipeScreenModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeList(
    screenModel: RecipeScreenModel,
    navigator: Navigator,
    listState: LazyGridState
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
            modifier = Modifier.weight(1f),
            state = listState
        ) {
            items(filteredRecipes, { it.id }) {
                RecipeCard(
                    recipe = it,
                    isOwner = it.creatorId == userId,
                    modifier = Modifier.width(RecipeCardDefaults.WIDTH).height(
                        RecipeCardDefaults.HEIGHT).padding(RecipeCardDefaults.PADDING),
                    onClick = {
                        navigator.push(RecipeDetailScreen(it.id)) //TODO: JS
                    },
                    onEdit = {
                        navigator.push(RecipeEditScreen(it.id))
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
            title = Res.string.delete_recipe,
            text = Res.string.delete_recipe_text,
            onDismiss = { screenModel.onShowDeleteDialogChanged(null) },
            onDelete = {
                screenModel.deleteRecipe(showDeleteDialog!!.id, showDeleteDialog?.imagePath)
            }
        )
    }
}