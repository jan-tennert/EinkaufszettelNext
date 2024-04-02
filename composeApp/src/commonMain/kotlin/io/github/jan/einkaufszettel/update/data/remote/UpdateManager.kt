package io.github.jan.einkaufszettel.update.data.remote

import kotlinx.coroutines.flow.Flow
import net.swiftzer.semver.SemVer

sealed interface UpdateDownloadEvent {

    data class Progress(val progress: Float): UpdateDownloadEvent

    data object Finished: UpdateDownloadEvent

}

expect class UpdateManager {

    suspend fun downloadLatestVersion(version: SemVer): Flow<UpdateDownloadEvent>

    suspend fun installLatestVersion(): Boolean

}