package com.mrsanglier.tsumegohero.game.training.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.animation.verticalSlideContentTransform
import com.mrsanglier.tsumegohero.coreui.componants.cellobjective.ObjectiveProgressBar
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.model.mapToColor
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.utils.ComposeProvider
import com.mrsanglier.tsumegohero.data.model.game.Attempt

private const val AnimationDurationMs: Int = 500

@Composable
internal fun CurrentDailyObectiveProgress(
    attempts: List<ComposeProvider<Color>?>,
    animateAttemptIndex: Int,
    doneCount: Int,
    totalText: TextSpec,
    modifier: Modifier = Modifier,
) {
    val animatedFilledCount by animateFloatAsState(
        targetValue = (animateAttemptIndex + 1).toFloat(),
        animationSpec = tween(durationMillis = AnimationDurationMs),
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        ObjectiveProgressBar(
            attempts = attempts,
            animateAttemptIndex = animateAttemptIndex,
            progress = (animatedFilledCount - animateAttemptIndex).coerceIn(0f, 1f),
            modifier = Modifier.weight(1f),
        )
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            AnimatedContent(
                targetState = doneCount,
                label = "Done.count",
                transitionSpec = { verticalSlideContentTransform(AnimationDurationMs) },
            ) { state ->
                THText(
                    text = state.toString().toTextSpec(),
                    style = THTheme.typography.content100Semibold,
                    modifier = Modifier.widthIn(min = 16.dp),
                    textAlign = TextAlign.End,
                )
            }
            THText(
                text = totalText,
                style = THTheme.typography.content50Semibold,
                color = THTheme.colors.contentSecondary,
            )
        }
    }
}

@Immutable
internal data class CurrentDailyObjectiveProgressState(
    val attempts: List<Attempt.Result?>,
) {
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        CurrentDailyObectiveProgress(
            attempts = attempts.mapToColor(),
            animateAttemptIndex = attempts.indexOfLast { it != null },
            doneCount = attempts.count { it != null },
            totalText = "/${attempts.count()}".toTextSpec(),
            modifier = modifier,
        )
    }
}
