package io.github.jan.einkaufszettel.root.data.local.image

import android.graphics.BitmapFactory
import coil3.Image
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage

@OptIn(ExperimentalCoilApi::class)
actual fun createImage(localImageData: LocalImageData): Image {
    val bitmap = BitmapFactory.decodeByteArray(localImageData.data, 0, localImageData.data.size)
    return bitmap.asImage()
}