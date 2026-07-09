package com.mrsanglier.tsumegohero.coreui.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

fun verticalSlideContentTransform(durationMs: Int): ContentTransform =
    slideInVertically(
        animationSpec = tween(durationMs),
        initialOffsetY = { -it },
    ) togetherWith
        slideOutVertically(
            animationSpec = tween(durationMs),
            targetOffsetY = { it },
        )
