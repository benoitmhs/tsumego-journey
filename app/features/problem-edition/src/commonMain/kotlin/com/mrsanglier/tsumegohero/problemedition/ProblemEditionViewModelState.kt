package com.mrsanglier.tsumegohero.problemedition

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Stone

internal enum class EditionStep {
    Board, Solutions, Info;
}

/** Stone placed (or erased) by a tap while entering the initial position. */
internal enum class EditionTool {
    Black, White, Erase;
}

/** State of every step at once, so that the steps can be displayed side by side (pager). */
@Immutable
internal data class ProblemEditionViewModelState(
    val screenTitle: TextSpec? = null,
    val step: EditionStep = EditionStep.Board,
    val boardSize: BoardSize = BoardSize.X19,
    val mainButton: THButtonState? = null,
    // Board step
    val initialBoardUiState: BoardUiState = BoardUiState(),
    val tool: EditionTool = EditionTool.Black,
    val firstPlayer: Stone = Stone.BLACK,
    // Solutions step
    val solutionsBoardUiState: BoardUiState = BoardUiState(),
    val moveNumbers: Map<Cell, Int> = emptyMap(),
    val moveTree: MoveTreeUiState = MoveTreeUiState(),
    val canGoPrevious: Boolean = false,
    val canGoNext: Boolean = false,
    /** Only a leaf (a move without following moves) can be marked as correct or wrong. */
    val canMark: Boolean = false,
    val currentOutcome: Boolean? = null,
    val canDelete: Boolean = false,
    val goodMoves: Set<Cell> = emptySet(),
    val badMoves: Set<Cell> = emptySet(),
    // Info step
    val title: String = "",
    val notes: String = "",
    val difficulty: TextSpec? = null,
)
