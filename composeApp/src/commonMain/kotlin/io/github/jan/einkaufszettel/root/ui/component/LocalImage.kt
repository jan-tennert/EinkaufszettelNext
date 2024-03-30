package io.github.jan.einkaufszettel.root.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import io.github.jan.einkaufszettel.Res
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
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(Icons.Filled.QuestionMark, modifier = Modifier.fillMaxSize(0.5f), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                Text(Res.string.add_image, textAlign = TextAlign.Center)
            }
        }
    }
}

