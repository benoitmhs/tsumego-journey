package com.mrsanglier.tsumegohero.data.model.user

import com.mrsanglier.tsumegohero.data.model.game.Rank

data class User(
    val userId: String,
    val username: String,
    val rank: Rank?,
    val dailyStreak: Int,
    val stepSuccessStreak: Int,
    val nbRankStepCompleted: Int,
    val bestDailyStreak: Int,
    val nbTsumegoSolved: Int,
)