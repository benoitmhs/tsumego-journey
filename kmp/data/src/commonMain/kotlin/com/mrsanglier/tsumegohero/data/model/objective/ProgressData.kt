package com.mrsanglier.tsumegohero.data.model.objective

import com.mrsanglier.tsumegohero.data.model.game.Rank

data class ProgressData(
    val rank: Rank,
    val rankStepCompleted: Int,
    val rankStepRequired: Int,
    val problemStreak: Int,
    val problemStreakRequired: Int,
)