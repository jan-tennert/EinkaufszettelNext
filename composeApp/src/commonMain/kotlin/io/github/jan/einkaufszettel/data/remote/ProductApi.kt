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
    val doneById: String?,
    val creatorId: String,
    val doneSince: Instant?
)

@Serializable
data class ProductCreationDto(
    val content: String,
    val shopId: Long,
    val creatorId: String
)

interface ProductApi {

    suspend fun retrieveProducts(): List<ProductDto>

    suspend fun createProduct(
        shopId: Long,
        content: String,
        creatorId: String
    ): ProductDto

    suspend fun markAsDone(
        id: Long,
        doneById: String
    ): ProductDto

    suspend fun markAsUndone(
        id: Long
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
            creatorId = creatorId
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

    override suspend fun markAsDone(id: Long, doneById: String): ProductDto {
        return table.update({
            ProductDto::doneById setTo doneById
            ProductDto::doneSince setTo Clock.System.now()
        }) {
            ProductDto::id eq id
        }.decodeSingle()
    }

    override suspend fun markAsUndone(id: Long): ProductDto {
        return table.update({
            ProductDto::doneById setTo null
            ProductDto::doneSince setTo null
        }) {
            ProductDto::id eq id
        }.decodeSingle()
    }

}