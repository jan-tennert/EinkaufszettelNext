package io.github.jan.einkaufszettel.data.local.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import io.github.jan.einkaufszettel.db.Einkaufszettel
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface DatabaseProvider {

    suspend fun initDatabase(): Einkaufszettel

    fun getDatabase(): Einkaufszettel

}

internal class DatabaseProviderImpl(
    private val driverFactory: DatabaseDriverFactory
): DatabaseProvider {

    private val mutex = Mutex()
    private lateinit var database: Einkaufszettel

    override suspend fun initDatabase(): Einkaufszettel {
        val driver = driverFactory.createDriver()
        if(CurrentPlatformTarget == PlatformTarget.JS) {
            Einkaufszettel.Schema.awaitCreate(driver)
        }
        mutex.withLock {
            database = Einkaufszettel(driver)
        }
        return database
    }

    override fun getDatabase(): Einkaufszettel {
        if(!this::database.isInitialized) throw IllegalStateException("Database not initialized")
        return database
    }

}