package com.mrsanglier.tsumegohero.game.training.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.app.coreui.resources.fire_orange
import com.mrsanglier.tsumegohero.app.coreui.resources.topBanner_dailyStreak
import com.mrsanglier.tsumegohero.coreui.animation.verticalSlideContentTransform
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.topbanner.THTopBannerState
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val FireImageSize: Dp = 24.dp
private const val AnimationDurationMs: Int = 500
private val AnimationDelay: Duration = 500.milliseconds

@Composable
private fun DailyStreakTopBanner(
    streakCount: Int,
) {
    var streakDislayed by remember { mutableIntStateOf(streakCount - 1) }

    LaunchedEffect(streakCount) {
        delay(AnimationDelay)
        streakDislayed = streakCount
    }

    Row(
        modifier = Modifier.padding(THTheme.spacing.large),
        horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(THDrawable.fire_orange),
            contentDescription = null,
            modifier = Modifier.size(FireImageSize),
        )
        AnimatedContent(
            targetState = streakDislayed,
            label = "Streak count",
            transitionSpec = {
                verticalSlideContentTransform(AnimationDurationMs)
            }
        ) { state ->
            THText(
                text = state.toString().toTextSpec(),
                style = THTheme.typography.label200,
            )
        }
        THText(
            text = "jours de suite".toTextSpec(), // TODO: loco
            style = THTheme.typography.label100,
        )
    }
}

data class DailyStreakTopBannerState(
    val streakCount: Int,
) : THTopBannerState {

    @Composable
    override fun Content() {
        DailyStreakTopBanner(streakCount)
    }
}