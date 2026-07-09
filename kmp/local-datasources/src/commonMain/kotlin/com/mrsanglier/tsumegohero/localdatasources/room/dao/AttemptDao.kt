package com.mrsanglier.tsumegohero.localdatasources.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomAttempt
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface AttemptDao {
    @Upsert
    suspend fun upsert(attempt: RoomAttempt)

    @Query("SELECT * FROM attempt WHERE result = 'Success' ORDER BY date DESC LIMIT 1")
    fun observeLastSucceedAttempt(): Flow<RoomAttempt?>

    @Query("SELECT COUNT(DISTINCT tsumegoId) FROM attempt WHERE result = 'Success'")
    suspend fun getTsumegoSolvedCount(): Int

    @Query("SELECT * FROM attempt WHERE context = :gameContext ORDER BY date ASC")
    suspend fun getRankEstimationAttempts(gameContext: GameContext): List<RoomAttempt>

    @Query("SELECT * FROM attempt WHERE context = :gameContext ORDER BY date ASC")
    fun observeRankEstimationAttempts(gameContext: GameContext): Flow<List<RoomAttempt>>

    @Query(
        """
        SELECT * FROM attempt
        WHERE context LIKE 'TRAINING:%'
        AND date >= :startOfDay
        AND date < :endOfDay
        ORDER BY date ASC
    """
    )
    fun observeTrainingAttempts(startOfDay: Instant, endOfDay: Instant): Flow<List<RoomAttempt>>

    @Query("SELECT * FROM attempt WHERE context = :gameContext ORDER BY date DESC LIMIT :limit")
    suspend fun getLastAttempts(gameContext: GameContext, limit: Int): List<RoomAttempt>

    @Query("DELETE FROM attempt WHERE context = :gameContext")
    suspend fun deleteByContext(gameContext: GameContext)

    @Query("DELETE FROM attempt")
    suspend fun clean()
}
