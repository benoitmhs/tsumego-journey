package com.mrsanglier.tsumegohero.rankestimation.usecase

import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import com.mrsanglier.tsumegohero.rankestimation.delegate.ComputeSearchStateDelegate
import com.mrsanglier.tsumegohero.rankestimation.delegate.ComputeSearchStateDelegateImpl
import com.mrsanglier.tsumegohero.rankestimation.model.RankEstimationProgress
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveRankEstimationProgressUseCase(
    computeSearchStateDelegateImpl: ComputeSearchStateDelegateImpl,
    private val attemptRepository: AttemptRepository,
) : ComputeSearchStateDelegate by computeSearchStateDelegateImpl {

    operator fun invoke(): Flow<RankEstimationProgress> =
        attemptRepository.observeRankEstimationAttempts()
            .map { attempts ->
                val seedRank = attempts.firstOrNull()?.rank ?: RankEstimationConfig.DEFAULT_SEED_RANK
                val searchState = computeSearchState(attempts, seedRank)
                RankEstimationProgress(
                    currentRank = searchState.nextRank ?: attempts.lastOrNull()?.rank ?: seedRank,
                    problemsDone = searchState.problemsDone,
                    problemsExpected = searchState.problemsExpected,
                )
            }
            .distinctUntilChanged()
}
