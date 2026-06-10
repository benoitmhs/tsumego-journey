package com.mrsanglier.tsumegohero.localdatasources

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mrsanglier.tsumegohero.localdatasources.room.AppDatabase
import com.mrsanglier.tsumegohero.localdatasources.utils.TsumegoDatabasePath
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getAppDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
        name = databasePath(),
    )
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}

@OptIn(ExperimentalForeignApi::class)
fun databasePath(): String {
    val documentDirectory =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )

    return requireNotNull(documentDirectory?.path) + "/$TsumegoDatabasePath"
}

fun getDatabase(): AppDatabase {
    return getAppDatabaseBuilder().build()
}
