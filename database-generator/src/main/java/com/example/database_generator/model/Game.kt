package com.example.database_generator.model


data class Game(
    val sgf: RawTsumego,
    val board: Board,
    val moveStack: List<MoveNode> = emptyList(),
    val tsumego: Tsumego,
    val cropBoard: CropBoard,
    val reviewRoot: RootNode? = null,
    val reviewIndex: Int = 0,
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
        get() = lastMove?.move?.stone == playerStone

    val outcome: SgfNodeOutcome
        get() = lastMove?.outcome ?: SgfNodeOutcome.NONE

    val isReview: Boolean
        get() = reviewRoot != null

    val reviewNextMove: List<MoveNode>? = if (isReview) {
        lastMove?.children ?: reviewRoot?.children
    } else null

    val nextGoodMove: List<MoveNode>
        get() = reviewNextMove?.filter { it.outcome == SgfNodeOutcome.SUCCESS }.orEmpty()
}
