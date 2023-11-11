package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import einkaufszettel.GetAllRecipes
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.data.remote.ShopDto
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.ui.screen.app.AppStateErrorHandler
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.components.IngredientDetailContent
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.components.StepDetailContent
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.dialog.ProductDialog
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.ShopProductScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.main.ShopScreenModel
import org.koin.core.parameter.parametersOf

typealias Ingredient = Pair<Long, String>

class RecipeDetailScreen(
    private val recipeId: Long
): Screen {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<RecipeDetailScreenModel>(tag = recipeId.toString(), parameters = { parametersOf(recipeId) })
        val shopScreenModel = getScreenModel<ShopScreenModel>()
        val shopProductScreenModel = getScreenModel<ShopProductScreenModel>()
        val shopProductState by shopProductScreenModel.state.collectAsStateWithLifecycle()
        val recipe by screenModel.recipeFlow.collectAsStateWithLifecycle()
        val shops by shopScreenModel.shopFlow.collectAsStateWithLifecycle()
        if(recipe == null) {
            LoadingCircle()
            return
        }

        var ingredientDialog by remember { mutableStateOf<Ingredient?>(null) }
        val windowSizeClass = calculateWindowSizeClass()
        when(windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Medium, WindowWidthSizeClass.Compact -> {
                CompactContent(
                    recipe = recipe!!,
                    shops = shops,
                    onAdd = { shopId, content -> ingredientDialog = Ingredient(shopId, content) }
                )
            }
            WindowWidthSizeClass.Expanded -> {
                ExpandedContent(
                    recipe = recipe!!,
                    shops = shops,
                    onAdd = { shopId, content -> ingredientDialog = Ingredient(shopId, content) }
                )
            }
        }

        if(ingredientDialog != null) {
            val (shopId, ingredient) = ingredientDialog!!
            ProductDialog(
                oldContent = ingredient,
                onDismiss = { ingredientDialog = null },
                onSubmit = { content -> shopProductScreenModel.createProduct(shopId, content); ingredientDialog = null }
            )
        }

        AppStateErrorHandler(
            state = shopProductState,
            resetState = shopProductScreenModel::resetState,
        )
    }

    @Composable
    private fun CompactContent(
        recipe: GetAllRecipes,
        shops: List<ShopDto>,
        onAdd: (shop: Long, content: String) -> Unit = { _, _ -> }
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            item {
                Text(recipe.name, style = MaterialTheme.typography.headlineLarge)
            }
            if(!recipe.steps.isNullOrBlank()) {
                StepDetailContent(steps = recipe.steps)
            }
            if(recipe.ingredients.isNotEmpty()) {
                IngredientDetailContent(
                    ingredients = recipe.ingredients,
                    onAdd = onAdd,
                    shops = shops
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ExpandedContent(
        recipe: GetAllRecipes,
        shops: List<ShopDto>,
        onAdd: (shop: Long, content: String) -> Unit = { _, _ -> }
    ) {
        RecipeDetailScreenExpanded(
            recipe = recipe,
            shops = shops,
            onAdd = onAdd
        )
    }

}