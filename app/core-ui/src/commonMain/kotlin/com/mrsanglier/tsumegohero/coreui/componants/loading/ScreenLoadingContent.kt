package com.mrsanglier.tsumegohero.coreui.componants.loading

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Loading shown in place of a screen content (the top bar stays visible).
 * It fades in after a short delay, so that a fast loading does not flash.
 */
@Composable
fun ScreenLoadingContent(modifier: Modifier = Modifier) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = fadeIn(tween(durationMillis = FadeInDurationMs, delayMillis = ShowDelayMs)),
            modifier = Modifier.align(Alignment.Center),
        ) {
            TripleOrbitLoadingAnimation()
        }
    }
}

private const val ShowDelayMs = 300
private const val FadeInDurationMs = 200
