package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.screen.app.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.ShopScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.ShopCard
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.ShopCardDefaults
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail.ShopDetailScreen
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

object ShopScreen: Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.getNavigatorScreenModel<ShopScreenModel>()
        val shops by screenModel.shopFlow.collectAsStateWithLifecycle()
        var sideBar by remember { mutableStateOf<Screen?>(null) }

        RefreshScope(navigator) {
            Row {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(ShopCardDefaults.SIZE),
                    modifier = Modifier.fillMaxHeight().weight(1f)
                ) {
                    items(shops, { it.id }) {
                        ShopCard(
                            shop = it,
                            modifier = Modifier.size(ShopCardDefaults.SIZE).padding(ShopCardDefaults.PADDING),
                            onClick = {
                                if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                                    navigator.push(ShopDetailScreen(it.id))
                                } else {
                                    sideBar = ShopDetailScreen(it.id)
                                }
                            },
                            onEdit = {},
                            onDelete = {}
                        )
                    }
                }
                sideBar?.let {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(0.3f)) {
                        sideBar!!.Content()
                    }
                }
            }
        }
    }

}