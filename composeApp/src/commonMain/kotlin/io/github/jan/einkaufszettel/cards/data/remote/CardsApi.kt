package io.github.jan.einkaufszettel.cards.data.remote

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    val id: Long,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("owner_id")
    val ownerId: String,
    @SerialName("image_path")
    val imagePath: String,
    val description: String,
    @SerialName("authorized_users")
    val authorizedUsers: List<String>
)

@Serializable
data class CardCreationDto(
    @SerialName("image_path")
    val imagePath: String,
    val description: String,
    @SerialName("owner_id")
    val ownerId: String,
    @SerialName("authorized_users")
    val authorizedUsers: List<String>
)

interface CardsApi {

    suspend fun retrieveCards(): List<CardDto>

    suspend fun createCard(
        imagePath: String,
        description: String,
        ownerId: String,
        authorizedUsers: List<String>
    ): CardDto

    suspend fun editCard(
        id: Long,
        description: String,
        authorizedUsers: List<String>
    ): CardDto

    suspend fun deleteCard(
        id: Long
    )

    suspend fun uploadImage(
        path: String,
        data: ByteArray
    ): String

    suspend fun deleteImage(
        path: String
    )

    companion object {
        const val BUCKET_ID = "cards"
        const val TABLE_NAME = "cards"
    }

}

internal class CardsApiImpl(
    postgrest: Postgrest,
    storage: Storage
) : CardsApi {

    private val table = postgrest[CardsApi.TABLE_NAME]
    private val cardImages = storage[CardsApi.BUCKET_ID]

    override suspend fun retrieveCards(): List<CardDto> {
        return table.select().decodeList()
    }

    override suspend fun createCard(
        imagePath: String,
        description: String,
        ownerId: String,
        authorizedUsers: List<String>
    ): CardDto {
        return table.insert(
            CardCreationDto(
                imagePath = imagePath,
                description = description,
                ownerId = ownerId,
                authorizedUsers = authorizedUsers
            )
        ) {
            select()
        }.decodeSingle()
    }

    override suspend fun editCard(
        id: Long,
        description: String,
        authorizedUsers: List<String>
    ): CardDto {
        return table.update({
            CardDto::authorizedUsers setTo authorizedUsers
            CardDto::description setTo description
        }) {
            filter {
                CardDto::id eq id
            }
            select()
        }.decodeSingle()
    }

    override suspend fun deleteCard(id: Long) {
        table.delete {
            filter {
                CardDto::id eq id
            }
        }
    }

    override suspend fun uploadImage(path: String, data: ByteArray): String {
        return cardImages.upload(path, data)
    }

    override suspend fun deleteImage(path: String) {
        cardImages.delete(path)
    }

}