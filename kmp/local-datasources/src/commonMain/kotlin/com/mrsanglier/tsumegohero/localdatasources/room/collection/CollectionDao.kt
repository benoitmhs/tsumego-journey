package com.mrsanglier.tsumegohero.localdatasources.room.collection

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Upsert
    suspend fun upsertCollection(collection: RoomCollection)

    @Query(
        """
        SELECT c.*, COUNT(t.id) AS problemCount
        FROM collection c
        LEFT JOIN user_tsumego t ON t.collectionId = c.id
        GROUP BY c.id
        ORDER BY c.createdAt DESC
    """
    )
    fun observeCollections(): Flow<List<RoomCollectionWithCount>>

    @Query("SELECT * FROM collection WHERE id = :id LIMIT 1")
    fun observeCollection(id: String): Flow<RoomCollection?>

    @Upsert
    suspend fun upsertTsumego(tsumego: RoomUserTsumego)

    @Query("SELECT * FROM user_tsumego WHERE collectionId = :collectionId ORDER BY createdAt ASC")
    fun observeTsumegos(collectionId: String): Flow<List<RoomUserTsumego>>

    @Query("SELECT * FROM user_tsumego WHERE id = :id LIMIT 1")
    fun observeTsumego(id: String): Flow<RoomUserTsumego?>

    @Query("DELETE FROM user_tsumego WHERE id = :id")
    suspend fun deleteTsumego(id: String)
}
