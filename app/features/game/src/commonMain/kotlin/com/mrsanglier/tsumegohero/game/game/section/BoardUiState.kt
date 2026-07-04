package com.mrsanglier.tsumegohero.game.game.section

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.utils.ComposeProvider
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.CropBoard
import com.mrsanglier.tsumegohero.game.model.Move

@Immutable
data class BoardUiState(
    val title: TextSpec? = null,
    val whiteStones: Set<Cell> = emptySet(),
    val blackStones: Set<Cell> = emptySet(),
    val cropBoard: CropBoard? = null,
    val lastMove: Move? = null,
    val isGhostMode: Boolean = false,
    val playerStone: TextSpec? = null,
    val result: TextSpec? = null,
    val borderColor: ComposeProvider<Color> = { Color.Companion.Transparent },
)