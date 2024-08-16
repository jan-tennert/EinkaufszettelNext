package io.github.jan.einkaufszettel.root.data.local.image

import io.github.vinceglb.filekit.core.PlatformFile

expect class LocalImageReader {

    suspend fun platformFileToLocalImage(file: PlatformFile): LocalImageData

}