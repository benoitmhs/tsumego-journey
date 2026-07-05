package com.mrsanglier.tsumegohero.game.model

import com.mrsanglier.tsumegohero.data.model.game.RawTsumego

data class Game(
    val sgf: RawTsumego,
    val board: Board,
    val boardConfig: BoardConfig,
    val moveStack: List<MoveNode> = emptyList(),
    val tsumego: Tsumego,
    val cropBoard: CropBoard,
    val reviewRoot: RootNode? = null,
    val reviewIndex: Int = 0,
    val option: GameOption = GameOption(autoPlay = true, ghost = false),
    val isSequenceSubmitted: Boolean = false,
) {
    val playerStone: Stone
        get() = tsumego.playerStone

    val lastMove: MoveNode?
        get() = when {
            !isReview -> moveStack.lastOrNull()
            reviewIndex == 0 -> null
            else -> moveStack.getOrNull((reviewIndex - 1))
        }

    val isOpponentTurn: Boolean
        get() = option.autoPlay && lastMove?.move?.stone == playerStone

    val outcome: SgfNodeOutcome
        get() = when {
            option.autoPlay -> lastMove?.outcome ?: SgfNodeOutcome.NONE
            !isSequenceSubmitted -> SgfNodeOutcome.NONE
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
