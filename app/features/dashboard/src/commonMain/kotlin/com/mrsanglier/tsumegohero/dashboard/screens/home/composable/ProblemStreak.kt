package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.app.coreui.resources.stone_black
import com.mrsanglier.tsumegohero.app.coreui.resources.stone_white
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.theme.shape.THShape
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val StoneMaxSize: Dp = 24.dp
private val PlaceHolderSize: Dp = 12.dp

@Composable
fun ProblemStreak(
    streak: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 1..total) {
            when {
                i > streak -> PlaceHolder(modifier = Modifier.weight(1f))
                i % 2 == 0 -> Stone(
                    drawable = THDrawable.stone_black,
                    modifier = Modifier.weight(1f),
                )

                else -> Stone(
                    drawable = THDrawable.stone_white,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun Stone(
    drawable: DrawableResource,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = null,
            modifier = Modifier
                .sizeIn(
                    maxWidth = StoneMaxSize,
                    maxHeight = StoneMaxSize,
                )
                .fillMaxSize()
                .aspectRatio(1f)
                .background(
                    color = THTheme.colors.background,
                    shape = THShape.circle,
                )
        )
    }
}

@Composable
private fun PlaceHolder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(PlaceHolderSize)
                .background(
                    color = THTheme.colors.surfaceDisable,
                    shape = THShape.circle,
                )
        )
    }
}

data class ProblemStreakData(
    val streak: Int,
    val total: Int,
) {
    @Composable
    fun Composable(modifier: Modifier = Modifier) {
        ProblemStreak(
            streak = streak,
            total = total,
            modifier = modifier,
        )
    }
}
