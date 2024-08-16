package io.github.jan.einkaufszettel.root.data.local.image

import io.github.vinceglb.filekit.core.PlatformFile

actual class LocalImageReader {

    actual suspend fun platformFileToLocalImage(file: PlatformFile): LocalImageData {
        return LocalImageData(
            data = file.readBytes(),
            extension = file.name.substringAfterLast('.')
        )
    }

}