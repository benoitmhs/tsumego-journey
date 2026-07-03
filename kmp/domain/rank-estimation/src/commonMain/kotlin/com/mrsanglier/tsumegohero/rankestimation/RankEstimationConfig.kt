package com.mrsanglier.tsumegohero.rankestimation

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal object RankEstimationConfig {
    val FLASH_TIME = 20.seconds
    val CLASSICAL_TIME = 2.minutes
    val DIFFICULT_TIME = 7.minutes
    const val BLOCK_SIZE = 5
    const val SUCCESS_THRESHOLD = 0.8
}
