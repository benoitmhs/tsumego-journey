package com.mrsanglier.tsumegohero.localdatasources.room.collection

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.mrsanglier.tsumegohero.localdatasources.room.InstantTypeConverter

/**
 * User collections live in their own database: the main [com.mrsanglier.tsumegohero.localdatasources.room.AppDatabase]
 * file is overwritten by the bundled tsumego asset on each asset version bump, which would wipe user data.
 */
@Database(
    entities = [
        RoomCollection::class,
        RoomUserTsumego::class,
    ],
    version = 1,
)
@TypeConverters(InstantTypeConverter::class)
@ConstructedBy(CollectionDatabaseConstructor::class)
abstract class CollectionDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object CollectionDatabaseConstructor : RoomDatabaseConstructor<CollectionDatabase> {
    override fun initialize(): CollectionDatabase
}
