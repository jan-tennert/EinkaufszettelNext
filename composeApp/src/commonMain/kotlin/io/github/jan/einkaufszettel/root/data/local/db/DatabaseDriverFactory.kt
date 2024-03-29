package io.github.jan.einkaufszettel.root.data.local.db

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {

    fun createDriver(): SqlDriver

}