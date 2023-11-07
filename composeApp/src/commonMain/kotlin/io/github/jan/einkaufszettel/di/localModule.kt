package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.data.local.ProductDataSource
import io.github.jan.einkaufszettel.data.local.ProductDataSourceImpl
import io.github.jan.einkaufszettel.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.data.local.ProfileDataSourceImpl
import io.github.jan.einkaufszettel.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.data.local.RecipeDataSourceImpl
import io.github.jan.einkaufszettel.data.local.ShopDataSource
import io.github.jan.einkaufszettel.data.local.ShopDataSourceImpl
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
}