package io.github.jan.einkaufszettel.shops.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.components.ContextMenuScope
import io.github.jan.einkaufszettel.shops.data.remote.ShopDto

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
    isOwner: Boolean = false,
    modifier: Modifier = Modifier,
    isScrolling: () -> Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ContextMenuScope(
        onItemClicked = onClick,
        modifier = modifier,
        isScrolling = isScrolling,
        items = {
            DropdownMenuItem(
                text = { Text(Res.string.edit) },
                onClick = {
                    onEdit()
                    it.value = false
                },
                enabled = isOwner
            )
            DropdownMenuItem(
                text = { Text(Res.string.delete) },
                onClick = {
                    onDelete()
                    it.value = false
                },
                enabled = isOwner
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
                    /*val request = remember {
                        ImageRequest(shop.iconUrl) {
                            scale(Scale.FIT)
                            size(SizeResolver {
                                Size(ShopCardDefaults.IMAGE_SIZE, ShopCardDefaults.IMAGE_SIZE)
                            })
                        }
                    }*/
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current).data(shop.iconUrl).crossfade(true).build(),
                        contentDescription = shop.name,
                        modifier = Modifier.size(ShopCardDefaults.ICON_SIZE),
                    )
                    /*AutoSizeBox(request, modifier = Modifier.size(ShopCardDefaults.ICON_SIZE)) { action ->
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
                    }*/
                    Spacer(Modifier.height(ShopCardDefaults.PADDING))
                    Text(shop.name)
                }
            }
        }
    )
}