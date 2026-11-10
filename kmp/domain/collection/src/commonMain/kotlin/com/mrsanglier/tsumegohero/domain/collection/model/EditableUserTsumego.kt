package com.mrsanglier.tsumegohero.domain.collection.model

import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Stone

/** A saved problem, split into the fields of the edition flow. */
data class EditableUserTsumego(
    val boardSize: BoardSize,
    val blackStones: Set<Cell>,
    val whiteStones: Set<Cell>,
    val firstPlayer: Stone,
    val tree: SolutionTree,
    val title: String?,
    val rank: Rank?,
    val notes: String?,
)
