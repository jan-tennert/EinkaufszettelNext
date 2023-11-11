package io.github.jan.einkaufszettel.ui.screen.app.tabs.home

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
import androidx.compose.material.icons.filled.Home
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
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.screen.app.AppStateErrorHandler
import io.github.jan.einkaufszettel.ui.screen.app.tabs.components.ProductCard

data object HomeTab: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val painter = rememberVectorPainter(Icons.Filled.Home)
            return remember {
                TabOptions(0u, Res.string.home, painter)
            }
        }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val windowSizeClass = calculateWindowSizeClass()
        val screenModel = getScreenModel<HomeScreenModel>()
        val screenModelState by screenModel.state.collectAsStateWithLifecycle()
        val shopAndProducts by screenModel.shopAndProductFlow.collectAsStateWithLifecycle()
        when(windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> CompactContent(shopAndProducts, screenModel)
            WindowWidthSizeClass.Expanded -> ExpandedContent(shopAndProducts, screenModel)
        }

        AppStateErrorHandler(
            state = screenModelState,
            resetState = screenModel::resetState,
        )
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
                    ListHeader(shop.name, Modifier.clickable { screenModel.changeShopCollapse(shop.id, !shop.collapsed) }.animateItemPlacement())
                }
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
                        ListHeader(shop.name)
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

    @Composable
    private fun ListHeader(name: String, modifier: Modifier = Modifier) {
        Box(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).then(modifier),
            contentAlignment = Alignment.Center
        ) {
            Text(name, style = MaterialTheme.typography.headlineSmall)
        }
    }

}