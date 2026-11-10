package com.mrsanglier.tsumegohero.localdatasources.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mrsanglier.tsumegohero.localdatasources.createDataStoreAndroid
import com.mrsanglier.tsumegohero.localdatasources.getCollectionDatabase
import com.mrsanglier.tsumegohero.localdatasources.getDatabase
import com.mrsanglier.tsumegohero.localdatasources.room.AppDatabase
import com.mrsanglier.tsumegohero.localdatasources.room.collection.CollectionDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val localPlatformModule: Module = module {
    single<AppDatabase> { getDatabase(androidContext()) }
    single<CollectionDatabase> { getCollectionDatabase(androidContext()) }
    single<DataStore<Preferences>> { createDataStoreAndroid(get()) }
}
