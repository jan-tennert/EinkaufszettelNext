package io.github.jan.einkaufszettel.auth

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.auth.data.remote.AuthenticationApi
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val authenticationApiImpl: AuthenticationApi
) : StateScreenModel<LoginScreenModel.State>(State.Idle) {

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data class Error(val message: String) : State
        data object SignUpSuccess : State
        data class PasswordResetSuccess(val email: String) : State
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

    fun sendPasswordResetEmail(email: String) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            runCatching {
                authenticationApiImpl.sendPasswordResetEmail(email)
            }.onSuccess {
                mutableState.value = State.PasswordResetSuccess(email)
            }.onFailure(::handleError)
        }
    }

    fun signInWithOtp(email: String, otp: String) {
        screenModelScope.launch {
            mutableState.value = State.Loading
            runCatching {
                authenticationApiImpl.signInWithOtp(email, otp)
            }.onSuccess {
                mutableState.value = State.Idle
            }.onFailure(::handleError)
        }
    }

    fun resetState() {
        mutableState.value = State.Idle
    }

    fun setLoading() {
        mutableState.value = State.Loading
    }

    fun setPassResetSuccess(email: String) {
        mutableState.value = State.PasswordResetSuccess(email)
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
                mutableState.value =
                    State.Error(Res.string.unknown_error.format(error.message ?: ""))
            }
        }
    }

}