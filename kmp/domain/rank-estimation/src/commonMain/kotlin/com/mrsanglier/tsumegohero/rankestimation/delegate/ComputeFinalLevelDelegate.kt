package com.mrsanglier.tsumegohero.rankestimation.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ComputeFinalLevelDelegate {
    operator fun invoke(attempts: List<Pair<Rank, Attempt>>): Level {
        val attempsResults = attempts
            .groupBy({ it.first }, { it.second })
            .map { (rank, attemptsByRank) ->
                val averageResTime = attemptsByRank.map { it.resolutionTimeMs }.average()
                val successRate = attemptsByRank.count { it.result == Attempt.Result.Success } / attemptsByRank.size.toFloat()
                Triple(rank, averageResTime, successRate)
            }
        attempsResults.forEach {
            println("${it.first}  ${it.second.milliseconds.inWholeSeconds}  ${it.third}")
        }

        val flashRank = attempsResults.getMaxRankValidate(RankEstimationConfig.FLASH_TIME)
        val classicalRank = attempsResults.getMaxRankValidate(RankEstimationConfig.CLASSICAL_TIME)
        val difficultRank = attempsResults.getMaxRankValidate(RankEstimationConfig.DIFFICULT_TIME)

        return Level(
            flashRank = flashRank ?: Rank.entries.first(),
            classicalRank = classicalRank ?: Rank.entries.first(),
            difficultRank = difficultRank ?: Rank.entries.first(),
        )
    }

    private fun List<Triple<Rank, Double, Float>>.getMaxRankValidate(timeRequirest: Duration): Rank? =
        this
            .filter { (_, averageResTime, successRate) ->
                averageResTime <= timeRequirest.inWholeMilliseconds
                    && successRate >= RankEstimationConfig.SUCCESS_THRESHOLD
            }
            .maxOfWithOrNull(Rank.comparator) { it.first }
}
