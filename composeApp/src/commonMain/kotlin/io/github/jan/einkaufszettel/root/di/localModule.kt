package io.github.jan.einkaufszettel.root.di

import io.github.jan.einkaufszettel.cards.data.local.CardsDataSource
import io.github.jan.einkaufszettel.cards.data.local.CardsDataSourceImpl
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.profile.data.local.ProfileDataSourceImpl
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSourceImpl
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageReader
import io.github.jan.einkaufszettel.shops.data.local.ProductDataSource
import io.github.jan.einkaufszettel.shops.data.local.ProductDataSourceImpl
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSource
import io.github.jan.einkaufszettel.shops.data.local.ShopDataSourceImpl
import org.koin.core.scope.Scope
import org.koin.dsl.module

val localModule = module {
    single<ProfileDataSource> {
        ProfileDataSourceImpl(get(), get())
    }
    single<ProductDataSource> {
        ProductDataSourceImpl(get())
    }
    single<ShopDataSource> {
        ShopDataSourceImpl(get())
    }
    single<RecipeDataSource> {
        RecipeDataSourceImpl(get())
    }
    single<LocalImageReader> {
        createLocalImageReader()
    }
    single<CardsDataSource> {
        CardsDataSourceImpl(get())
    }
}

expect fun Scope.createLocalImageReader(): LocalImageReader