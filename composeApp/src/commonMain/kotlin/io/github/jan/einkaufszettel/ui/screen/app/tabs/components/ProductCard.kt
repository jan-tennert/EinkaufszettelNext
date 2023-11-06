package io.github.jan.einkaufszettel.ui.screen.app.tabs.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import einkaufszettel.GetAllProducts
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.data.local.DateTimerFormatter
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.dialog.ProductDialog

@Composable
fun ProductCard(
    product: GetAllProducts,
    modifier: Modifier = Modifier,
    onDoneChange: (Boolean) -> Unit = {},
    onEdit: (String) -> Unit = {},
    onDelete: () -> Unit = {},
) {
    var showEditDialog by remember { mutableStateOf(false) }
    ElevatedCard(modifier) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isDone = product.doneSince != null || product.doneBy != null
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(24.dp)) {
                if(product.loading == 1L) {
                    CircularProgressIndicator(
                        Modifier
                            .matchParentSize())
                } else {
                    Checkbox(checked = isDone, modifier = Modifier
                        .matchParentSize(), onCheckedChange = onDoneChange)
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                val date = remember(product) { DateTimerFormatter.format(product.doneSince ?: product.createdAt) }
                Text(product.content, textDecoration = if(isDone) TextDecoration.LineThrough else null)
                Text("$date ${Res.string.by} ${(product.doneBy ?: product.creator) ?: Res.string.unknown}", fontSize = 10.sp)
            }
            if(isDone) {
                IconButton(onDelete, enabled = product.loading != 1L) {
                    Icon(Icons.Filled.Delete, null)
                }
            } else {
                IconButton({ showEditDialog = true }, enabled = product.loading != 1L) {
                    Icon(Icons.Filled.Edit, null)
                }
            }
        }
    }
    if(showEditDialog) {
        ProductDialog(
            oldContent = product.content,
            onDismiss = { showEditDialog = false },
            onSubmit = { showEditDialog = false; onEdit(it) }
        )
    }
}