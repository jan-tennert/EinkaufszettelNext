package io.github.jan.einkaufszettel.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import io.github.jan.einkaufszettel.data.local.image.LocalImageData

@Composable
fun LocalImage(data: LocalImageData?, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if(data != null) {
            AutoSizeBox(
                request = remember(data) {
                    ImageRequest(data)
                },
                modifier = Modifier.matchParentSize(),
            ) {
                when(it) {
                    is ImageAction.Loading -> {
                        LoadingCircle()
                    }
                    is ImageAction.Failure -> {
                        it.error.printStackTrace()
                    }
                    is ImageAction.Success -> {
                        Image(
                            rememberImageSuccessPainter(it),
                            modifier = Modifier.matchParentSize(),
                            contentDescription = null
                        )
                    }
                }
            }
        } else {
            Icon(Icons.Filled.QuestionMark, modifier = Modifier.matchParentSize(), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

