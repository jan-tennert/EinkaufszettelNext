package io.github.jan.einkaufszettel.data.local

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import einkaufszettel.ShopTable
import io.github.jan.einkaufszettel.data.local.db.DatabaseProvider
import io.github.jan.einkaufszettel.data.remote.ShopDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ShopDataSource {

    suspend fun insertShop(shop: ShopDto)

    fun getAllShops(): Flow<List<ShopDto>>

    suspend fun retrieveAllShops(): List<ShopDto>

    suspend fun deleteById(id: Long)

    suspend fun deleteAll(ids: List<Long>)

    suspend fun clear()

    suspend fun insertAll(shops: List<ShopDto>)

}

internal class ShopDataSourceImpl(
    private val databaseProvider: DatabaseProvider
) : ShopDataSource {

    private val queries get() = databaseProvider.getDatabase().shopTableQueries

    override suspend fun clear() {
       queries.clearShops()
    }

    override suspend fun deleteById(id: Long) {
        queries.deleteShopById(id)
    }

    override fun getAllShops(): Flow<List<ShopDto>> {
        return queries.getAllShops().asFlow().mapToList(Dispatchers.Default).map { it.map(ShopTable::toShopDto) }
    }

    override suspend fun insertAll(shops: List<ShopDto>) {
        queries.transaction {
            shops.forEach { shop ->
                insertShop(shop)
            }
        }
    }

    override suspend fun insertShop(shop: ShopDto) {
        queries.insertShop(
            id = shop.id,
            createdAt = shop.createdAt,
            name = shop.name,
            iconUrl = shop.iconUrl,
            ownerId = shop.ownerId,
            authorizedUsers = shop.authorizedUsers
        )
    }

    override suspend fun retrieveAllShops(): List<ShopDto> {
        return queries.getAllShops().awaitAsList().map(ShopTable::toShopDto)
    }

    override suspend fun deleteAll(ids: List<Long>) {
        queries.transaction {
            ids.forEach { id ->
                queries.deleteShopById(id)
            }
        }
    }

}

private fun ShopTable.toShopDto(): ShopDto {
    return ShopDto(
        id = id,
        createdAt = createdAt,
        name = name,
        iconUrl = iconUrl,
        ownerId = ownerId,
        authorizedUsers = authorizedUsers
    )
}