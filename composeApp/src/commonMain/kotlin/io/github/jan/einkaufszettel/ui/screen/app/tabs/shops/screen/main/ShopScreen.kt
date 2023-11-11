package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.main

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.data.remote.ShopDto
import io.github.jan.einkaufszettel.ui.screen.app.AppScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.ui.screen.app.tabs.components.CreateButton
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.ShopCard
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.ShopCardDefaults
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.VerticalDivider
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.create.ShopCreateScreen
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail.ShopDetailScreen
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.edit.ShopEditScreen
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

object ShopScreen: Screen {

    @OptIn(ExperimentalVoyagerApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.getNavigatorScreenModel<ShopScreenModel>()
        val shops by screenModel.shopFlow.collectAsStateWithLifecycle()
        var showDeleteDialog by remember { mutableStateOf<Long?>(null) }
        val userIdFlow by screenModel.userIdFlow.collectAsStateWithLifecycle()
        Navigator(BlankScreen) { navigator ->
            RefreshScope(navigator.parent!!, AppScreenModel.RefreshType.SHOPS) {
                Row {
                    val listState = rememberLazyGridState()
                    Scaffold(
                        modifier = Modifier.fillMaxHeight().weight(1f),
                        floatingActionButton = {
                            if(navigator.lastItem !is ShopCreateScreen && navigator.lastItem !is ShopDetailScreen) {
                                CreateButton(
                                    extended = listState.firstVisibleItemIndex == 0,
                                    onClick = {
                                        if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                                            navigator.parent!!.push(ShopCreateScreen)
                                        } else {
                                            navigator.replace(ShopCreateScreen)
                                        }
                                    }
                                )
                            }
                        }
                    ) {
                        ShopGrid(
                            userId = userIdFlow,
                            gridState = listState,
                            shops = shops,
                            showDeleteDialog = { id -> showDeleteDialog = id },
                            onEdit = { id ->
                                if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                                    navigator.parent!!.push(ShopEditScreen(id))
                                } else {
                                    navigator.replace(ShopEditScreen(id))
                                }
                            },
                            modifier = Modifier.padding(it)
                        )
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

        if(showDeleteDialog != null) {
            DeleteDialog(
                onDismiss = { showDeleteDialog = null },
                onDelete = {
                    screenModel.deleteShop(showDeleteDialog!!)
                }
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ShopGrid(
        userId: String?,
        gridState: LazyGridState,
        shops: List<ShopDto>,
        showDeleteDialog: (Long) -> Unit,
        onEdit: (Long) -> Unit = {},
        modifier: Modifier = Modifier,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        LazyVerticalGrid(
            columns = GridCells.Adaptive(ShopCardDefaults.SIZE),
            modifier = modifier,
            state = gridState
        ) {
            items(shops, { shop -> shop.id }) { shop ->
                ShopCard(
                    shop = shop,
                    modifier = Modifier.size(ShopCardDefaults.SIZE).padding(ShopCardDefaults.PADDING).animateItemPlacement(),
                    onClick = {
                        if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                            navigator.parent!!.push(ShopDetailScreen(shop.id))
                        } else {
                            if (navigator.lastItem is ShopDetailScreen && (navigator.lastItem as ShopDetailScreen).id == shop.id) {
                                navigator.replace(BlankScreen)
                            } else {
                                navigator.replace(ShopDetailScreen(shop.id))
                            }
                        }
                    },
                    isOwner = userId == shop.ownerId,
                    onEdit = {
                        onEdit(shop.id)
                    },
                    onDelete = {
                        showDeleteDialog(shop.id)
                    }
                )
            }
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
            text = {
                Text(Res.string.delete_list_text)
            }
        )
    }

}