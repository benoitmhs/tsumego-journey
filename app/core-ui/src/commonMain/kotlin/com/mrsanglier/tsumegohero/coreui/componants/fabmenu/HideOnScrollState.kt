package com.mrsanglier.tsumegohero.coreui.componants.fabmenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Visibility of an element (typically a FAB) that hides after a long enough scroll down and comes back
 * on a scroll up. Attach [nestedScrollConnection] with `Modifier.nestedScroll` to a parent of the scrolling list.
 */
@Stable
class HideOnScrollState internal constructor(
    private val hideThresholdPx: Float,
    private val showThresholdPx: Float,
) {
    var isVisible: Boolean by mutableStateOf(true)
        private set

    // Distance scrolled in the current direction: positive down (content moving up), negative up
    private var accumulated = 0f

    val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            val delta = -consumed.y
            if (delta == 0f) return Offset.Zero
            // Restart counting when the direction changes
            if (delta > 0f != accumulated > 0f) accumulated = 0f
            accumulated += delta
            when {
                accumulated > hideThresholdPx -> isVisible = false
                accumulated < -showThresholdPx -> isVisible = true
            }
            return Offset.Zero
        }
    }
}

@Composable
fun rememberHideOnScrollState(
    hideThreshold: Dp = DefaultHideThreshold,
    showThreshold: Dp = DefaultShowThreshold,
): HideOnScrollState {
    val density = LocalDensity.current
    return remember(density, hideThreshold, showThreshold) {
        with(density) {
            HideOnScrollState(hideThresholdPx = hideThreshold.toPx(), showThresholdPx = showThreshold.toPx())
        }
    }
}

private val DefaultHideThreshold: Dp = 120.dp
private val DefaultShowThreshold: Dp = 16.dp
