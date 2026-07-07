package com.mrsanglier.tsumegohero.domain.common

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object Config {

    object Game {
        val FLASH_TIME: Duration = 20.seconds
        val CLASSICAL_TIME: Duration = 2.minutes
        val DIFFICULT_TIME: Duration = 7.minutes
    }

    object DailyObjective {
        const val FLASH_TOTAL_OBJECTIVE: Int = 20
        const val CLASSICAL_TOTAL_OBJECTIVE: Int = 5
        const val DIFFICULT_TOTAL_OBJECTIVE: Int = 2
    }
}