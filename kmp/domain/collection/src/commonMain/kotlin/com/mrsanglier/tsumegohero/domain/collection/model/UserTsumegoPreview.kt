package com.mrsanglier.tsumegohero.domain.collection.model

import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.CropBoard

/** What is needed to display a user problem in a list: its initial position and its metadata. */
data class UserTsumegoPreview(
    val id: String,
    val title: String?,
    val rank: Rank?,
    val boardSize: BoardSize,
    val blackStones: Set<Cell>,
    val whiteStones: Set<Cell>,
    val cropBoard: CropBoard?,
)
