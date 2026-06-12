package com.mrsanglier.tsumegohero.localdatasources.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun upsert(user: RoomUser)

    @Query("SELECT * FROM user LIMIT 1")
    fun observeUser(): Flow<RoomUser?>

    @Query("DELETE FROM user")
    suspend fun clean()
}
