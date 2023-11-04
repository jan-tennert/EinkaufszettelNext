package io.github.jan.einkaufszettel.ui.screen.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.data.remote.AuthenticationApi
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val authenticationApiImpl: AuthenticationApi
) : StateScreenModel<LoginScreenModel.State>(State.Idle) {

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data class Error(val message: String) : State
        data object SignUpSuccess : State
    }

    fun login(email: String, password: String) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            runCatching {
                authenticationApiImpl.login(email, password)
            }.onSuccess {
                mutableState.value = State.Idle
            }.onFailure(::handleError)
        }
    }

    fun signUp(email: String, password: String) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            runCatching {
                authenticationApiImpl.signUp(email, password)
            }.onSuccess {
                mutableState.value = State.SignUpSuccess
            }.onFailure(::handleError)
        }
    }

    fun resetState() {
        mutableState.value = State.Idle
    }

    fun setLoading() {
        mutableState.value = State.Loading
    }

    private fun handleError(error: Throwable) {
        when(error) {
            is RestException -> {
                mutableState.value = State.Error(Res.string.invalid_credentials)
            }
            is HttpRequestException -> {
                mutableState.value = State.Error(Res.string.network_error)
            }
            else -> {
                mutableState.value = State.Error(Res.string.unknown_error.format(error.message ?: ""))
            }
        }
    }

}