package com.mrsanglier.tsumegohero.repository

import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollection
import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollectionSummary
import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.localdatasources.datasource.LocalCollectionDatasource
import kotlinx.coroutines.flow.Flow

class CollectionRepository(
    private val localCollectionDatasource: LocalCollectionDatasource,
) {
    suspend fun upsertCollection(collection: TsumegoCollection) {
        localCollectionDatasource.upsertCollection(collection)
    }

    fun observeCollections(): Flow<List<TsumegoCollectionSummary>> =
        localCollectionDatasource.observeCollections()

    fun observeCollection(id: String): Flow<TsumegoCollection?> =
        localCollectionDatasource.observeCollection(id)

    suspend fun upsertTsumego(tsumego: UserTsumego) {
        localCollectionDatasource.upsertTsumego(tsumego)
    }

    fun observeTsumegos(collectionId: String): Flow<List<UserTsumego>> =
        localCollectionDatasource.observeTsumegos(collectionId)

    fun observeTsumego(id: String): Flow<UserTsumego?> =
        localCollectionDatasource.observeTsumego(id)

    suspend fun deleteTsumego(id: String) {
        localCollectionDatasource.deleteTsumego(id)
    }
}
