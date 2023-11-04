package io.github.jan.einkaufszettel.data.local.db

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {

    fun createDriver(): SqlDriver

}