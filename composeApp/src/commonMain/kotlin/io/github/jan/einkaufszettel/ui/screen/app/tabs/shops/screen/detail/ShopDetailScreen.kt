package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.dialog.ErrorDialog
import io.github.jan.einkaufszettel.ui.screen.app.AppScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.ui.screen.app.pullrefresh.pullRefresh
import io.github.jan.einkaufszettel.ui.screen.app.pullrefresh.rememberPullRefreshState
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.ProductCard
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.dialog.ProductDialog
import org.koin.core.parameter.parametersOf

class ShopDetailScreen(val id: Long): Screen {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ShopDetailScreenModel>(parameters = { parametersOf(id) })
        val products by screenModel.productFlow.collectAsStateWithLifecycle()
        val screenModelState by screenModel.state.collectAsStateWithLifecycle()
        var showCreateDialog by remember { mutableStateOf(false) }
        val listState = rememberLazyListState()
        RefreshScope(LocalNavigator.currentOrThrow.parent!!) {
            Scaffold(
                floatingActionButton = {
                    CreateButton(
                        extended = listState.firstVisibleItemIndex == 0,
                        onClick = { showCreateDialog = true }
                    )
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = listState
                ) {
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

        if(showCreateDialog) {
            ProductDialog(
                onDismiss = { showCreateDialog = false },
                onSubmit = { showCreateDialog = false; screenModel.createProduct(it) }
            )
        }

        when(screenModelState) {
            is ShopDetailScreenModel.State.Error -> {
                ErrorDialog((screenModelState as ShopDetailScreenModel.State.Error).message, screenModel::resetState)
            }
            ShopDetailScreenModel.State.NetworkError -> {
                ErrorDialog(Res.string.network_error, screenModel::resetState)
            }
            else -> {}
        }
    }

    @Composable
    private fun CreateButton(extended: Boolean, onClick: () -> Unit) {
        ExtendedFloatingActionButton(
            expanded = extended,
            text = { Text(Res.string.create) },
            icon = { Icon(Icons.Filled.Add, null) },
            onClick = onClick
        )
    }

}