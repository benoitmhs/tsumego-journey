package com.mrsanglier.tsumegohero.coreui.componants.topbanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.coreui.extension.surface
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val DisplayDuration = 3500.milliseconds
private val ExitAnimationDuration = 500.milliseconds

@Composable
fun TopBannerView(
    viewModel: TopBannerViewModel,
    modifier: Modifier = Modifier,
) {
    val bannerState by viewModel.shownBanner.collectAsStateWithLifecycle()
    var visible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(bannerState) {
        if (bannerState == null) return@LaunchedEffect
        visible = true
        delay(DisplayDuration)
        visible = false
        delay(ExitAnimationDuration)
        viewModel.consumeBanner()
    }

    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = visible && bannerState != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            bannerState?.let { state ->
                TopBannerCard(
                    state = state,
                    onClick = {
                        coroutineScope.launch {
                            visible = false
                            delay(ExitAnimationDuration)
                            viewModel.consumeBanner()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun TopBannerCard(
    state: THTopBannerState,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .surface(
                shape = THTheme.shape.topBanner,
                background = THTheme.colors.surface2,
            )
            .thClickable(onClick = onClick)
            .statusBarsPadding()
            .zIndex(1f),
    ) {
        state.Content()
    }
}
