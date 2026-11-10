package com.mrsanglier.tsumegohero.localdatasources

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mrsanglier.tsumegohero.localdatasources.room.collection.CollectionDatabase
import com.mrsanglier.tsumegohero.localdatasources.utils.CollectionDatabasePath
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/** No destructive fallback: this database holds user content, future schema changes need real migrations. */
@OptIn(ExperimentalForeignApi::class)
fun getCollectionDatabase(): CollectionDatabase {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    val path = requireNotNull(documentDirectory?.path) + "/$CollectionDatabasePath"

    return Room.databaseBuilder<CollectionDatabase>(name = path)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
