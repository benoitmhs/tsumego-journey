package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val imageSize: Dp = 32.dp

@Composable
fun DailyStreakCell(
    imageRes: DrawableResource,
    text: TextSpec,
    alpha: Float = 1f,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = THTheme.spacing.small)
            .alpha(alpha),
        horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.size(imageSize),
        )

        THText(
            text = text,
            style = THTheme.typography.label200,
            color = THTheme.colors.content,
        )
    }
}

data class DailyStreakCellData(
    val imageRes: DrawableResource,
    val text: TextSpec,
    val alpha: Float,
) {
    @Composable
    fun Composable(modifier: Modifier = Modifier) {
        DailyStreakCell(
            imageRes = imageRes,
            text = text,
            alpha = alpha,
            modifier = modifier,
        )
    }
}