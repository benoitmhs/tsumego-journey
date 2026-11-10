package com.mrsanglier.tsumegohero.localdatasources

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mrsanglier.tsumegohero.localdatasources.room.collection.CollectionDatabase
import com.mrsanglier.tsumegohero.localdatasources.utils.CollectionDatabasePath
import kotlinx.coroutines.Dispatchers

/** No destructive fallback: this database holds user content, future schema changes need real migrations. */
fun getCollectionDatabase(ctx: Context): CollectionDatabase {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(CollectionDatabasePath)
    return Room.databaseBuilder<CollectionDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
