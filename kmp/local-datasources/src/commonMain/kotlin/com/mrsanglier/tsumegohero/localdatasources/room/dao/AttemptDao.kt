package com.mrsanglier.tsumegohero.localdatasources.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomAttempt
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {
    @Upsert
    suspend fun upsert(attempt: RoomAttempt)

    @Query("SELECT * FROM attempt WHERE success = 1 ORDER BY date DESC LIMIT 1")
    fun observeLastSucceedAttempt(): Flow<RoomAttempt?>

    @Query("SELECT COUNT(DISTINCT tsumegoId) FROM attempt WHERE success = 1")
    suspend fun getTsumegoSolvedCount(): Int

    @Query("DELETE FROM attempt")
    suspend fun clean()
}
