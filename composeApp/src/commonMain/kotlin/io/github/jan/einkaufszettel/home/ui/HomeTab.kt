package io.github.jan.einkaufszettel.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
import io.github.jan.einkaufszettel.app.ui.components.ProductCard
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle

data object HomeTab: Tab, AppStateScreen<HomeScreenModel> {

    override val options: TabOptions
        @Composable
        get() {
            val painter = rememberVectorPainter(Icons.Filled.Home)
            return remember {
                TabOptions(0u, Res.string.home, painter)
            }
        }

    @Composable
    override fun createScreenModel(): HomeScreenModel {
        return getScreenModel<HomeScreenModel>()
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content(screenModel: HomeScreenModel, state: AppState) {
        val windowSizeClass = calculateWindowSizeClass()
        val shopAndProducts by screenModel.shopAndProductFlow.collectAsStateWithLifecycle()
        when(windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> CompactContent(shopAndProducts, screenModel)
            WindowWidthSizeClass.Expanded -> ExpandedContent(shopAndProducts, screenModel)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun CompactContent(
        shopAndProducts: List<ShopAndProduct>,
        screenModel: HomeScreenModel
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            shopAndProducts.forEach { (shop, products) ->
                stickyHeader {
                    ListHeader(
                        name = shop.name,
                        isPinned = shop.pinned,
                        modifier = Modifier.clickable { screenModel.changeShopCollapse(shop.id, !shop.collapsed) }.animateItemPlacement(),
                        onPin = { screenModel.changeShopPinned(shop.id, !shop.pinned) }
                    )
                }
                if(!shop.collapsed) {
                    items(products, { it.id }) {
                        ProductCard(
                            product = it,
                            modifier = Modifier.fillMaxWidth().padding(8.dp).animateItemPlacement(),
                            onDoneChange = { done -> screenModel.changeDoneStatus(it.id, done) },
                            onDelete = { screenModel.deleteProduct(it.id) },
                            onEdit = { content -> screenModel.editContent(it.id, content) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ExpandedContent(
        shopAndProducts: List<ShopAndProduct>,
        screenModel: HomeScreenModel
    ) {
        Row {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(shopAndProducts, { it.first.id }) { (shop, products) ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ListHeader(shop.name, shop.pinned, onPin = { screenModel.changeShopPinned(shop.id, !shop.pinned) })
                        if(!shop.collapsed) {
                            products.forEach {
                                ProductCard(
                                    product = it,
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    onDoneChange = { done -> screenModel.changeDoneStatus(it.id, done) },
                                    onDelete = { screenModel.deleteProduct(it.id) },
                                    onEdit = { content -> screenModel.editContent(it.id, content) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ListHeader(name: String, isPinned: Boolean, modifier: Modifier = Modifier, onPin: () -> Unit = {}) {
        Box(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).then(modifier),
            contentAlignment = Alignment.Center
        ) {
            Text(name, style = MaterialTheme.typography.headlineSmall)
            IconButton(
                onClick = onPin,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                if (!isPinned) {
                    Icon(Icons.Filled.BookmarkAdd, null)
                } else {
                    Icon(
                        Icons.Filled.Bookmark,
                        null,
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }
        }
    }

}