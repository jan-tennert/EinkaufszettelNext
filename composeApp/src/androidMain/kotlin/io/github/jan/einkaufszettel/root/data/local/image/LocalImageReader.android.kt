package io.github.jan.einkaufszettel.root.data.local.image

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import io.github.vinceglb.filekit.core.PlatformFile
import java.io.File


actual class LocalImageReader(private val context: Context) {
    actual suspend fun platformFileToLocalImage(file: PlatformFile): LocalImageData {
        return LocalImageData(
            data = file.readBytes(),
            extension = getMimeType(context, file.uri)
        )
    }

    private fun getMimeType(context: Context, uri: Uri): String {
        val extension: String = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime: MimeTypeMap = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri)).toString()
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

}