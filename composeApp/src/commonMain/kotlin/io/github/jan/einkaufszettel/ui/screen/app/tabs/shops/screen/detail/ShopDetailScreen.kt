package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.ProductCard
import org.koin.core.parameter.parametersOf

class ShopDetailScreen(val id: Long): Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ShopDetailScreenModel>(parameters = { parametersOf(id) })
        val products by screenModel.productFlow.collectAsStateWithLifecycle()


        LaunchedEffect(Unit) {
            screenModel.refreshProducts(true)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(products, { it.id }) {
                ProductCard(
                    product = it,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            }
        }
    }

}