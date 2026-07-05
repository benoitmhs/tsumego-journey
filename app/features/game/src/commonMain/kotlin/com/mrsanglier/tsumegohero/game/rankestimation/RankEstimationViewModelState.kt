package com.mrsanglier.tsumegohero.game.rankestimation

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.game.section.GameActionState

@Immutable
internal data class RankEstimationViewModelState(
    val progress: Float,
    val boardState: BoardUiState,
    val gameActionState: GameActionState,
)
