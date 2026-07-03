package com.mrsanglier.tsumegohero.localdatasources.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mrsanglier.tsumegohero.localdatasources.room.model.RankCount
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomTsumego
import kotlinx.coroutines.flow.Flow

@Dao
interface TsumegoDao {
    @Upsert
    suspend fun upsert(tsumegos: List<RoomTsumego>)

    @Query("SELECT * FROM tsumego")
    fun observeAllGames(): Flow<List<RoomTsumego>>

    @Query("SELECT * FROM tsumego WHERE id = :id LIMIT 1")
    fun observeGame(id: String): Flow<RoomTsumego>

    @Query(
        """
        SELECT * FROM tsumego 
        WHERE rank = :rank 
        AND id NOT IN (SELECT tsumegoId FROM attempt) 
        LIMIT 1
    """
    )
    suspend fun getNextNeverAttempted(rank: String): RoomTsumego?

    @Query(
        """
        SELECT * FROM tsumego 
        WHERE rank = :rank 
        AND id NOT IN (SELECT tsumegoId FROM attempt WHERE success = 1) 
        LIMIT 1
    """
    )
    suspend fun getNextNeverSucceeded(rank: String): RoomTsumego?

    @Query(
        """
        SELECT t.* FROM tsumego t
        INNER JOIN attempt a ON t.id = a.tsumegoId
        WHERE t.rank = :rank
        ORDER BY a.date ASC
        LIMIT 1
    """
    )
    suspend fun getOldestAttempted(rank: String): RoomTsumego?

    @Query(
        """
        SELECT
            rank,
            COUNT(*) AS total
        FROM tsumego
        GROUP BY rank
        ORDER BY rank;
    """
    )
    suspend fun countRanks(): List<RankCount>

    @Query("DELETE FROM tsumego")
    suspend fun clean()
}
