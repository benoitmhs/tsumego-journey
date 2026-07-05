package com.mrsanglier.tsumegohero.rankestimation.model

/**
 * @property currentRank: current rank of Tsumego testing
 * @property rankProblemsDone: number of problems done for the current rank
 * @property rankProblemTotal: total number of problems for the current rank
 */
data class RankEstimationProgress(
    val currentRank: Int,
    val rankProblemsDone: Int,
    val rankProblemTotal: Int,
)
