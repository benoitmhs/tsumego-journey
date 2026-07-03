package com.mrsanglier.tsumegohero.localdatasources.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomAttempt
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomRankEstimationAttemptRow
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {
    @Upsert
    suspend fun upsert(attempt: RoomAttempt)

    @Query("SELECT * FROM attempt WHERE result = 'Success' ORDER BY date DESC LIMIT 1")
    fun observeLastSucceedAttempt(): Flow<RoomAttempt?>

    @Query("SELECT COUNT(DISTINCT tsumegoId) FROM attempt WHERE result = 'Success'")
    suspend fun getTsumegoSolvedCount(): Int

    @Query(
        """
        SELECT a.*, t.rank AS `rank`
        FROM attempt a
        INNER JOIN tsumego t ON a.tsumegoId = t.id
        WHERE a.context = :gameContext
    """
    )
    suspend fun getRankEstimationAttempts(gameContext: GameContext): List<RoomRankEstimationAttemptRow>

    @Query("DELETE FROM attempt WHERE context = :gameContext")
    suspend fun deleteByContext(gameContext: GameContext)

    @Query("DELETE FROM attempt")
    suspend fun clean()
}
