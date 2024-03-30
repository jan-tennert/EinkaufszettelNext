package io.github.jan.einkaufszettel.cards.data.local

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import einkaufszettel.GetAllCards
import io.github.jan.einkaufszettel.cards.data.remote.CardDto
import io.github.jan.einkaufszettel.root.data.local.db.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface CardsDataSource {

    suspend fun insertCard(
        card: CardDto
    )

    suspend fun insertCards(
        cards: List<CardDto>
    )

    fun getAllCards(): Flow<List<GetAllCards>>

    suspend fun retrieveAllCards(): List<GetAllCards>

    suspend fun deleteCardById(
        id: Long
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

    override suspend fun retrieveAllCards(): List<GetAllCards> {
        return queries.getAllCards().awaitAsList()
    }

    override suspend fun deleteCardById(
        id: Long
    ) {
        queries.deleteCardById(id)
    }

}