package com.mrsanglier.tsumegohero.rankestimation.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig

class ComputeFinalLevelDelegate(
    private val evaluateRankBlockDelegate: EvaluateRankBlockDelegate,
) {
    operator fun invoke(attempts: List<Pair<Rank, Attempt>>): Level? {
        val attemptsByRank = attempts.groupBy({ it.first }, { it.second })

        val validatedBlocks = Rank.entries.asSequence()
            .map { rank -> rank to attemptsByRank[rank] }
            .takeWhile { (_, rankAttempts) -> (rankAttempts?.size ?: 0) >= RankEstimationConfig.BLOCK_SIZE }
            .map { (rank, rankAttempts) -> rank to evaluateRankBlockDelegate(rankAttempts.orEmpty()) }
            .takeWhile { (_, evaluation) -> evaluation.difficultPassed }
            .toList()

        val difficultRank = validatedBlocks.lastOrNull()?.first ?: return null
        val classicalRank = validatedBlocks.lastOrNull { (_, evaluation) -> evaluation.classicalPassed }?.first
        val flashRank = validatedBlocks.lastOrNull { (_, evaluation) -> evaluation.flashPassed }?.first

        return Level(
            flashRank = flashRank ?: Rank.entries.first(),
            classicalRank = classicalRank ?: Rank.entries.first(),
            difficultRank = difficultRank,
        )
    }
}
