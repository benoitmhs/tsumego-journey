package com.mrsanglier.tsumegohero.rankestimation

import com.mrsanglier.tsumegohero.data.model.game.Rank
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal object RankEstimationConfig {
    val FLASH_TIME = 20.seconds
    val CLASSICAL_TIME = 2.minutes
    val DIFFICULT_TIME = 7.minutes
    const val CADRAGE_STEP = 8
    const val CADRAGE_BLOCK_SIZE = 2
    const val DICHOTOMY_BLOCK_SIZE = 3
    const val BLOCK_SUCCESS_QUORUM = 2
    const val PROBLEM_CAP = 25

    // 1 kyu = 2 rank entries (each kyu/dan has a "+" half rank)
    const val TIER_MIN_GAP = 2
    val DEFAULT_SEED_RANK = Rank.`15K`
}
