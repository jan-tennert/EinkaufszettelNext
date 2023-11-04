package io.github.jan.einkaufszettel.data.local

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import einkaufszettel.ProfileTable
import io.github.jan.einkaufszettel.data.remote.Profile
import io.github.jan.einkaufszettel.db.Einkaufszettel
import io.github.jan.supabase.gotrue.GoTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ProfileDataSource {

    suspend fun getProfiles(): Flow<List<Profile>>

    suspend fun insertProfile(profile: Profile) = insertProfiles(listOf(profile))

    suspend fun insertProfiles(profiles: List<Profile>)

    suspend fun retrieveProfile(uid: String): Profile

    suspend fun retrieveOwnProfile(): Profile?

    suspend fun clear()

}

internal class ProfileDataSourceImpl(
    database: Einkaufszettel,
    private val goTrue: GoTrue
) : ProfileDataSource {

    private val queries = database.profileTableQueries

    override suspend fun getProfiles(): Flow<List<Profile>> = queries.getProfiles().asFlow().mapToList(Dispatchers.Default).map { it.map { p -> p.toProfile() } }

    override suspend fun insertProfiles(profiles: List<Profile>) {
        queries.transaction {
            profiles.forEach { profile ->
                queries.insertProfile(
                    id = profile.id,
                    username = profile.username
                )
            }
        }
    }

    override suspend fun retrieveProfile(uid: String): Profile = queries.getProfileById(uid).awaitAsOne().toProfile()

    override suspend fun retrieveOwnProfile(): Profile? = goTrue.currentUserOrNull()?.id?.let { retrieveProfile(it) }

    override suspend fun clear() {
        queries.clearProfiles()
    }

}

private fun ProfileTable.toProfile() = Profile(
    id = id,
    username = username
)