package com.mrsanglier.tsumegohero.game.model

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego

data class Game(
    val sgf: RawTsumego,
    val board: Board,
    val moveStack: List<MoveNode> = emptyList(),
    val tsumego: Tsumego,
    val cropBoard: CropBoard,
    val reviewRoot: RootNode? = null,
    val reviewIndex: Int = 0,
    val mode: GameMode = GameMode.Standard,
    val isGhostSubmitted: Boolean = false,
) {
    val isGhostMode: Boolean
        get() = mode == GameMode.Ghost

    val playerStone: Stone
        get() = tsumego.playerStone

    val lastMove: MoveNode?
        get() = when {
            !isReview -> moveStack.lastOrNull()
            reviewIndex == 0 -> null
            else -> moveStack.getOrNull((reviewIndex - 1))
        }

    val isOpponentTurn: Boolean
        get() = !isGhostMode && lastMove?.move?.stone == playerStone

    val outcome: SgfNodeOutcome
        get() = when {
            !isGhostMode -> lastMove?.outcome ?: SgfNodeOutcome.NONE
            !isGhostSubmitted -> SgfNodeOutcome.NONE
            moveStack.any { it.outcome == SgfNodeOutcome.SUCCESS } -> SgfNodeOutcome.SUCCESS
            else -> SgfNodeOutcome.FAILURE
        }

    val isReview: Boolean
        get() = reviewRoot != null

    val reviewNextMove: List<MoveNode>? = if (isReview) {
        lastMove?.children ?: reviewRoot?.children
    } else null

    val nextGoodMove: List<MoveNode>
        get() = reviewNextMove?.filter { it.outcome == SgfNodeOutcome.SUCCESS }.orEmpty()
}
