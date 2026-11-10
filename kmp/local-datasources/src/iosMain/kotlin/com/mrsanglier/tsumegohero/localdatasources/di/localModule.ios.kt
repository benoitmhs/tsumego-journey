package com.mrsanglier.tsumegohero.localdatasources.di

import com.mrsanglier.tsumegohero.localdatasources.createDataStoreIOS
import com.mrsanglier.tsumegohero.localdatasources.getCollectionDatabase
import com.mrsanglier.tsumegohero.localdatasources.getDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val localPlatformModule: Module = module {
    singleOf(::getDatabase)
    singleOf(::getCollectionDatabase)
    singleOf(::createDataStoreIOS)
}
