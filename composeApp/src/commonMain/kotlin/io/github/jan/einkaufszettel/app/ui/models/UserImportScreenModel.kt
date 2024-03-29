package io.github.jan.einkaufszettel.app.ui.models

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateModel
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.launch

open class UserImportScreenModel(private val profileApi: ProfileApi, private val profileDataSource: ProfileDataSource): AppStateModel() {

    fun importUser(id: String) {
        screenModelScope.launch {
            mutableState.value = AppState.Loading
            runCatching {
                profileApi.retrieveProfile(id)
            }.onSuccess {
                if(it != null) {
                    profileDataSource.insertProfile(it)
                    mutableState.value = AppState.Idle
                } else {
                    mutableState.value = AppState.Error(Res.string.user_not_found)
                }
            }.onFailure {
                when(it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
            }
        }
    }

}