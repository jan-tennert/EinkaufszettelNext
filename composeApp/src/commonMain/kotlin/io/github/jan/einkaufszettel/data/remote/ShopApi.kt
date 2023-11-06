package io.github.jan.einkaufszettel.data.remote

import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ShopDto(
    val id: Long,
    val createdAt: Instant,
    val name: String,
    val iconUrl: String,
    val ownerId: String,
    val authorizedUsers: List<String>,
    @Transient val collapsed: Boolean = false,
    @Transient val pinned: Boolean = false
)

@Serializable
data class ShopCreationDto(
    val name: String,
    val iconUrl: String,
    val ownerId: String,
    val authorizedUsers: List<String>
)

interface ShopApi {

    suspend fun retrieveShops(): List<ShopDto>

    suspend fun createShop(
        name: String,
        iconUrl: String,
        ownerId: String,
        authorizedUsers: List<String>
    ): ShopDto

    suspend fun editShop(
        id: Long,
        newName: String,
        authorizedUsers: List<String>
    ): ShopDto

    suspend fun deleteShop(
        id: Long
    )

}

internal class ShopApiImpl(
    postgrest: Postgrest
) : ShopApi {

    private val table = postgrest["shops"]

    override suspend fun retrieveShops(): List<ShopDto> {
        return table.select().decodeList()
    }

    override suspend fun createShop(
        name: String,
        iconUrl: String,
        ownerId: String,
        authorizedUsers: List<String>
    ): ShopDto {
        return table.insert(
            ShopCreationDto(
                name,
                iconUrl,
                ownerId,
                authorizedUsers
            )
        ).decodeSingle()
    }

    override suspend fun deleteShop(id: Long) {
        table.delete {
            ShopDto::id eq id
        }
    }

    override suspend fun editShop(id: Long, newName: String, authorizedUsers: List<String>): ShopDto {
        return table.update(
            {
                ShopDto::name setTo newName
                ShopDto::authorizedUsers setTo authorizedUsers
            }
        ) {
            ShopDto::id eq id
        }.decodeSingle()
    }

}