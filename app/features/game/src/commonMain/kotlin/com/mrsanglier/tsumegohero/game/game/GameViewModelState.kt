package com.mrsanglier.tsumegohero.game.game

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.utils.ComposeProvider
import com.mrsanglier.tsumegohero.game.model.CropBoard
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move

@Immutable
data class GameViewModelState(
    val title: TextSpec? = null,
    val whiteStones: Set<Cell> = emptySet(),
    val blackStones: Set<Cell> = emptySet(),
    val cropBoard: CropBoard? = null,
    val playerStone: TextSpec? = null,
    val result: TextSpec? = null,
    val lastMove: Move? = null,
    val borderColor: ComposeProvider<Color> = { Color.Transparent },
    val nextButton: THButtonState,
    val resetButton: THButtonState,
    val isReview: Boolean = false,
    val reviewButton: THButtonState? = null,
    val reviewPreviousButton: THButtonState? = null,
    val reviewNextButton: THButtonState? = null,
    val reviewResetButton: THButtonState? = null,
    val goodMoves: Set<Cell>? = null,
    val badMoves: Set<Cell>? = null,
    val isGhostMode: Boolean = false,
    val submitButton: THButtonState? = null,
)
