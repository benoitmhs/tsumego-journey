package com.mrsanglier.tsumegohero.rankestimation

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal object RankEstimationConfig {
    val FLASH_MAX = 20.seconds
    val CLASSICAL_MAX = 2.minutes
    val DIFFICULT_MAX = 7.minutes
    const val BLOCK_SIZE = 5
    const val SUCCESS_THRESHOLD = 0.8
}
