package io.github.jan.einkaufszettel.ui.screen.app

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.data.remote.AuthenticationApi
import kotlinx.coroutines.launch

class AppScreenModel(
    private val authenticationApi: AuthenticationApi,
    private val profileDataSource: ProfileDataSource
): ScreenModel {

    fun logout() {
        screenModelScope.launch {
            runCatching {
                authenticationApi.logout()
            }.onSuccess {
                clearLocalData()
            }
        }
    }

    private fun clearLocalData() {
        screenModelScope.launch {
            runCatching {
                profileDataSource.clear()
            }
        }
    }

}