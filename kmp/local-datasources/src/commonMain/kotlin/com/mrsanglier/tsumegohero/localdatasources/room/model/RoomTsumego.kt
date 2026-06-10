package com.mrsanglier.tsumegohero.localdatasources.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import kotlin.time.Instant

@Entity(tableName = "tsumego")
data class RoomTsumego(
    @PrimaryKey val id: String,
    val name: String,
    val data: String,
    val updatedAt: Instant,
    val rank: String,
) {
    fun toAppModel(): RawTsumego = RawTsumego(
        id = id,
        name = name,
        data = data,
        updatedAt = updatedAt,
        rank = Rank.safeValueOf(rank)!!,
    )

    companion object {
        fun fromAppModel(appModel: RawTsumego): RoomTsumego = RoomTsumego(
            id = appModel.id,
            name = appModel.name,
            data = appModel.data,
            updatedAt = appModel.updatedAt,
            rank = appModel.rank.rawValue,
        )
    }
}
