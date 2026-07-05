package com.mrsanglier.tsumegohero.rankestimation.model

import com.mrsanglier.tsumegohero.data.model.game.Rank

/**
 * @property currentRank: rank of the Tsumego currently tested
 * @property problemsDone: number of problems done since the beginning of the estimation
 * @property problemsExpected: estimated total number of problems for the whole estimation
 */
data class RankEstimationProgress(
    val currentRank: Rank,
    val problemsDone: Int,
    val problemsExpected: Int,
)
