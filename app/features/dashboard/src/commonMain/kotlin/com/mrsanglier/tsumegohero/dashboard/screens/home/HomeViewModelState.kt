package com.mrsanglier.tsumegohero.dashboard.screens.home

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.DailyStreakCellData
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.ProblemStreakData
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.RankProgressBarData

@Immutable
data class HomeViewModelState(
    val dailyStreakData: DailyStreakCellData = PlaceHolder.DailyStreak,
    val rankProgressBarData: RankProgressBarData = PlaceHolder.RankProgressBar,
    val problemStreakData: ProblemStreakData = PlaceHolder.ProblemStreak,
    val mainAction: THButtonState? = null,
)
