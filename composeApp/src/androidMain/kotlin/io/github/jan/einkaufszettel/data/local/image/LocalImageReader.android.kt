package io.github.jan.einkaufszettel.data.local.image

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class LocalImageReader(private val context: Context) {
    actual suspend fun platformFileToLocalImage(file: Any): LocalImageData {
        file as Uri
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(file)?.use {
                LocalImageData(
                    data = it.readBytes(),
                    extension = file.path?.substringAfterLast('.') ?: ""
                )
            } ?: error("Could not read file")
        }
    }

}