package io.github.jan.einkaufszettel.auth.data.remote

import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo

interface AuthenticationApi {

    suspend fun login(
        email: String,
        password: String
    )

    suspend fun signUp(
        email: String,
        password: String
    ): UserInfo?

    suspend fun logout()

}

internal class AuthenticationApiImpl(
    private val auth: Auth
): AuthenticationApi {

    override suspend fun login(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUp(email: String, password: String): UserInfo? {
        return auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

}