package io.github.jan.einkaufszettel.data.local.image

expect class LocalImageReader {

    suspend fun platformFileToLocalImage(file: Any): LocalImageData

}