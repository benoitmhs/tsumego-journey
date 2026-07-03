package com.mrsanglier.tsumegohero.rankestimation.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig

data class RankBlockEvaluation(
    val flashPassed: Boolean,
    val classicalPassed: Boolean,
    val difficultPassed: Boolean,
)

class EvaluateRankBlockDelegate {
    operator fun invoke(attempts: List<Attempt>): RankBlockEvaluation {
        val threshold = attempts.size * RankEstimationConfig.SUCCESS_THRESHOLD

        fun successCountWithin(max: kotlin.time.Duration) = attempts.count {
            it.result == Attempt.Result.Success && it.resolutionTimeMs <= max.inWholeMilliseconds
        }

        return RankBlockEvaluation(
            flashPassed = successCountWithin(RankEstimationConfig.FLASH_MAX) >= threshold,
            classicalPassed = successCountWithin(RankEstimationConfig.CLASSICAL_MAX) >= threshold,
            difficultPassed = successCountWithin(RankEstimationConfig.DIFFICULT_MAX) >= threshold,
        )
    }
}
