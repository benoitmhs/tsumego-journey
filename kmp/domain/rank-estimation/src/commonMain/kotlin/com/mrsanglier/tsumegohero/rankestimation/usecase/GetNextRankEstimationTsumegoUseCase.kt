package com.mrsanglier.tsumegohero.rankestimation.usecase

import com.mrsanglier.tsumegohero.core.error.THAppError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.domain.common.delegate.GetNextTsumegoFromRankDelegate
import com.mrsanglier.tsumegohero.domain.common.delegate.GetNextTsumegoFromRankDelegateImpl
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first

class GetNextRankEstimationTsumegoUseCase(
    getNextTsumegoFromRankDelegateImpl: GetNextTsumegoFromRankDelegateImpl,
    private val attemptRepository: AttemptRepository,
    private val userRepository: UserRepository,
) : GetNextTsumegoFromRankDelegate by getNextTsumegoFromRankDelegateImpl {

    suspend operator fun invoke(): THResult<String?> = THResult.catchResult {
        // Check end of rank estimation flow
        if (userRepository.observeUser().first()?.level != null) return@catchResult null

        val attempts = attemptRepository.getRankEstimationAttempts()
        val rank = currentRank(attempts) ?: Rank.entries.first()

        getNextTsumegoFromRank(rank)?.id
            ?: throw THAppError.Code.ObjectNotFound.toError(message = "No tsumego found")
    }

    private fun currentRank(attempts: List<Attempt>): Rank? {
        val ranksAttempted = attempts.map { it.rank }
        val maxRank = ranksAttempted.maxWithOrNull(Rank.comparator)
            ?: Rank.entries.first()

        val countAtMaxRank = ranksAttempted.count { it == maxRank }
        return if (countAtMaxRank >= RankEstimationConfig.BLOCK_SIZE) {
            Rank.entries.getOrNull(Rank.entries.indexOf(maxRank) + 1)
        } else {
            maxRank
        }
    }
}
