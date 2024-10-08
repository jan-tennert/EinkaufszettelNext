package io.github.jan.einkaufszettel.shops.ui.screen.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppScreenModel
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
import io.github.jan.einkaufszettel.app.ui.components.ChildTopBar
import io.github.jan.einkaufszettel.app.ui.components.CreateButton
import io.github.jan.einkaufszettel.app.ui.components.ProductCard
import io.github.jan.einkaufszettel.app.ui.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getScreenModelWT
import io.github.jan.einkaufszettel.shops.ui.dialog.ProductDialog
import org.koin.core.parameter.parametersOf

data class ShopDetailStateScreen(val id: Long): AppStateScreen<ShopDetailScreenModel> {

    @Composable
    override fun createScreenModel(): ShopDetailScreenModel {
        return getScreenModelWT<ShopDetailScreenModel>(parameters = { parametersOf(id) }, tag = id.toString())
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content(screenModel: ShopDetailScreenModel, state: AppState) {
        val products by screenModel.productFlow.collectAsStateWithLifecycle()
        val shop = screenModel.shopFlow.collectAsStateWithLifecycle()
        var showCreateDialog by remember { mutableStateOf(false) }
        val listState = rememberLazyListState()
        val navigator = LocalNavigator.currentOrThrow
        RefreshScope(LocalNavigator.currentOrThrow.parent!!, AppScreenModel.RefreshType.PRODUCTS) {
            Scaffold(
                floatingActionButton = {
                    CreateButton(
                        extended = listState.firstVisibleItemIndex == 0,
                        onClick = { showCreateDialog = true }
                    )
                },
                topBar = {
                    ChildTopBar(
                        title = shop.value?.name ?: "",
                        navigator = navigator
                    )
                }
            ) {
                LazyColumn(
                    modifier = Modifier.padding(it).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = listState
                ) {
                    item {
                        if(products.isEmpty()) {
                            Text(Res.string.empty_list, style = MaterialTheme.typography.bodyMedium)
                        }
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

        if(showCreateDialog) {
            ProductDialog(
                onDismiss = { showCreateDialog = false },
                onSubmit = { showCreateDialog = false; screenModel.createProduct(it) }
            )
        }
    }

}