package io.github.jan.einkaufszettel.update.data.remote

import kotlinx.coroutines.flow.Flow
import net.swiftzer.semver.SemVer

actual class UpdateManager {
    actual suspend fun downloadLatestVersion(version: SemVer): Flow<UpdateDownloadEvent> {
        throw UnsupportedOperationException("Not needed on JS")
    }

    actual suspend fun installLatestVersion(): Boolean {
        throw UnsupportedOperationException("Not needed on JS")
    }

}