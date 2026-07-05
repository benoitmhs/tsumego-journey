package com.mrsanglier.tsumegohero.rankestimation.usecase

import com.mrsanglier.tsumegohero.core.error.THAppError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import com.mrsanglier.tsumegohero.rankestimation.delegate.ComputeSearchStateDelegate
import com.mrsanglier.tsumegohero.rankestimation.delegate.ComputeSearchStateDelegateImpl
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first

class GetNextRankEstimationTsumegoUseCase(
    computeSearchStateDelegateImpl: ComputeSearchStateDelegateImpl,
    private val attemptRepository: AttemptRepository,
    private val tsumegoRepository: TsumegoRepository,
    private val userRepository: UserRepository,
) : ComputeSearchStateDelegate by computeSearchStateDelegateImpl {

    suspend operator fun invoke(declaredRank: Rank? = null): THResult<String?> = THResult.catchResult {
        // Check end of rank estimation flow
        if (userRepository.observeUser().first()?.level != null) return@catchResult null

        val attempts = attemptRepository.getRankEstimationAttempts()
        // The first problem is served at the declared rank, so the seed stays recoverable from history
        val seedRank = attempts.firstOrNull()?.rank
            ?: declaredRank
            ?: RankEstimationConfig.DEFAULT_SEED_RANK
        val rank = computeSearchState(attempts, seedRank).nextRank
            ?: return@catchResult null

        tsumegoRepository.getRandomNeverAttempted(rank)?.id
            ?: throw THAppError.Code.ObjectNotFound.toError(message = "No tsumego found")
    }
}
