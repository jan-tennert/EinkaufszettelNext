package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.ShopScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail.ShopDetailScreenModel
import org.koin.dsl.module

val shopModels = module {
    factory {
        ShopScreenModel(get(), get())
    }
    factory { parameters ->
        ShopDetailScreenModel(
            shopId = parameters.get(),
            productApi = get(),
            productDataSource = get(),
            goTrue = get()
        )
    }
}