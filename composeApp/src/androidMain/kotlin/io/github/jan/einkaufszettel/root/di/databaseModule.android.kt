package io.github.jan.einkaufszettel.root.di

import io.github.jan.einkaufszettel.root.data.local.db.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

actual fun Scope.createDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory(androidContext())