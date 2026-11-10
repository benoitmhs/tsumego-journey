package com.mrsanglier.tsumegohero.collection.edition

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

@Immutable
internal data class TsumegoEditionViewModelState(
    val step: EditionStep = EditionStep.Board,
    val boardSize: BoardSize = BoardSize.X19,
    val boardUiState: BoardUiState = BoardUiState(),
    val mainButton: THButtonState? = null,
    // Board step
    val tool: EditionTool = EditionTool.Black,
    val firstPlayer: Stone = Stone.BLACK,
    // Solutions step
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
) {
    /** Completed steps: the progress bar shows the current step as just started. */
    val stepNumber: Int
        get() = step.ordinal

    val stepCount: Int
        get() = EditionStep.entries.size
}
