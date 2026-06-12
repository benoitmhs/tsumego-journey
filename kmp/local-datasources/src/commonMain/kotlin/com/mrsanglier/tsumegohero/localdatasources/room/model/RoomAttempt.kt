package com.mrsanglier.tsumegohero.localdatasources.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import kotlin.time.Instant

@Entity(tableName = "attempt")
class RoomAttempt(
    @PrimaryKey val id: String,
    val userId: String,
    val tsumegoId: String,
    val success: Boolean,
    val mode: Attempt.Mode,     // Standard, Blind
    val date: Instant,
) {
    fun toAppModel(): Attempt = Attempt(
        id = id,
        userId = userId,
        tsumegoId = tsumegoId,
        success = success,
        mode = mode,
        date = date,
    )

    companion object {
        fun fromAppModel(appModel: Attempt): RoomAttempt = RoomAttempt(
            id = appModel.id,
            userId = appModel.userId,
            tsumegoId = appModel.tsumegoId,
            success = appModel.success,
            date = appModel.date,
            mode = appModel.mode,
        )
    }
}