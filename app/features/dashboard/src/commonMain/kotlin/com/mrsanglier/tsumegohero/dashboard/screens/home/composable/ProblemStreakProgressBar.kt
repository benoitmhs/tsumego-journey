package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastCoerceAtMost
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_stones
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.THIcon
import com.mrsanglier.tsumegohero.coreui.componants.progressbar.THStepProgressBar
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

@Composable
internal fun ProblemStreakProgressBar(
    progress: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        ((progress / total.toFloat()) + 0.02f).fastCoerceAtMost(1f)
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.small),
    ) {
        THStepProgressBar(
            progress = progress,
            total = total,
            modifier = Modifier.weight(1f),
        )

        THIcon(
            icon = THDrawable.ic_stones.toIconSpec(),
            tint = THTheme.colors.content,
            iconSize = IconSize.Regular,
        )
    }
}

internal data class ProblemStreakProgressBarData(
    val streak: Int,
    val total: Int,
) {
    @Composable
    fun Composable(modifier: Modifier = Modifier) {
        ProblemStreakProgressBar(
            progress = streak,
            total = total,
            modifier = modifier,
        )
    }
}
