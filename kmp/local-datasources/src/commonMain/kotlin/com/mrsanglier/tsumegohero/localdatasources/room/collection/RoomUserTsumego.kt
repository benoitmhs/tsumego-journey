package com.mrsanglier.tsumegohero.localdatasources.room.collection

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.data.model.game.Rank
import kotlin.time.Instant

@Entity(
    tableName = "user_tsumego",
    foreignKeys = [
        ForeignKey(
            entity = RoomCollection::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("collectionId")],
)
data class RoomUserTsumego(
    @PrimaryKey val id: String,
    val collectionId: String,
    val title: String?,
    val rank: String?,
    val notes: String?,
    val sgf: String,
    val createdAt: Instant,
) {
    fun toAppModel(): UserTsumego = UserTsumego(
        id = id,
        collectionId = collectionId,
        title = title,
        rank = rank?.let(Rank::safeValueOf),
        notes = notes,
        sgf = sgf,
        createdAt = createdAt,
    )

    companion object {
        fun fromAppModel(appModel: UserTsumego): RoomUserTsumego = RoomUserTsumego(
            id = appModel.id,
            collectionId = appModel.collectionId,
            title = appModel.title,
            rank = appModel.rank?.rawValue,
            notes = appModel.notes,
            sgf = appModel.sgf,
            createdAt = appModel.createdAt,
        )
    }
}
