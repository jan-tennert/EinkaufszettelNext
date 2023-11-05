package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import einkaufszettel.GetAllProducts
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.data.local.DateTimerFormatter

@Composable
fun ProductCard(product: GetAllProducts, modifier: Modifier = Modifier) {
    ElevatedCard(modifier) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(product.doneSince != null, {})
            Column(Modifier.weight(1f)) {
                val date = remember(product) { DateTimerFormatter.format(product.doneSince ?: product.createdAt) }
                Text(product.content)
                Text("$date ${Res.string.by} ${(product.doneBy ?: product.creator) ?: Res.string.unknown}", fontSize = 10.sp)
            }
            IconButton({}) {
                Icon(Icons.Filled.Delete, null)
            }
        }
    }
}