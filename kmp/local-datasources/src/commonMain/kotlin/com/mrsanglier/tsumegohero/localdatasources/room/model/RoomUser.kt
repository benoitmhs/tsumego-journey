package com.mrsanglier.tsumegohero.localdatasources.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.user.User

@Entity(tableName = "user")
data class RoomUser(
    @PrimaryKey val userId: String,
    val username: String,
    val rank: String?,
    val dailyStreak: Int,
    val problemStreak: Int,
    val nbRankStepCompleted: Int,
    val bestDailyStreak: Int,
    val nbTsumegoSolved: Int,
) {
    fun toAppModel(): User = User(
        userId = userId,
        username = username,
        rank = rank?.let(Rank::safeValueOf),
        dailyStreak = dailyStreak,
        problemStreak = problemStreak,
        nbRankStepCompleted = nbRankStepCompleted,
        bestDailyStreak = bestDailyStreak,
        nbTsumegoSolved = nbTsumegoSolved,
    )

    companion object {
        fun fromAppModel(appModel: User): RoomUser = RoomUser(
            userId = appModel.userId,
            username = appModel.username,
            rank = appModel.rank?.rawValue,
            dailyStreak = appModel.dailyStreak,
            problemStreak = appModel.problemStreak,
            nbRankStepCompleted = appModel.nbRankStepCompleted,
            bestDailyStreak = appModel.bestDailyStreak,
            nbTsumegoSolved = appModel.nbTsumegoSolved,
        )
    }
}
