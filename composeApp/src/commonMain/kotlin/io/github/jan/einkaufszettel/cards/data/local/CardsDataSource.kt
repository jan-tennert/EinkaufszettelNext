package io.github.jan.einkaufszettel.cards.data.local

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import einkaufszettel.GetAllCards
import einkaufszettel.GetCardById
import io.github.jan.einkaufszettel.cards.data.remote.CardDto
import io.github.jan.einkaufszettel.root.data.local.db.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CardsDataSource {

    suspend fun insertCard(
        card: CardDto
    )

    suspend fun insertCards(
        cards: List<CardDto>
    )

    fun getAllCards(): Flow<List<GetAllCards>>

    fun getCardById(
        id: Long
    ): Flow<GetCardById>

    suspend fun retrieveAllCards(): List<GetAllCards>

    suspend fun deleteCardById(
        id: Long
    )

    suspend fun deleteAll(
        ids: List<Long>
    )

}

internal class CardsDataSourceImpl(
    private val databaseProvider: DatabaseProvider
) : CardsDataSource {

    private val queries get() = databaseProvider.getDatabase().cardTableQueries

    override suspend fun insertCard(
        card: CardDto
    ) {
        queries.insertCard(
            id = card.id,
            createdAt = card.createdAt,
            ownerId = card.ownerId,
            imagePath = card.imagePath,
            description = card.description,
            authorizedUsers = card.authorizedUsers
        )
    }

    override suspend fun insertCards(
        cards: List<CardDto>
    ) {
        cards.forEach { insertCard(it) }
    }

    override fun getAllCards(): Flow<List<GetAllCards>> {
        return queries.getAllCards().asFlow().mapToList(Dispatchers.Default)
    }

    override fun getCardById(id: Long): Flow<GetCardById> {
        return queries.getCardById(id).asFlow().map { it.awaitAsOne() }
    }

    override suspend fun retrieveAllCards(): List<GetAllCards> {
        return queries.getAllCards().awaitAsList()
    }

    override suspend fun deleteCardById(
        id: Long
    ) {
        queries.deleteCardById(id)
    }

    override suspend fun deleteAll(ids: List<Long>) {
        queries.transaction {
            ids.forEach { deleteCardById(it) }
        }
    }

}