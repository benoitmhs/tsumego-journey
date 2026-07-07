package com.mrsanglier.tsumegohero.dashboard.screens.home

import androidx.compose.ui.util.fastCoerceAtLeast
import com.mrsanglier.tsumegohero.app.coreui.resources.fire_blue
import com.mrsanglier.tsumegohero.app.coreui.resources.fire_grey
import com.mrsanglier.tsumegohero.app.coreui.resources.fire_orange
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.DailyStreakCellData
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.ProblemStreakProgressBarData
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.RankProgressBarData
import com.mrsanglier.tsumegohero.data.model.user.DailyStreak
import com.mrsanglier.tsumegohero.data.model.objective.ProgressData

internal fun DailyStreak.toCellData(): DailyStreakCellData {
    val imageRes = when (status) {
        DailyStreak.Status.Today,
        DailyStreak.Status.Hot -> THDrawable.fire_orange

        DailyStreak.Status.Cold -> THDrawable.fire_blue
        DailyStreak.Status.Unactive -> THDrawable.fire_grey
    }

    val alpha = if (status == DailyStreak.Status.Today) 1f else 0.5f
    val text = if (streakCount > 0) streakCount.toString() else "-"

    return DailyStreakCellData(
        imageRes = imageRes,
        text = text.toTextSpec(),
        alpha = alpha,
    )
}

internal fun ProgressData.getRankProgressBarData(): RankProgressBarData =
    RankProgressBarData(
        label = this.rank.rawValue.lowercase().toTextSpec(),
        progress = (rankStepCompleted / rankStepRequired.toFloat())
            .fastCoerceAtLeast(0.005f),
    )

internal fun ProgressData.getProblemStreakData(): ProblemStreakProgressBarData =
    ProblemStreakProgressBarData(
        streak = problemStreak,
        total = problemStreakRequired,
    )

internal fun HomeViewModel.getMainAction(
    rankIsNull: Boolean,
    estimationInProgress: Boolean,
) = when {
    !rankIsNull -> THButtonState(
        text = "Solve Tsumego".toTextSpec(), // TODO: loco
        onClick = ::startTsumego,
        style = ButtonStyle.Primary,
    )

    estimationInProgress -> THButtonState(
        text = "Continue the test".toTextSpec(), // TODO: loco
        onClick = ::continueRankEstimation,
        style = ButtonStyle.Primary,
    )

    else -> THButtonState(
        text = "Choose my Rank".toTextSpec(), // TODO: loco
        onClick = ::openRankBottomSheet,
        style = ButtonStyle.Primary,
    )
}

internal object PlaceHolder {
    val DailyStreak: DailyStreakCellData
        get() = DailyStreakCellData(imageRes = THDrawable.fire_grey, text = "-".toTextSpec(), alpha = 0.5f)

    val RankProgressBar: RankProgressBarData
        get() = RankProgressBarData(label = "-".toTextSpec(), progress = 0f)

    val ProblemStreak: ProblemStreakProgressBarData
        get() = ProblemStreakProgressBarData(streak = 0, total = 10)
}
