package com.mrsanglier.tsumegohero.localdatasources.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.data.model.user.User

@Entity(tableName = "user")
data class RoomUser(
    @PrimaryKey val userId: String,
    val username: String,
    val flashRank: String?,
    val classicalRank: String?,
    val difficultRank: String?,
    val dailyStreak: Int,
    val problemStreak: Int,
    val nbRankStepCompleted: Int,
    val bestDailyStreak: Int,
    val nbTsumegoSolved: Int,
) {
    fun toAppModel(): User = User(
        userId = userId,
        username = username,
        level = getLevel(),
        dailyStreak = dailyStreak,
        problemStreak = problemStreak,
        nbRankStepCompleted = nbRankStepCompleted,
        bestDailyStreak = bestDailyStreak,
        nbTsumegoSolved = nbTsumegoSolved,
    )

    private fun getLevel(): Level? {
        val flash = flashRank?.let(Rank::safeValueOf) ?: return null
        val classical = classicalRank?.let(Rank::safeValueOf) ?: return null
        val difficult = difficultRank?.let(Rank::safeValueOf) ?: return null
        return Level(
            flashRank = flash,
            classicalRank = classical,
            difficultRank = difficult,
        )
    }

    companion object {
        fun fromAppModel(appModel: User): RoomUser = RoomUser(
            userId = appModel.userId,
            username = appModel.username,
            flashRank = appModel.level?.flashRank?.rawValue,
            classicalRank = appModel.level?.classicalRank?.rawValue,
            difficultRank = appModel.level?.difficultRank?.rawValue,
            dailyStreak = appModel.dailyStreak,
            problemStreak = appModel.problemStreak,
            nbRankStepCompleted = appModel.nbRankStepCompleted,
            bestDailyStreak = appModel.bestDailyStreak,
            nbTsumegoSolved = appModel.nbTsumegoSolved,
        )
    }
}
