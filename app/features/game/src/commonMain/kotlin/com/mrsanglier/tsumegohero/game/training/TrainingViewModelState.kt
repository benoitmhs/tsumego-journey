package com.mrsanglier.tsumegohero.game.training

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.game.section.GameActionState

@Immutable
data class TrainingViewModelState(
    val boardState: BoardUiState,
    val gameActionState: GameActionState,
)
