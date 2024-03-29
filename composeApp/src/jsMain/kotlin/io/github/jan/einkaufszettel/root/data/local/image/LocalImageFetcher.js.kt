package io.github.jan.einkaufszettel.root.data.local.image

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import coil3.annotation.ExperimentalCoilApi
import coil3.asCoilImage
import org.jetbrains.skia.Image

@OptIn(ExperimentalCoilApi::class)
actual fun createImage(localImageData: LocalImageData): coil3.Image {
    val image = Image.makeFromEncoded(localImageData.data).toComposeImageBitmap().asSkiaBitmap()
    return image.asCoilImage()
}