package com.mrsanglier.tsumegohero.data.model.user

data class User(
    val userId: String,
    val username: String,
    val level: Level?,
    val dailyStreak: Int,
    val problemStreak: Int,
    val nbRankStepCompleted: Int,
    val bestDailyStreak: Int,
    val nbTsumegoSolved: Int,
)