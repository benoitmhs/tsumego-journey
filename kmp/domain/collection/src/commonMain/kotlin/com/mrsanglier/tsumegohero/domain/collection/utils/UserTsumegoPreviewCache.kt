package com.mrsanglier.tsumegohero.domain.collection.utils

import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.domain.collection.model.UserTsumegoPreview
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.CropBoard

/** Part of a preview that depends only on the SGF, the costly part to build. */
data class PreviewGeometry(
    val boardSize: BoardSize,
    val blackStones: Set<Cell>,
    val whiteStones: Set<Cell>,
    val cropBoard: CropBoard?,
)

/**
 * Builds previews, reading the SGF of a problem again only when it changed.
 * Not thread safe: use one instance per flow collection.
 *
 * @param buildGeometry returns null for an unreadable SGF (the failure is cached too)
 */
class UserTsumegoPreviewCache(
    private val buildGeometry: (UserTsumego) -> PreviewGeometry?,
) {
    private class Entry(val sgf: String, val geometry: PreviewGeometry?)

    private val entries = mutableMapOf<String, Entry>()

    /** @return the previews of the readable problems, in the same order */
    fun previews(tsumegos: List<UserTsumego>): List<UserTsumegoPreview> {
        // Deleted problems leave the cache
        entries.keys.retainAll(tsumegos.mapTo(HashSet()) { it.id })
        return tsumegos.mapNotNull { tsumego ->
            val entry = entries[tsumego.id]?.takeIf { it.sgf == tsumego.sgf }
                ?: Entry(tsumego.sgf, buildGeometry(tsumego)).also { entries[tsumego.id] = it }
            // Title and rank are columns, read each time: renaming does not read the SGF again
            entry.geometry?.let { geometry ->
                UserTsumegoPreview(
                    id = tsumego.id,
                    title = tsumego.title,
                    rank = tsumego.rank,
                    boardSize = geometry.boardSize,
                    blackStones = geometry.blackStones,
                    whiteStones = geometry.whiteStones,
                    cropBoard = geometry.cropBoard,
                )
            }
        }
    }
}
