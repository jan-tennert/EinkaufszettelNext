package io.github.jan.einkaufszettel.profile.ui

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.supabase.exceptions.HttpRequestException
import kotlinx.coroutines.launch

class ProfileCreateScreenModel(
    private val profileApi: ProfileApi,
    private val profileDataSource: ProfileDataSource
): StateScreenModel<ProfileCreateScreenModel.State>(State.Idle) {

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data class Error(val message: String) : State
        data class Success(val name: String) : State
    }

    fun createProfile(name: String) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            runCatching {
                profileApi.createProfileForCurrentUser(name)
            }.onSuccess {
                profileDataSource.insertProfile(it)
                mutableState.value = State.Success(it.username)
            }.onFailure {
                when(it) {
                    is HttpRequestException -> mutableState.value =
                        State.Error(Res.string.network_error)
                    else -> mutableState.value =
                        State.Error(Res.string.unknown_error.format(it.message ?: ""))
                }
            }
        }
    }

    fun reset() {
        mutableState.value = State.Idle
    }

}