package com.mrsanglier.tsumegohero.localdatasources.room.collection

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollection
import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollectionSummary
import kotlin.time.Instant

@Entity(tableName = "collection")
data class RoomCollection(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Instant,
) {
    fun toAppModel(): TsumegoCollection = TsumegoCollection(
        id = id,
        name = name,
        createdAt = createdAt,
    )

    companion object {
        fun fromAppModel(appModel: TsumegoCollection): RoomCollection = RoomCollection(
            id = appModel.id,
            name = appModel.name,
            createdAt = appModel.createdAt,
        )
    }
}

data class RoomCollectionWithCount(
    @Embedded val collection: RoomCollection,
    val problemCount: Int,
) {
    fun toAppModel(): TsumegoCollectionSummary = TsumegoCollectionSummary(
        collection = collection.toAppModel(),
        problemCount = problemCount,
    )
}
