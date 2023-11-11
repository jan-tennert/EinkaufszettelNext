package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.AppScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.AppStateErrorHandler
import io.github.jan.einkaufszettel.ui.screen.app.pullrefresh.RefreshScope
import io.github.jan.einkaufszettel.ui.screen.app.tabs.components.CreateButton
import io.github.jan.einkaufszettel.ui.screen.app.tabs.components.ProductCard
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.dialog.ProductDialog
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.main.BlankScreen
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget
import org.koin.core.parameter.parametersOf

data class ShopDetailScreen(val id: Long): Screen {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ShopDetailScreenModel>(parameters = { parametersOf(id) }, tag = id.toString())
        val products by screenModel.productFlow.collectAsStateWithLifecycle()
        val screenModelState by screenModel.state.collectAsStateWithLifecycle()
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
                    TopBar(
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

        AppStateErrorHandler(
            state = screenModelState,
            resetState = screenModel::resetState
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar(title: String, navigator: Navigator) {
        CenterAlignedTopAppBar(
            title = { Text(title, style = MaterialTheme.typography.headlineMedium) },
            actions = {
                if(CurrentPlatformTarget == PlatformTarget.JS) {
                    IconButton(
                        onClick = { navigator.replace(BlankScreen) }
                    ) {
                        Icon(Icons.Filled.Close, null)
                    }
                }
            }
        )
    }

}