package io.github.jan.einkaufszettel.root.ui.screen.authenticated

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.launch

class AuthenticatedScreenModel(
    private val profileApi: ProfileApi,
    private val profileDataSource: ProfileDataSource
): StateScreenModel<AuthenticatedScreenModel.State>(State.Loading) {

    sealed interface State {
        data object Loading : State
        data object UserNotFound : State
        data object NetworkError : State
        data object UserFound : State
    }

    fun checkUser() {
        checkUserInDatabase()
    }

    private fun checkUserInApi() {
        screenModelScope.launch {
            runCatching {
                profileApi.retrieveOwnProfile()
            }.onSuccess {
                if (it != null) {
                    mutableState.value = State.UserFound
                    profileDataSource.insertProfile(it)
                } else {
                    mutableState.value = State.UserNotFound
                }
            }.onFailure {
                it.printStackTrace()
                when(it) {
                    is RestException -> mutableState.value = State.UserNotFound
                    is HttpRequestException -> mutableState.value = State.NetworkError
                    else -> mutableState.value = State.NetworkError
                }
            }
        }
    }

    private fun checkUserInDatabase() {
        println("checkUserInDatabase")
        screenModelScope.launch {
            runCatching {
                profileDataSource.retrieveOwnProfile()
            }.onSuccess {
                mutableState.value = State.UserFound
            }.onFailure {
                it.printStackTrace()
                checkUserInApi()
            }
        }
    }

}