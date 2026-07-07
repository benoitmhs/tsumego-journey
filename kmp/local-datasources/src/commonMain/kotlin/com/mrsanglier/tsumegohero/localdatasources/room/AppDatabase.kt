package com.mrsanglier.tsumegohero.localdatasources.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.mrsanglier.tsumegohero.localdatasources.room.converters.GameContextConverter
import com.mrsanglier.tsumegohero.localdatasources.room.dao.AttemptDao
import com.mrsanglier.tsumegohero.localdatasources.room.dao.TsumegoDao
import com.mrsanglier.tsumegohero.localdatasources.room.dao.UserDao
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomAttempt
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomUser
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomTsumego

@Database(
    entities = [
        RoomAttempt::class,
        RoomTsumego::class,
        RoomUser::class,
    ],
    version = 1
)
@TypeConverters(
    InstantTypeConverter::class,
    DateTimeConverter::class,
    GameContextConverter::class,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attemptDao(): AttemptDao
    abstract fun tsumegoDao(): TsumegoDao
    abstract fun userDao(): UserDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
