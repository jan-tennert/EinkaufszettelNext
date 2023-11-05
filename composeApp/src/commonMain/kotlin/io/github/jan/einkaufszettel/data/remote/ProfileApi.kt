package io.github.jan.einkaufszettel.data.remote

import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val username: String
)

interface ProfileApi {

    suspend fun retrieveOwnProfile(): ProfileDto?

    suspend fun retrieveProfile(uid: String): ProfileDto?

    suspend fun createProfileForUser(uid: String, name: String): ProfileDto

    suspend fun createProfileForCurrentUser(name: String): ProfileDto

}


internal class ProfileApiImpl(
    postgrest: Postgrest,
    private val goTrue: GoTrue
): ProfileApi {

    private val profileTable = postgrest["profiles"]

    override suspend fun retrieveProfile(uid: String): ProfileDto? {
        val result = profileTable.select {
            ProfileDto::id eq uid
        }
        return result.decodeSingleOrNull()
    }

    override suspend fun retrieveOwnProfile(): ProfileDto? = retrieveProfile(goTrue.currentUserOrNull()?.id ?: error("No user logged in"))

    override suspend fun createProfileForUser(uid: String, name: String): ProfileDto {
        val result = profileTable.insert(ProfileDto(uid, name))
        return result.decodeSingle()
    }

    override suspend fun createProfileForCurrentUser(name: String): ProfileDto {
        val uid = goTrue.currentUserOrNull()?.id ?: error("No user logged in")
        return createProfileForUser(uid, name)
    }

}