package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_stones
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.THIcon
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

private val DotSize: Dp = 2.dp

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
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            LinearProgressIndicator(
                color = THTheme.colors.contentTint,
                trackColor = THTheme.colors.surface2,
                strokeCap = StrokeCap.Round,
                progress = { animatedProgress },
                gapSize = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(THTheme.spacing.small),
                drawStopIndicator = {},
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = THTheme.spacing.tiny),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                for (i in 0..total) {
                    Box(
                        modifier = Modifier
                            .size(DotSize)
                            .background(
                                color = when {
                                    i == 0 -> THTheme.colors.contentTint
                                    i > progress -> THTheme.colors.contentSecondary
                                    else -> THTheme.colors.background
                                }
                            )
                    )
                }
            }
        }

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
