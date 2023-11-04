package io.github.jan.einkaufszettel.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.jan.einkaufszettel.db.Einkaufszettel
import org.koin.core.scope.Scope

actual fun Scope.createDriver(): SqlDriver {
    return AndroidSqliteDriver(Einkaufszettel.Schema.synchronous(), get(), "Einkaufszettel.db")
}