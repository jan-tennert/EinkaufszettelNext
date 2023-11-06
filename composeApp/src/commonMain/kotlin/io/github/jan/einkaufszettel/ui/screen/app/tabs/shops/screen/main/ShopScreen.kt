package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.screen.app.AppScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.ShopScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.ShopCard
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.ShopCardDefaults
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.VerticalDivider
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail.ShopDetailScreen
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

object ShopScreen: Screen {

    @OptIn(ExperimentalVoyagerApi::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.getNavigatorScreenModel<ShopScreenModel>()
        val shops by screenModel.shopFlow.collectAsStateWithLifecycle()

        Navigator(BlankScreen) { navigator ->
            RefreshScope(navigator.parent!!, AppScreenModel.RefreshType.SHOPS) {
                Row {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(ShopCardDefaults.SIZE),
                        modifier = Modifier.fillMaxHeight().weight(1f)
                    ) {
                        items(shops, { it.id }) {
                            ShopCard(
                                shop = it,
                                modifier = Modifier.size(ShopCardDefaults.SIZE).padding(ShopCardDefaults.PADDING).animateItemPlacement(),
                                onClick = {
                                    if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                                        navigator.parent!!.push(ShopDetailScreen(it.id))
                                    } else {
                                        if(navigator.lastItem is BlankScreen) {
                                            navigator.push(ShopDetailScreen(it.id))
                                        } else if (navigator.lastItem is ShopDetailScreen && (navigator.lastItem as ShopDetailScreen).id == it.id) {
                                            navigator.push(BlankScreen)
                                        } else if (navigator.lastItem is ShopDetailScreen) {
                                            navigator.push(ShopDetailScreen(it.id))
                                        }
                                    }
                                },
                                onEdit = {},
                                onDelete = {}
                            )
                        }
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
    }



}