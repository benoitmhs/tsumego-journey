package com.mrsanglier.tsumegohero.rankestimation

import com.mrsanglier.tsumegohero.data.model.game.Rank
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal object RankEstimationConfig {
    val FLASH_TIME = 20.seconds
    val CLASSICAL_TIME = 2.minutes
    val DIFFICULT_TIME = 7.minutes
    const val CADRAGE_STEP = 8

    // A block fails at 2 failures. It is validated at 2 successes when clean,
    // 3 successes when it contains a failure. Tier time thresholds then apply
    // to the average resolution time of the block's solved problems.
    const val BLOCK_FAILURE_LIMIT = 2
    const val BLOCK_CLEAN_SUCCESS_COUNT = 2
    const val BLOCK_RECOVERY_SUCCESS_COUNT = 3
    const val BLOCK_MAX_SIZE = BLOCK_RECOVERY_SUCCESS_COUNT + BLOCK_FAILURE_LIMIT - 1
    const val PROBLEM_CAP = 25

    // 1 kyu = 2 rank entries (each kyu/dan has a "+" half rank)
    const val TIER_MIN_GAP = 2
    val DEFAULT_SEED_RANK = Rank.`15K`
}
