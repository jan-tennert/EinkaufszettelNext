package io.github.jan.einkaufszettel.root.data.local.image

expect class LocalImageReader {

    suspend fun platformFileToLocalImage(file: Any): LocalImageData

}