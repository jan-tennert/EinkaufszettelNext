package io.github.jan.einkaufszettel.root.di

import io.github.jan.einkaufszettel.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.coil.Coil3Integration
import io.github.jan.supabase.coil.coil3
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.koin.dsl.module

@OptIn(SupabaseExperimental::class, ExperimentalSerializationApi::class)
val supabaseModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            namingStrategy = JsonNamingStrategy.SnakeCase
        }
    }
    single<SupabaseClient> {
        Coil3Integration.setLogLevel(LogLevel.INFO)
        createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_KEY) {
            defaultSerializer = KotlinXSerializer(get())
            defaultLogLevel = LogLevel.DEBUG
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "https"
                host = "jan-tennert.github.io/EinkaufszettelNext"
            }
            install(Storage)
            install(Postgrest)
            install(ComposeAuth) {
                googleNativeLogin(BuildConfig.GOOGLE_CLIENT_ID)
            }
            install(Coil3Integration)
        }
    }
    single<Auth> {
        get<SupabaseClient>().auth
    }
    single<Storage> {
        get<SupabaseClient>().storage
    }
    single<Postgrest> {
        get<SupabaseClient>().postgrest
    }
    single<ComposeAuth> {
        get<SupabaseClient>().composeAuth
    }
    single {
        get<SupabaseClient>().coil3
    }
}