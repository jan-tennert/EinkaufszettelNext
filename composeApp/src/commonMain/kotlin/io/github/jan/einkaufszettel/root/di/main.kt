package io.github.jan.einkaufszettel.root.di

import org.koin.core.KoinApplication

fun KoinApplication.installModules() = modules(supabaseModule, modelModule, supabaseModule, remoteModule, databaseModule, localModule)