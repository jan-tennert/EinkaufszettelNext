package io.github.jan.einkaufszettel.root.data.local.image

import io.github.vinceglb.filekit.PlatformFile

expect class LocalImageReader {

    suspend fun platformFileToLocalImage(file: PlatformFile): LocalImageData

}