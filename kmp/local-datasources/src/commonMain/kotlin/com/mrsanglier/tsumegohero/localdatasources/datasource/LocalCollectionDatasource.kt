package com.mrsanglier.tsumegohero.localdatasources.datasource

import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollection
import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollectionSummary
import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.localdatasources.room.collection.CollectionDao
import com.mrsanglier.tsumegohero.localdatasources.room.collection.RoomCollection
import com.mrsanglier.tsumegohero.localdatasources.room.collection.RoomUserTsumego
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalCollectionDatasource(
    private val dao: CollectionDao,
) {
    suspend fun upsertCollection(collection: TsumegoCollection) {
        dao.upsertCollection(RoomCollection.fromAppModel(collection))
    }

    fun observeCollections(): Flow<List<TsumegoCollectionSummary>> =
        dao.observeCollections().map { roomModels -> roomModels.map { it.toAppModel() } }

    fun observeCollection(id: String): Flow<TsumegoCollection?> =
        dao.observeCollection(id).map { it?.toAppModel() }

    suspend fun upsertTsumego(tsumego: UserTsumego) {
        dao.upsertTsumego(RoomUserTsumego.fromAppModel(tsumego))
    }

    fun observeTsumegos(collectionId: String): Flow<List<UserTsumego>> =
        dao.observeTsumegos(collectionId).map { roomModels -> roomModels.map { it.toAppModel() } }
}
