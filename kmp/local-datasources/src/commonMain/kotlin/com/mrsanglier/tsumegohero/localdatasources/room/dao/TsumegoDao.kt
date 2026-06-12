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
    suspend fun getNextTsumego(rank: String): RoomTsumego?

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
