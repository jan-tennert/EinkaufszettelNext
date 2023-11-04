package io.github.jan.einkaufszettel.di

import app.cash.sqldelight.db.SqlDriver
import io.github.jan.einkaufszettel.db.Einkaufszettel
import org.koin.core.scope.Scope
import org.koin.dsl.module

expect fun Scope.createDriver(): SqlDriver

val databaseModule = module {
    single {
        Einkaufszettel(createDriver())
    }
}