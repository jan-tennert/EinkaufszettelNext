package io.github.jan.einkaufszettel.update.ui

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateModel
import io.github.jan.einkaufszettel.update.data.remote.GithubApi
import io.github.jan.einkaufszettel.update.data.remote.UpdateDownloadEvent
import io.github.jan.einkaufszettel.update.data.remote.UpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.swiftzer.semver.SemVer
import kotlin.js.JsName
import kotlin.jvm.JvmInline

class UpdateScreenModel(
    private val updateManager: UpdateManager,
    private val githubApi: GithubApi
): AppStateModel() {

    sealed interface State: AppState {
        @JvmInline
        value class Progress(val progress: Float): State
        data object DownloadSuccess: State
    }

    private val _latestVersion = MutableStateFlow<SemVer?>(null)
    val latestVersion = _latestVersion.asStateFlow()
    private val _ignoreUpdate = MutableStateFlow(false)
    val ignoreUpdate = _ignoreUpdate.asStateFlow()

    fun checkForUpdates() {
        screenModelScope.launch {
            runCatching {
                githubApi.retrieveLatestVersion()
            }.onSuccess {
                _latestVersion.value = it
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun downloadLatestVersion() {
        screenModelScope.launch {
            runCatching {
                val version = latestVersion.value ?: return@launch
                updateManager.downloadLatestVersion(version).collect {
                    mutableState.value = when(it) {
                        is UpdateDownloadEvent.Progress -> State.Progress(it.progress)
                        is UpdateDownloadEvent.Finished -> State.DownloadSuccess
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun installLatestVersion() {
        screenModelScope.launch {
            runCatching {
                updateManager.installLatestVersion()
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @JsName("ignoreUpdateJs")
    fun ignoreUpdate() {
        _ignoreUpdate.value = true
    }

}