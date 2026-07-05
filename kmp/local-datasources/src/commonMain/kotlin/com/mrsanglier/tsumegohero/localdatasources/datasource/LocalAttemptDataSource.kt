package com.mrsanglier.tsumegohero.localdatasources.datasource

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
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

    suspend fun getTsumegoSolvedCount(): Int =
        dao.getTsumegoSolvedCount()

    suspend fun getRankEstimationAttempts(): List<Attempt> =
        dao.getRankEstimationAttempts(GameContext.RankEstimation).map { it.toAppModel() }

    fun observeRankEstimationAttempts(): Flow<List<Attempt>> =
        dao.observeRankEstimationAttempts(GameContext.RankEstimation)
            .map { attempts -> attempts.map { it.toAppModel() } }

    suspend fun deleteRankEstimationAttempts() {
        dao.deleteByContext(GameContext.RankEstimation)
    }

    suspend fun clean() {
        dao.clean()
    }
}
