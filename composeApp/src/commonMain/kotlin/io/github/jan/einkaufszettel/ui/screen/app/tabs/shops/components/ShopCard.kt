package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageEvent
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult
import com.seiko.imageloader.option.Scale
import com.seiko.imageloader.option.SizeResolver
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import com.seiko.imageloader.ui.AutoSizeImage
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.data.remote.ShopDto
import io.github.jan.einkaufszettel.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.ui.screen.app.tabs.components.ContextMenuScope

object ShopCardDefaults {

    val SIZE = 150.dp
    val ICON_SIZE = 64.dp
    val PADDING = 8.dp
    val CONTENT_PADDING = 4.dp
    val IMAGE_SIZE = 256f

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShopCard(
    shop: ShopDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ContextMenuScope(
        onItemClicked = onClick,
        modifier = modifier,
        items = {
            DropdownMenuItem(
                text = { Text(Res.string.edit) },
                onClick = onEdit
            )
            DropdownMenuItem(
                text = { Text(Res.string.delete) },
                onClick = onDelete
            )
        },
        content = {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ShopCardDefaults.CONTENT_PADDING)
                        .indication(it, LocalIndication.current),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val request = remember {
                        ImageRequest(shop.iconUrl) {
                            scale(Scale.FIT)
                            size(SizeResolver {
                                Size(ShopCardDefaults.IMAGE_SIZE, ShopCardDefaults.IMAGE_SIZE)
                            })
                        }
                    }
                    AutoSizeBox(request, modifier = Modifier.size(ShopCardDefaults.ICON_SIZE)) { action ->
                        when(action) {
                            is ImageAction.Loading -> {
                                LoadingCircle()
                            }
                            is ImageAction.Failure -> {
                                SideEffect {
                                    action.error.printStackTrace()
                                }
                                Icon(Icons.Filled.Error, null)
                            }
                            is ImageAction.Success -> {
                                Image(rememberImageSuccessPainter(action), null)
                            }
                        }
                    }
                    Spacer(Modifier.height(ShopCardDefaults.PADDING))
                    Text(shop.name)
                }
            }
        }
    )
}