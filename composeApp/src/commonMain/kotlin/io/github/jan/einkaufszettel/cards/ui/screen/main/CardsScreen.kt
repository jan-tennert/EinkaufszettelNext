package io.github.jan.einkaufszettel.cards.ui.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppScreenModel
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
import io.github.jan.einkaufszettel.app.ui.components.CreateButton
import io.github.jan.einkaufszettel.app.ui.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.cards.ui.screen.create.CardCreateScreen
import io.github.jan.einkaufszettel.cards.ui.screen.detail.CardDetailScreen
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.shops.ui.components.VerticalDivider
import io.github.jan.einkaufszettel.shops.ui.screen.main.BlankScreen
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

object CardsScreen : AppStateScreen<CardsScreenModel> {

    @Composable
    override fun createScreenModel(): CardsScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun Content(screenModel: CardsScreenModel, state: AppState) {
        Navigator(BlankScreen) { navigator ->
            RefreshScope(navigator.parent!!, AppScreenModel.RefreshType.CARDS) {
                Row {
                    val listState = rememberLazyGridState()
                    Scaffold(
                        modifier = Modifier.fillMaxHeight().weight(1f),
                        floatingActionButton = {
                            if (navigator.lastItem !is CardCreateScreen && navigator.lastItem !is CardDetailScreen) {
                                CreateButton(
                                    extended = listState.firstVisibleItemIndex == 0,
                                    onClick = {
                                        if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                                            navigator.parent!!.push(CardCreateScreen)
                                        } else {
                                            navigator.replace(CardCreateScreen)
                                        }
                                    }
                                )
                            }
                        }
                    ) {
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

        /*if (showDeleteDialog != null) {
            //
        }*/
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