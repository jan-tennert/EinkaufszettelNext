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
import org.koin.dsl.module

val remoteModule = module {
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
}