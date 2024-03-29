package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.shops.ui.screen.ShopProductScreenModel
import io.github.jan.einkaufszettel.shops.ui.screen.create.ShopCreateScreenModel
import io.github.jan.einkaufszettel.shops.ui.screen.detail.ShopDetailScreenModel
import io.github.jan.einkaufszettel.shops.ui.screen.edit.ShopEditScreenModel
import io.github.jan.einkaufszettel.shops.ui.screen.main.ShopScreenModel
import org.koin.dsl.module

val shopModels = module {
    factory {
        ShopScreenModel(get(), get(), get(), get())
    }
    factory { parameters ->
        ShopDetailScreenModel(
            shopId = parameters.get(),
            productApi = get(),
            productDataSource = get(),
            auth = get(),
            shopDataSource = get()
        )
    }
    factory {
        ShopProductScreenModel(get(), get(), get())
    }
    factory {
        ShopCreateScreenModel(get(), get(), get(), get(), get(), get())
    }
    factory { parameters ->
        ShopEditScreenModel(
            shopId = parameters.get(),
            shopApi = get(),
            shopDataSource = get(),
            profileDataSource = get(),
            profileApi = get()
        )
    }
}