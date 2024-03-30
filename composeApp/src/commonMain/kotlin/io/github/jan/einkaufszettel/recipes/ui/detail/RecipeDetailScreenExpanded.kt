package io.github.jan.einkaufszettel.recipes.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mohamedrejeb.richeditor.model.RichTextState
import einkaufszettel.GetAllRecipes
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.recipes.ui.detail.components.IngredientContent
import io.github.jan.einkaufszettel.recipes.ui.detail.components.StepCard
import io.github.jan.einkaufszettel.shops.data.remote.ShopDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreenExpanded(
    recipe: GetAllRecipes,
    shops: List<ShopDto>,
    onAdd: (shop: Long, content: String) -> Unit = { _, _ -> }
) {
    val navigator = LocalNavigator.currentOrThrow
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(recipe.name, style = MaterialTheme.typography.headlineLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Row(
            modifier = Modifier.padding(it).fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            if (recipe.steps != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(Res.string.steps, style = MaterialTheme.typography.headlineSmall)
                    val stepState = rememberSaveable(saver = RichTextState.Saver, inputs = arrayOf(recipe.steps)) {
                        RichTextState().apply {
                            setHtml(recipe.steps.ifBlank { Res.string.empty_steps })
                        }
                    }
                    StepCard(
                        steps = stepState,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(Res.string.ingredients, style = MaterialTheme.typography.headlineSmall)
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(recipe.ingredients) { ingredient ->
                        IngredientContent(
                            ingredient = ingredient,
                            onAdd = onAdd,
                            shops = shops,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}