package com.mrsanglier.tsumegohero.data.model.user

data class DailyStreak(
    val streakCount: Int,
    val status: Status,
) {
    enum class Status {
        Today, Hot, Cold, Unactive;
    }
}