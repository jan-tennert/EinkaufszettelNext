package io.github.jan.einkaufszettel.profile.data.remote

import io.github.jan.supabase.gotrue.Auth
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

    suspend fun retrieveProfiles(ids: List<String>): List<ProfileDto>

    suspend fun createProfileForUser(uid: String, name: String): ProfileDto

    suspend fun createProfileForCurrentUser(name: String): ProfileDto

    suspend fun updateProfile(uid: String, name: String): ProfileDto

    suspend fun updateOwnProfile(name: String): ProfileDto

}


internal class ProfileApiImpl(
    postgrest: Postgrest,
    private val auth: Auth
): ProfileApi {

    private val profileTable = postgrest["profiles"]

    override suspend fun retrieveProfile(uid: String): ProfileDto? {
        val result = profileTable.select {
            filter {
                ProfileDto::id eq uid
            }
        }
        return result.decodeSingleOrNull()
    }

    override suspend fun retrieveOwnProfile(): ProfileDto? = retrieveProfile(auth.currentUserOrNull()?.id ?: error("No user logged in"))

    override suspend fun createProfileForUser(uid: String, name: String): ProfileDto {
        val result = profileTable.insert(ProfileDto(uid, name))
        return result.decodeSingle()
    }

    override suspend fun createProfileForCurrentUser(name: String): ProfileDto {
        val uid = auth.currentUserOrNull()?.id ?: error("No user logged in")
        return createProfileForUser(uid, name)
    }

    override suspend fun retrieveProfiles(ids: List<String>): List<ProfileDto> {
        return profileTable.select {
            filter {
                ProfileDto::id isIn ids
            }
        }.decodeList()
    }

    override suspend fun updateOwnProfile(name: String): ProfileDto {
        val uid = auth.currentUserOrNull()?.id ?: error("No user logged in")
        return updateProfile(uid, name)
    }

    override suspend fun updateProfile(uid: String, name: String): ProfileDto {
        val result = profileTable.update(ProfileDto(uid, name)) {
            select()
            filter {
                ProfileDto::id eq uid
            }
        }
        return result.decodeSingle()
    }

}