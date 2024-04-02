package io.github.jan.einkaufszettel.update.data.remote

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import net.swiftzer.semver.SemVer
import java.io.File

actual class UpdateManager(
    private val context: Context,
    private val httpClient: HttpClient,
) {

    actual suspend fun downloadLatestVersion(version: SemVer): Flow<UpdateDownloadEvent> {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, "update.apk")
            if(file.exists()) {
                file.delete()
            }
            file.createNewFile()
            callbackFlow {
                val response = httpClient.get(API_URL.format(version.toString())) {
                    onDownload { bytesSentTotal, contentLength ->
                        val float = bytesSentTotal.toFloat() / contentLength.toFloat()
                        trySend(UpdateDownloadEvent.Progress(if(float.isNaN()) 0f else float))
                    }
                }
                file.writeBytes(response.body())
                trySend(UpdateDownloadEvent.Finished)
                close()
            }
        }
    }

    actual suspend fun installLatestVersion(): Boolean {
        val file = File(context.filesDir, "update.apk")
        if(!file.exists()) return false
        val intent = Intent(Intent.ACTION_VIEW)
        val downloadedApk = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
        intent.setDataAndType(downloadedApk, "application/vnd.android.package-archive")
        val resInfoList: List<ResolveInfo> = context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            context.grantUriPermission(
                context.applicationContext.packageName.toString() + ".provider",
                downloadedApk,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(context, intent, null)
        return true
    }

    companion object {
        const val API_URL = "https://github.com/jan-tennert/EinkaufszettelNext/releases/download/%s/composeApp-release.apk"
    }

}