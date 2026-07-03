package com.mrsanglier.tsumegohero.localdatasources.room.model

import androidx.room.Embedded
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.Rank

data class RoomRankEstimationAttemptRow(
    @Embedded val attempt: RoomAttempt,
    val rank: String,
) {
    fun toAppModel(): Pair<Rank, Attempt> = Rank.safeValueOf(rank)!! to attempt.toAppModel()
}
