package io.github.jan.einkaufszettel.data.local

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import einkaufszettel.ProfileTable
import io.github.jan.einkaufszettel.data.local.db.DatabaseProvider
import io.github.jan.einkaufszettel.data.remote.ProfileDto
import io.github.jan.supabase.gotrue.GoTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ProfileDataSource {

    suspend fun getProfiles(): Flow<List<ProfileDto>>

    suspend fun retrieveAllProfiles(): List<ProfileDto>

    suspend fun insertProfile(profile: ProfileDto) = insertProfiles(listOf(profile))

    suspend fun insertProfiles(profiles: List<ProfileDto>)

    suspend fun retrieveProfile(uid: String): ProfileDto?

    suspend fun retrieveOwnProfile(): ProfileDto?

    suspend fun clear()

}

internal class ProfileDataSourceImpl(
    private val databaseProvider: DatabaseProvider,
    private val goTrue: GoTrue
) : ProfileDataSource {

    private val queries get() = databaseProvider.getDatabase().profileTableQueries

    override suspend fun getProfiles(): Flow<List<ProfileDto>> = queries.getProfiles().asFlow().mapToList(Dispatchers.Default).map { it.map(ProfileTable::toProfile) }

    override suspend fun retrieveAllProfiles(): List<ProfileDto> {
        return queries.getProfiles().awaitAsList().map(ProfileTable::toProfile)
    }

    override suspend fun insertProfiles(profiles: List<ProfileDto>) {
        queries.transaction {
            profiles.forEach { profile ->
                queries.insertProfile(
                    id = profile.id,
                    username = profile.username
                )
            }
        }
    }

    override suspend fun retrieveProfile(uid: String): ProfileDto? = queries.getProfileById(uid).awaitAsOneOrNull()?.toProfile()

    override suspend fun retrieveOwnProfile(): ProfileDto? = goTrue.currentUserOrNull()?.id?.let { retrieveProfile(it) }

    override suspend fun clear() {
        queries.clearProfiles()
    }

}

private fun ProfileTable.toProfile() = ProfileDto(
    id = id,
    username = username
)