package com.mrsanglier.tsumegohero.rankestimation.usecase

import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first

class GetNextRankEstimationTsumegoUseCase(
    private val userRepository: UserRepository,
    private val attemptRepository: AttemptRepository,
    private val tsumegoRepository: TsumegoRepository,
) {
    suspend operator fun invoke(): THResult<String?> = THResult.catchResult {
        val user = userRepository.observeUser().first()
        if (user?.level != null) return@catchResult null

        val attempts = attemptRepository.getRankEstimationAttempts()
        val rank = currentRank(attempts) ?: return@catchResult null

        tsumegoRepository.getNextNeverAttempted(rank)?.id
    }

    private fun currentRank(attempts: List<Pair<Rank, Attempt>>): Rank? {
        val ranksAttempted = attempts.map { it.first }
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
