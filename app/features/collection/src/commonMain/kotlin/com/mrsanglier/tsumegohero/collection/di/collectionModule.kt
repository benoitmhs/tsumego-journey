package com.mrsanglier.tsumegohero.collection.di

import com.mrsanglier.tsumegohero.collection.detail.CollectionDetailViewModel
import com.mrsanglier.tsumegohero.collection.list.CollectionListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val collectionModule = module {
    viewModelOf(::CollectionListViewModel)
    viewModelOf(::CollectionDetailViewModel)
}
