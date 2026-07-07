package com.mrsanglier.tsumegohero.repository

import com.mrsanglier.tsumegohero.core.extension.todayInterval
import com.mrsanglier.tsumegohero.data.model.game.Attempt
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

    suspend fun getRankEstimationAttempts(): List<Attempt> =
        localAttemptDataSource.getRankEstimationAttempts()

    fun observeRankEstimationAttempts(): Flow<List<Attempt>> =
        localAttemptDataSource.observeRankEstimationAttempts()

    fun observeTodayTrainingAttempts(): Flow<List<Attempt>> {
        val today = todayInterval()
        return localAttemptDataSource.observeTrainingAttempts(
            startOfDay = today.start,
            endOfDay = today.endExclusive,
        )
    }

    suspend fun deleteRankEstimationAttempts() {
        localAttemptDataSource.deleteRankEstimationAttempts()
    }
}
