package io.github.jan.einkaufszettel.shops.data.remote

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
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

    suspend fun uploadIcon(
        path: String,
        data: ByteArray
    ): String

    fun getIconUrl(
        path: String
    ): String

}

internal class ShopApiImpl(
    postgrest: Postgrest,
    storage: Storage
) : ShopApi {

    private val table = postgrest["shops"]
    private val bucket = storage["icons"]

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
        ) {
            select()
        }.decodeSingle()
    }

    override suspend fun deleteShop(id: Long) {
        table.delete {
            filter {
                ShopDto::id eq id
            }
        }
    }

    override suspend fun editShop(id: Long, newName: String, authorizedUsers: List<String>): ShopDto {
        return table.update(
            {
                ShopDto::name setTo newName
                ShopDto::authorizedUsers setTo authorizedUsers
            }
        ) {
            select()
            filter {
                ShopDto::id eq id
            }
        }.decodeSingle()
    }

    override suspend fun uploadIcon(path: String, data: ByteArray): String {
        return bucket.upload(path, data)
    }

    override fun getIconUrl(path: String): String {
        return bucket.publicUrl(path)
    }

}