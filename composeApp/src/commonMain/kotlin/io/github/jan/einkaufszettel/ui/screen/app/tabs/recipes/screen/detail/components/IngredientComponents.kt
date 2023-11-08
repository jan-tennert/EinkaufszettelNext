package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.data.remote.ShopDto

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun LazyListScope.IngredientDetailContent(
    ingredients: List<String>,
    shops: List<ShopDto>,
    onAdd: (shop: Long, content: String) -> Unit = { _, _ -> }
) {
    stickyHeader {
        Box(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(Res.string.ingredients, style = MaterialTheme.typography.headlineSmall)
        }
    }
    items(ingredients, { it }) { ingredient ->
        IngredientContent(
            ingredient = ingredient,
            onAdd = onAdd,
            shops = shops,
            modifier = Modifier.animateItemPlacement()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientContent(
    ingredient: String,
    modifier: Modifier = Modifier,
    onAdd: (shop: Long, content: String) -> Unit,
    shops: List<ShopDto>
) {
    var expandShops by rememberSaveable(key = ingredient) { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expandShops,
        onExpandedChange = { expandShops = it },
        content = {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(8.dp).menuAnchor().then(modifier)
            ) {
                ListItem(
                    headlineContent = { Text(ingredient) },
                    trailingContent = {
                        IconButton({ expandShops = true }) {
                            Icon(Icons.Filled.AddShoppingCart, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            ShopDropDown(
                expandShops = expandShops,
                onExpandChange = { expandShops = it },
                onSelect = { shop -> onAdd(shop.id, ingredient) },
                shops = shops
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxScope.ShopDropDown(
    expandShops: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onSelect: (ShopDto) -> Unit,
    shops: List<ShopDto>,
) {
    ExposedDropdownMenu(
        expanded = expandShops,
        onDismissRequest = { onExpandChange(false) },
    ) {
        Column {
            shops.forEach {
                ListItem(
                    headlineContent = { Text(it.name) },
                    modifier = Modifier.clickable {
                        onSelect(it)
                        onExpandChange(false)
                    }
                )
            }
        }
    }
}