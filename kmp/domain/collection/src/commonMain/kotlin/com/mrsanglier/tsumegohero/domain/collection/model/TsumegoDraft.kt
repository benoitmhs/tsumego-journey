package com.mrsanglier.tsumegohero.domain.collection.model

import com.mrsanglier.tsumegohero.game.model.Board
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.Stone

/** A problem being entered manually, before it is saved in a collection. */
data class TsumegoDraft(
    val boardSize: BoardSize,
    val blackStones: Set<Cell>,
    val whiteStones: Set<Cell>,
    val solutions: List<SolutionSequence> = emptyList(),
) {
    val hasCorrectSolution: Boolean
        get() = solutions.any { it.isCorrect }

    fun initialBoard(): Board {
        // Not inside Board.apply: Board has its own blackStones/whiteStones that would shadow the draft ones
        val board = Board(boardSize)
        blackStones.forEach { board.setupStone(it, Stone.BLACK) }
        whiteStones.forEach { board.setupStone(it, Stone.WHITE) }
        return board
    }
}

/**
 * A sequence of moves played from the initial position.
 * @property isCorrect true if the sequence solves the problem, false if it is a refuted attempt.
 */
data class SolutionSequence(
    val moves: List<Move>,
    val isCorrect: Boolean,
)
