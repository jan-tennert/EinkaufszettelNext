package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.mohamedrejeb.richeditor.model.RichTextState
import einkaufszettel.GetAllRecipes
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.data.remote.ShopDto
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.ui.dialog.ErrorDialog
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.components.IngredientContent
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.components.IngredientDetailContent
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.components.StepCard
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.components.StepDetailContent
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.ShopScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.dialog.ProductDialog
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.ShopProductScreenModel
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

        when(shopProductState) {
            is ShopProductScreenModel.State.Error -> {
                ErrorDialog((shopProductState as ShopProductScreenModel.State.Error).message, shopProductScreenModel::resetState)
            }
            ShopProductScreenModel.State.NetworkError -> {
                ErrorDialog(Res.string.network_error, shopProductScreenModel::resetState)
            }
            else -> {}
        }
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