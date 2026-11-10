package com.mrsanglier.tsumegohero.data.model.collection

import com.mrsanglier.tsumegohero.data.model.game.Rank
import kotlin.time.Instant

/**
 * A problem created or imported by the user, stored in one of their collections.
 *
 * @property sgf the problem in SGF format (initial position + solution tree)
 */
data class UserTsumego(
    val id: String,
    val collectionId: String,
    val title: String?,
    val rank: Rank?,
    val notes: String?,
    val sgf: String,
    val createdAt: Instant,
)
