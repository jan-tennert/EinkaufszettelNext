package io.github.jan.einkaufszettel.data.remote

import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String
)

interface ProfileApi {

    suspend fun retrieveOwnProfile(): Profile?

    suspend fun retrieveProfile(uid: String): Profile?

    suspend fun createProfileForUser(uid: String, name: String): Profile

    suspend fun createProfileForCurrentUser(name: String): Profile

}


internal class ProfileApiImpl(
    postgrest: Postgrest,
    private val goTrue: GoTrue
): ProfileApi {

    private val profileTable = postgrest["profiles"]

    override suspend fun retrieveProfile(uid: String): Profile? {
        val result = profileTable.select {
            Profile::id eq uid
        }
        return result.decodeSingleOrNull()
    }

    override suspend fun retrieveOwnProfile(): Profile? = retrieveProfile(goTrue.currentUserOrNull()?.id ?: error("No user logged in"))

    override suspend fun createProfileForUser(uid: String, name: String): Profile {
        val result = profileTable.insert(Profile(uid, name))
        return result.decodeSingle()
    }

    override suspend fun createProfileForCurrentUser(name: String): Profile {
        val uid = goTrue.currentUserOrNull()?.id ?: error("No user logged in")
        return createProfileForUser(uid, name)
    }

}