package com.mrsanglier.tsumegohero.game.usertsumego

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.game.section.GameActionState

@Immutable
internal data class UserTsumegoGameViewModelState(
    val title: TextSpec,
    val boardState: BoardUiState,
    val gameActionState: GameActionState,
)
