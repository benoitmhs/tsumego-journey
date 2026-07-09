package com.mrsanglier.tsumegohero.coreui.componants.cellobjective

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.componants.icon.Content
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.spacer.THHorizontalSpacer
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.utils.ComposeProvider
import kotlinx.coroutines.delay

private val ProgressHeight: Dp = 4.dp
private const val AnimationDurationMs: Int = 500
private const val AnimationDelayMs: Int = 500

@Composable
fun CellObjective(
    title: TextSpec,
    icon: IconSpec,
    trailingIcon: IconSpec?,
    attempts: ComposeProvider<List<Color>>,
    doneText: TextSpec,
    totalText: TextSpec,
    animateAttemptIndex: Int? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon.Content(iconSize = IconSize.Regular)
        THTheme.spacing.small.THHorizontalSpacer()


        MainContent(
            title = title,
            doneText = doneText,
            totalText = totalText,
            attempts = attempts,
            animateAttemptIndex = animateAttemptIndex,
            modifier = Modifier.weight(1f),
        )
        THTheme.spacing.large.THHorizontalSpacer()

        trailingIcon?.Content(iconSize = IconSize.Small)
    }

}

@Composable
private fun RowScope.MainContent(
    title: TextSpec,
    doneText: TextSpec,
    totalText: TextSpec,
    attempts: ComposeProvider<List<Color>>,
    animateAttemptIndex: Int?,
    modifier: Modifier = Modifier,
) {
    val progressAnimatable = remember { Animatable(0f) }

    LaunchedEffect(animateAttemptIndex) {
        progressAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = AnimationDurationMs,
                delayMillis = AnimationDelayMs,
            ),
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(THTheme.spacing.tiny),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            THText(
                text = title,
                style = THTheme.typography.title100,
                modifier = Modifier.weight(1f),
            )
            THText(
                text = doneText,
                style = THTheme.typography.content100Semibold,
            )
            THText(
                text = totalText,
                style = THTheme.typography.content50Semibold,
                color = THTheme.colors.contentSecondary,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(THTheme.shape.circle),
            horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.xtiny),
        ) {
            attempts().forEachIndexed { index, color ->
                LinearProgressIndicator(
                    modifier = Modifier
                        .height(ProgressHeight)
                        .weight(1f),
                    color = color,
                    backgroundColor = THTheme.colors.contentDisable,
                    progress = if (index == animateAttemptIndex) progressAnimatable.value else 1f,
                )
            }
        }
    }
}
