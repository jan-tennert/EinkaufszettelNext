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
) : ScreenModel {

    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    fun login(email: String, password: String) {
        screenModelScope.launch {
            isLoading.value = true
            runCatching {
                authenticationApiImpl.login(email, password)
            }.onFailure {
                when(it) {
                    is RestException -> {
                        error.value = Res.string.invalid_credentials
                    }
                    is HttpRequestException -> {
                        error.value = Res.string.network_error
                    }
                    else -> {
                        error.value = Res.string.unknown_error.format(it.message ?: "")
                    }
                }
            }
            isLoading.value = false
        }
    }

    fun resetError() {
        error.value = null
    }

}