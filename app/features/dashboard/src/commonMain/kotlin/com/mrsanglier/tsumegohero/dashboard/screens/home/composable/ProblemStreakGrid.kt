package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.theme.shape.THShape
import com.mrsanglier.tsumegohero.game.model.Stone

private val StoneMaxSize: Dp = 24.dp
private val PlaceHolderSize: Dp = 12.dp

@Composable
fun ProblemStreakGrid(
    streak: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        GridBackground(total = total)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (i in 1..total) {
                when {
                    i > streak -> PlaceHolder(modifier = Modifier.weight(1f))
                    i % 2 == 0 -> StoneIcon(
                        stone = Stone.BLACK,
                        modifier = Modifier
                            .weight(1f)
                            .sizeIn(
                                maxWidth = StoneMaxSize,
                                maxHeight = StoneMaxSize,
                            )
                    )

                    else -> StoneIcon(
                        stone = Stone.WHITE,
                        modifier = Modifier
                            .weight(1f)
                            .sizeIn(
                                maxWidth = StoneMaxSize,
                                maxHeight = StoneMaxSize,
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun GridBackground(
    total: Int,
    modifier: Modifier = Modifier,
) {
    val strokeColor = THTheme.colors.strokeSecondary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(total.toFloat() / 3)
    ) {
        val cellSize = size.width / total.toFloat()
        val offset = cellSize / 2f

        // Horizontal lines
        for (i in 0 until 3) {
            drawLine(
                color = strokeColor,
                start = Offset(x = 0f, y = offset + i * cellSize),
                end = Offset(x = size.width, y = offset + i * cellSize),
            )
        }

        // Vertical lines
        for (i in 0 until total) {
            drawLine(
                color = strokeColor,
                start = Offset(x = offset + i * cellSize, y = 0f),
                end = Offset(x = offset + i * cellSize, y = size.height),
            )
        }
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
        ProblemStreakGrid(
            streak = streak,
            total = total,
            modifier = modifier,
        )
    }
}
