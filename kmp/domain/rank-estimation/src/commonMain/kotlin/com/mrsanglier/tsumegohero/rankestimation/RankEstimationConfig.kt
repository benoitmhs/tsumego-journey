package com.mrsanglier.tsumegohero.rankestimation

import com.mrsanglier.tsumegohero.data.model.game.Rank
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal object RankEstimationConfig {
    val FLASH_TIME = 20.seconds
    val CLASSICAL_TIME = 2.minutes
    val DIFFICULT_TIME = 7.minutes
    const val CADRAGE_STEP = 8

    // A block is decided on raw results: 2 successes validate it, 2 failures fail it,
    // a 3rd problem breaks a 1-1 tie. Tier time thresholds then apply to the average
    // resolution time of the block's solved problems.
    const val BLOCK_DECISION_COUNT = 2
    const val BLOCK_MAX_SIZE = 2 * BLOCK_DECISION_COUNT - 1
    const val PROBLEM_CAP = 25

    // 1 kyu = 2 rank entries (each kyu/dan has a "+" half rank)
    const val TIER_MIN_GAP = 2
    val DEFAULT_SEED_RANK = Rank.`15K`
}
