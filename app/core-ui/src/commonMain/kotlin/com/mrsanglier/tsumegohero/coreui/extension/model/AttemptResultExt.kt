package com.mrsanglier.tsumegohero.coreui.extension.model

import androidx.compose.ui.graphics.Color
import com.mrsanglier.tsumegohero.coreui.extension.composed
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.utils.ComposeProvider
import com.mrsanglier.tsumegohero.data.model.game.Attempt

fun List<Attempt.Result?>.mapToColor(): List<ComposeProvider<Color>?> =
    map { it?.getColor() }

fun Attempt.Result.getColor(): ComposeProvider<Color> = THTheme.composed {
    when (this@getColor) {
        Attempt.Result.Success -> colors.detailGreen
        Attempt.Result.Failure -> colors.contentCritical
        Attempt.Result.Skip -> colors.contentSecondary
    }
}