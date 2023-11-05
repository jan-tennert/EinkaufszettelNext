package io.github.jan.einkaufszettel.data.local

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import einkaufszettel.GetAllProducts
import io.github.jan.einkaufszettel.data.local.db.DatabaseProvider
import io.github.jan.einkaufszettel.data.remote.ProductDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface ProductDataSource {

    suspend fun insertProduct(product: ProductDto)

    fun getAllProducts(): Flow<List<GetAllProducts>>

    suspend fun retrieveAllProducts(): List<GetAllProducts>

    suspend fun deleteProductById(id: Long)

    suspend fun deleteAll(ids: List<Long>)

    suspend fun clearProducts()

    suspend fun insertAll(products: List<ProductDto>)

    suspend fun markAsDone(id: Long, doneById: String)

    suspend fun markAsUndone(id: Long)

    suspend fun editContent(id: Long, content: String)

}

internal class ProductDataSourceImpl(
    private val databaseProvider: DatabaseProvider
) : ProductDataSource {

    private val queries get() = databaseProvider.getDatabase().productTableQueries

    override suspend fun clearProducts() {
        queries.clear()
    }

    override fun getAllProducts(): Flow<List<GetAllProducts>> {
        return queries.getAllProducts().asFlow().mapToList(Dispatchers.Default)
    }

    override suspend fun retrieveAllProducts(): List<GetAllProducts> {
        return queries.getAllProducts().awaitAsList()
    }

    override suspend fun deleteProductById(id: Long) {
        queries.deleteProductById(id)
    }

    override suspend fun editContent(id: Long, content: String) {
        queries.editProductContent(content, id)
    }

    override suspend fun insertAll(products: List<ProductDto>) {
        queries.transaction {
            products.forEach { product ->
                insertProduct(product)
            }
        }
    }

    override suspend fun insertProduct(product: ProductDto) {
        queries.insertProduct(
            id = product.id,
            createdAt = product.createdAt,
            content = product.content,
            shopId = product.shopId,
            doneById = product.doneBy,
            creatorId = product.userId,
            doneSince = product.doneSince,
        )
    }

    override suspend fun markAsDone(id: Long, doneById: String) {
        queries.markProductAsDone(doneById, id)
    }

    override suspend fun markAsUndone(id: Long) {
        queries.markProductAsUndone(id)
    }

    override suspend fun deleteAll(ids: List<Long>) {
        queries.transaction {
            ids.forEach { id ->
                deleteProductById(id)
            }
        }
    }

}