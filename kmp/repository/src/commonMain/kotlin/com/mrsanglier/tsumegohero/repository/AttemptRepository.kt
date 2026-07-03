package com.mrsanglier.tsumegohero.repository

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.localdatasources.datasource.LocalAttemptDataSource
import kotlinx.coroutines.flow.Flow

class AttemptRepository(
    private val localAttemptDataSource: LocalAttemptDataSource,
) {
    suspend fun upsert(attempt: Attempt) {
        localAttemptDataSource.upsert(attempt)
    }

    fun observeLastSucceedAttempt(): Flow<Attempt?> =
        localAttemptDataSource.observeLastSucceedAttempt()

    suspend fun getTsumegoSolvedCount(): Int =
        localAttemptDataSource.getTsumegoSolvedCount()

    suspend fun getRankEstimationAttempts(): List<Pair<Rank, Attempt>> =
        localAttemptDataSource.getRankEstimationAttempts()

    suspend fun deleteRankEstimationAttempts() {
        localAttemptDataSource.deleteRankEstimationAttempts()
    }
}
