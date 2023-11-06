package io.github.jan.einkaufszettel.data.remote

import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Long,
    val createdAt: Instant,
    val content: String,
    val shopId: Long,
    val userId: String,
    val doneBy: String?,
    val doneSince: Instant?
)

@Serializable
data class ProductCreationDto(
    val content: String,
    val shopId: Long,
    val userId: String
)

interface ProductApi {

    suspend fun retrieveProducts(): List<ProductDto>

    suspend fun createProduct(
        shopId: Long,
        content: String,
        creatorId: String
    ): ProductDto

    suspend fun changeDoneStatus(
        id: Long,
        doneById: String?
    ): ProductDto

    suspend fun editContent(
        id: Long,
        content: String
    ): ProductDto

    suspend fun deleteProduct(
        id: Long
    )

}

internal class ProductApiImpl(
    postgrest: Postgrest
) : ProductApi {

    private val table = postgrest["products"]

    override suspend fun retrieveProducts(): List<ProductDto> {
        return table.select().decodeList()
    }

    override suspend fun createProduct(shopId: Long, content: String, creatorId: String): ProductDto {
        return table.insert(ProductCreationDto(
            content = content,
            shopId = shopId,
            userId = creatorId
        )).decodeSingle()
    }

    override suspend fun deleteProduct(id: Long) {
        table.delete {
            ProductDto::id eq id
        }
    }

    override suspend fun editContent(id: Long, content: String): ProductDto {
        return table.update({
            ProductDto::content setTo content
        }) {
            ProductDto::id eq id
        }.decodeSingle()
    }

    override suspend fun changeDoneStatus(id: Long, doneById: String?): ProductDto {
        return table.update({
            ProductDto::doneBy setTo doneById
            ProductDto::doneSince setTo if(doneById != null) Clock.System.now() else null
        }) {
            ProductDto::id eq id
        }.decodeSingle()
    }

}