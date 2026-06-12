package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

private val Size: Dp = 100.dp
private val IndicatorStroke = Size / 10
private val IndicatorGap: Dp = 4.dp

@Composable
fun RankProgressBar(
    label: TextSpec,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(progress)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(Size),
            color = THTheme.colors.contentTint,
            strokeWidth = IndicatorStroke,
            trackColor = THTheme.colors.surface2,
            strokeCap = StrokeCap.Round,
            gapSize = IndicatorGap,
            progress = { animatedProgress },
        )

        THText(
            text = label,
            style = THTheme.typography.label200,
        )
    }
}

data class RankProgressBarData(
    val label: TextSpec,
    val progress: Float,
) {
    @Composable
    fun Composable(modifier: Modifier = Modifier) {
        RankProgressBar(
            label = label,
            progress = progress,
            modifier = modifier,
        )
    }
}
