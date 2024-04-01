package io.github.jan.einkaufszettel.cards.ui.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppScreenModel
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
import io.github.jan.einkaufszettel.app.ui.BlankScreen
import io.github.jan.einkaufszettel.app.ui.components.CreateButton
import io.github.jan.einkaufszettel.app.ui.components.DeleteDialog
import io.github.jan.einkaufszettel.app.ui.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.cards.ui.components.CardCard
import io.github.jan.einkaufszettel.cards.ui.screen.create.CardsCreateScreen
import io.github.jan.einkaufszettel.cards.ui.screen.detail.CardDetailScreen
import io.github.jan.einkaufszettel.cards.ui.screen.edit.CardEditScreen
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.root.ui.dialog.LoadingDialog
import io.github.jan.einkaufszettel.shops.ui.components.VerticalDivider
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

object CardsScreen : AppStateScreen<CardsScreenModel> {

    @Composable
    override fun createScreenModel(): CardsScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun Content(screenModel: CardsScreenModel, state: AppState) {
        val showDeleteDialog by screenModel.showDeleteDialog.collectAsStateWithLifecycle()
        Navigator(BlankScreen) { navigator ->
            RefreshScope(navigator.parent!!, AppScreenModel.RefreshType.CARDS) {
                Row {
                    val listState = rememberLazyGridState()
                    Scaffold(
                        modifier = Modifier.fillMaxHeight().weight(1f),
                        floatingActionButton = {
                            if (navigator.lastItem !is CardsCreateScreen && navigator.lastItem !is CardDetailScreen) {
                                CreateButton(
                                    extended = listState.firstVisibleItemIndex == 0,
                                    onClick = {
                                        if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                                            navigator.parent!!.push(CardsCreateScreen)
                                        } else {
                                            navigator.replace(CardsCreateScreen)
                                        }
                                    }
                                )
                            }
                        }
                    ) {
                        CardGrid(
                            screenModel = screenModel,
                            listState = listState,
                            navigator = navigator
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

        if (showDeleteDialog != null) {
            DeleteDialog(
                title = Res.string.delete_card,
                text = Res.string.delete_card_text,
                onDismiss = { screenModel.showDeleteDialog(null) },
                onDelete = {
                    screenModel.deleteCard(showDeleteDialog!!.id, showDeleteDialog!!.imagePath)
                }
            )
        }

        when(state) {
            is AppState.Loading -> {
                LoadingDialog()
            }
            is CardsScreenModel.State.DeleteSuccess -> {
                CardDeletedDialog {
                    screenModel.resetState()
                }
            }
        }
    }

    @Composable
    private fun CardDeletedDialog(
        onClose: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(Res.string.card_deleted) },
            text = { Text(Res.string.card_deleted_message) },
            confirmButton = {
                TextButton(
                    onClick = onClose
                ) {
                    Text("Ok")
                }
            }
        )
    }

    @Composable
    private fun CardGrid(screenModel: CardsScreenModel, listState: LazyGridState, navigator: Navigator) {
        val cards by screenModel.cards.collectAsStateWithLifecycle()
        val userId by screenModel.userIdFlow.collectAsStateWithLifecycle()
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Adaptive(150.dp)
        ) {
            items(cards, { it.id }) { card ->
                CardCard(
                    card = card,
                    modifier = Modifier.size(150.dp).padding(8.dp),
                    isOwner = card.ownerId == userId,
                    onClick = {
                        if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                            navigator.parent!!.push(CardDetailScreen(card.id))
                        } else {
                            navigator.replace(CardDetailScreen(card.id))
                        }
                    },
                    onEdit = {
                        if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                            navigator.parent!!.push(CardEditScreen(card.id))
                        } else {
                            navigator.replace(CardEditScreen(card.id))
                        }
                    },
                    onDelete = {
                        screenModel.showDeleteDialog(card)
                    }
                )
            }
        }
    }
}

