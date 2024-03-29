package io.github.jan.einkaufszettel.root.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageData

@Composable
fun LocalImage(data: LocalImageData?, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if(data != null) {
            AsyncImage(
                model = data,
                modifier = Modifier.matchParentSize(),
                contentDescription = null
            )
        } else {
            Icon(Icons.Filled.QuestionMark, modifier = Modifier.matchParentSize(), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

