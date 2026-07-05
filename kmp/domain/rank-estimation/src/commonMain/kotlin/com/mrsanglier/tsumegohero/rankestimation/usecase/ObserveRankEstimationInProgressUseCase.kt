package com.mrsanglier.tsumegohero.rankestimation.usecase

import com.mrsanglier.tsumegohero.repository.AttemptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveRankEstimationInProgressUseCase(
    private val attemptRepository: AttemptRepository,
) {
    operator fun invoke(): Flow<Boolean> =
        attemptRepository.observeRankEstimationAttempts()
            .map { attempts -> attempts.isNotEmpty() }
            .distinctUntilChanged()
}
