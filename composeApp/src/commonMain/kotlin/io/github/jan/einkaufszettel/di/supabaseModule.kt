package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.imageloader.ImageLoaderIntegration
import io.github.jan.supabase.imageloader.imageLoader
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
        createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_KEY) {
            defaultSerializer = KotlinXSerializer(get())
            install(GoTrue) {
                flowType = FlowType.PKCE
            }
            install(Storage)
            install(Postgrest)
            install(ComposeAuth) {
                googleNativeLogin(BuildConfig.GOOGLE_CLIENT_ID)
            }
            install(ImageLoaderIntegration)
        }
    }
    single<GoTrue> {
        get<SupabaseClient>().gotrue
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
    single<ImageLoaderIntegration> {
        get<SupabaseClient>().imageLoader
    }
}