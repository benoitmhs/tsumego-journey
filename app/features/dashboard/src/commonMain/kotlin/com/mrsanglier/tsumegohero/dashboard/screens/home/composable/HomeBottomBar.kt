package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.extension.foHazeChild
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import dev.chrisbanes.haze.HazeState

@Composable
internal fun HomeBottomBar(
    streakProgressBar: ProblemStreakProgressBarData,
    hazeState: HazeState,
    elevation: Dp,
    primaryButton: THButtonState?,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(if (elevation == 0.dp) THTheme.colors.background else THTheme.colors.surfaceBlur)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .foHazeChild(
                state = hazeState,
                backgroundColor = backgroundColor,
            )
            .navigationBarsPadding()
            .padding(THTheme.spacing.large)
            .zIndex(1f),
        verticalArrangement = Arrangement.spacedBy(THTheme.spacing.small),
    ) {
        streakProgressBar.Composable()
        primaryButton?.Content(modifier = Modifier.fillMaxWidth())
    }
}
