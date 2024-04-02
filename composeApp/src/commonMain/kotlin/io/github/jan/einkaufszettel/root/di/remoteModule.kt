package io.github.jan.einkaufszettel.root.di

import io.github.jan.einkaufszettel.auth.data.remote.AuthenticationApi
import io.github.jan.einkaufszettel.auth.data.remote.AuthenticationApiImpl
import io.github.jan.einkaufszettel.cards.data.remote.CardsApi
import io.github.jan.einkaufszettel.cards.data.remote.CardsApiImpl
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApi
import io.github.jan.einkaufszettel.profile.data.remote.ProfileApiImpl
import io.github.jan.einkaufszettel.recipes.data.remote.RecipeApi
import io.github.jan.einkaufszettel.recipes.data.remote.RecipeApiImpl
import io.github.jan.einkaufszettel.shops.data.remote.ProductApi
import io.github.jan.einkaufszettel.shops.data.remote.ProductApiImpl
import io.github.jan.einkaufszettel.shops.data.remote.ShopApi
import io.github.jan.einkaufszettel.shops.data.remote.ShopApiImpl
import io.github.jan.einkaufszettel.update.data.remote.GithubApi
import io.github.jan.einkaufszettel.update.data.remote.GithubApiImpl
import io.github.jan.einkaufszettel.update.data.remote.UpdateManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.scope.Scope
import org.koin.dsl.module

expect fun Scope.updateManager(): UpdateManager

val remoteModule = module {
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }
    single<AuthenticationApi> {
        AuthenticationApiImpl(get())
    }
    single<ProfileApi> {
        ProfileApiImpl(get(), get())
    }
    single<ProductApi> {
        ProductApiImpl(get())
    }
    single<ShopApi> {
        ShopApiImpl(get(), get())
    }
    single<RecipeApi> {
        RecipeApiImpl(get(), get())
    }
    single<CardsApi> {
        CardsApiImpl(get(), get())
    }
    single {
        updateManager()
    }
    single<GithubApi> {
        GithubApiImpl(get())
    }
}