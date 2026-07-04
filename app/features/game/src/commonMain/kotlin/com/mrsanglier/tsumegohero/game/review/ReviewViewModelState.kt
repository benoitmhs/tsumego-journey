package com.mrsanglier.tsumegohero.game.review

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.CropBoard
import com.mrsanglier.tsumegohero.game.model.Move

@Immutable
data class ReviewViewModelState(
    val boardState: BoardUiState = BoardUiState(),
    val goodMoves: Set<Cell>? = null,
    val badMoves: Set<Cell>? = null,
    val previousButton: THButtonState? = null,
    val nextButton: THButtonState? = null,
    val resetButton: THButtonState? = null,
)
