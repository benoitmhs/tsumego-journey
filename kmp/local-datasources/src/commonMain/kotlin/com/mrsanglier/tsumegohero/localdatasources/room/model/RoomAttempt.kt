package com.mrsanglier.tsumegohero.localdatasources.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import kotlin.time.Instant

@Entity(tableName = "attempt")
class RoomAttempt(
    @PrimaryKey val id: String,
    val userId: String,
    val tsumegoId: String,
    val rank: String,
    val result: Attempt.Result,
    val context: GameContext,
    val date: Instant,
    val resolutionTimeMs: Long,
) {
    fun toAppModel(): Attempt = Attempt(
        id = id,
        userId = userId,
        tsumegoId = tsumegoId,
        rank = Rank.safeValueOf(rank)!!,
        result = result,
        date = date,
        context = context,
        resolutionTimeMs = resolutionTimeMs,
    )

    companion object {
        fun fromAppModel(appModel: Attempt): RoomAttempt = RoomAttempt(
            id = appModel.id,
            userId = appModel.userId,
            tsumegoId = appModel.tsumegoId,
            rank = appModel.rank.rawValue,
            result = appModel.result,
            date = appModel.date,
            context = appModel.context,
            resolutionTimeMs = appModel.resolutionTimeMs,
        )
    }
}