package io.github.jan.einkaufszettel.root.data.local.image

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.coroutines.suspendCoroutine

actual class LocalImageReader {

    actual suspend fun platformFileToLocalImage(file: Any): LocalImageData {
        file as File
        return LocalImageData(
            data = readFile(file),
            extension = file.name.substringAfterLast('.')
        )
    }

}

private suspend fun readFile(file: File): ByteArray = suspendCoroutine {
    val reader = FileReader()
    reader.onload = { loadEvt ->
        println(loadEvt.target.asDynamic().result)
        val content = Int8Array(loadEvt.target.asDynamic().result as ArrayBuffer).unsafeCast<ByteArray>()
        it.resumeWith(Result.success(content))
    }
    reader.readAsArrayBuffer(file)
}