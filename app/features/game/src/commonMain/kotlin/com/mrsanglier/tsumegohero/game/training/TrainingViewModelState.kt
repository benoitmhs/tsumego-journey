package com.mrsanglier.tsumegohero.game.training

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.game.section.GameActionState
import com.mrsanglier.tsumegohero.game.training.composable.CurrentDailyObjectiveProgressState

@Immutable
internal data class TrainingViewModelState(
    val title: TextSpec,
    val currentDailyObectiveProgress: CurrentDailyObjectiveProgressState,
    val boardState: BoardUiState,
    val gameActionState: GameActionState,
)
