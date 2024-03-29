package io.github.jan.einkaufszettel.root.data.local.db

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.jan.einkaufszettel.db.Einkaufszettel

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Einkaufszettel.Schema.synchronous(), context, "Einkaufszettel.db")
    }

}