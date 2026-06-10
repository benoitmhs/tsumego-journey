package com.mrsanglier.tsumegohero.dashboardgame.model

import com.mrsanglier.tsumegohero.data.model.game.Rank
import kotlin.time.Instant

data class TsumegoItem(
    val title: String,
    val tsumegoId: String,
    val rank: Rank,
    val updatedAt: Instant,
)
