package io.github.jan.einkaufszettel.root.di

import io.github.jan.einkaufszettel.root.data.local.db.DatabaseDriverFactory
import io.github.jan.einkaufszettel.root.data.local.db.DatabaseProvider
import io.github.jan.einkaufszettel.root.data.local.db.DatabaseProviderImpl
import org.koin.core.scope.Scope
import org.koin.dsl.module

expect fun Scope.createDriverFactory(): DatabaseDriverFactory

val databaseModule = module {
    single {
        createDriverFactory()
    }
    single<DatabaseProvider> {
        DatabaseProviderImpl(get())
    }
}