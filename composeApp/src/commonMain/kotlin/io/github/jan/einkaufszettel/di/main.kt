package io.github.jan.einkaufszettel.di

import org.koin.core.KoinApplication

fun KoinApplication.installModules() = modules(supabaseModule, modelModule, supabaseModule, remoteModule, databaseModule, localModule)