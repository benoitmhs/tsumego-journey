package com.mrsanglier.tsumegohero.localdatasources.datasource

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.localdatasources.room.dao.AttemptDao
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomAttempt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalAttemptDataSource(
    private val dao: AttemptDao,
) {
    suspend fun upsert(attempt: Attempt) {
        dao.upsert(RoomAttempt.fromAppModel(attempt))
    }

    fun observeLastSucceedAttempt(): Flow<Attempt?> =
        dao.observeLastSucceedAttempt().map { it?.toAppModel() }
}