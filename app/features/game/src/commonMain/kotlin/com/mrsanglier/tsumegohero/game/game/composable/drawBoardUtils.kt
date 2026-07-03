package com.mrsanglier.tsumegohero.game.game.composable

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import com.mrsanglier.tsumegohero.coreui.theme.color.DarkGreen
import com.mrsanglier.tsumegohero.game.game.uimodel.BoardStyle
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.Stone

internal fun DrawScope.drawBoard(
    boardSize: BoardSize,
    style: BoardStyle,
    blackStones: Set<Cell>,
    whiteStones: Set<Cell>,
    blackStoneImageBitmap: ImageBitmap,
    whiteStoneImageBitmap: ImageBitmap,
    lastMove: Move?,
    goodMoves: Set<Cell>?,
    badMoves: Set<Cell>?,
    lastMoveAlpha: Float = 1f,
) {
    val cellSpacing = minOf(size.width, size.height) / (boardSize.size - 1 + 2 * BORDER_SPACING_COEF)
    val boarderSpacing = cellSpacing * BORDER_SPACING_COEF

    val startOffset = Offset(x = boarderSpacing, y = boarderSpacing)
    val endOffset = boarderSpacing

    // Draw column lines
    for (x in 0..<boardSize.size) {
        val startOffset = startOffset + Offset(x = x * cellSpacing, y = 0f)
        val endOffset = startOffset.copy(y = this.size.height - endOffset)
        drawLine(
            color = style.gridColor,
            start = startOffset,
            end = endOffset,
            strokeWidth = LINE_STROKE,
        )
    }

    // Draw row lines
    for (y in 0..<boardSize.size) {
        val startOffset = startOffset + Offset(x = 0f, y = y * cellSpacing)
        val endOffset = startOffset.copy(x = this.size.width - endOffset)
        drawLine(
            color = style.gridColor,
            start = startOffset,
            end = endOffset,
            strokeWidth = LINE_STROKE,
        )
    }

    // Draw hoshi
    for (x in 0..<boardSize.size) {
        for (y in 0..<boardSize.size) {
            if ((x - 3) % 6 == 0 && (y - 3) % 6 == 0) {
                drawCircle(
                    center = Offset(x = x * cellSpacing, y = y * cellSpacing) + startOffset,
                    radius = cellSpacing * 0.1f,
                    color = style.gridColor,
                )
            }
        }
    }

    // Draw Stones
    val stoneSize = cellSpacing * STONE_SIZE_RATIO
    for (stone in blackStones) {
        drawStone(
            imageBitmap = blackStoneImageBitmap,
            cell = stone,
            cellSpacing = cellSpacing,
            startOffset = startOffset,
        )
    }
    for (stone in whiteStones) {
        drawStone(
            imageBitmap = whiteStoneImageBitmap,
            cell = stone,
            cellSpacing = cellSpacing,
            startOffset = startOffset,
        )
    }

    // Draw review marker
    goodMoves?.forEach { (x, y) ->
        drawCircle(
            center = Offset(x = x * cellSpacing, y = y * cellSpacing) + startOffset,
            radius = cellSpacing / 2 * REVIEW_MARKER_RATIO,
            color = DarkGreen.copy(alpha = REVIEW_MARKER_ALPHA),
        )
    }
    badMoves?.forEach { (x, y) ->
        drawCircle(
            center = Offset(x = x * cellSpacing, y = y * cellSpacing) + startOffset,
            radius = cellSpacing / 2 * REVIEW_MARKER_RATIO,
            color = Color.Red.copy(alpha = REVIEW_MARKER_ALPHA),
        )
    }

    // Draw last stone marker
    lastMove?.let { move ->
        drawStone(
            imageBitmap = if (move.stone == Stone.BLACK) blackStoneImageBitmap else whiteStoneImageBitmap,
            cell = move.gameMove,
            cellSpacing = cellSpacing,
            startOffset = startOffset,
            alpha = lastMoveAlpha,
        )
        val circleColor = when (move.stone) {
            Stone.BLACK -> Color.White
            Stone.WHITE -> Color.Black
        }
        drawCircle(
            color = circleColor,
            radius = stoneSize / 2 * LAST_STONE_RATIO,
            style = Stroke(width = LINE_STROKE),
            center = Offset(move.gameMove.x * cellSpacing, move.gameMove.y * cellSpacing) + startOffset,
            alpha = lastMoveAlpha,
        )
    }
}

private fun DrawScope.drawStone(
    imageBitmap: ImageBitmap,
    cell: Cell,
    cellSpacing: Float,
    startOffset: Offset,
    alpha: Float = 1f,
) {
    val stoneSize = cellSpacing * STONE_SIZE_RATIO
    // Shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.2f * alpha),
        radius = stoneSize / 2 * STONE_SHADOW_RATIO,
        center = Offset(
            x = cell.x * cellSpacing,
            y = cell.y * cellSpacing,
        ) + startOffset + Offset(1f, 1f),
    )

    // Stone
    drawImage(
        image = imageBitmap,
        dstSize = IntSize(
            width = stoneSize.toInt(),
            height = stoneSize.toInt(),
        ),
        dstOffset = (Offset(
            x = (cell.x * cellSpacing) - stoneSize / 2,
            y = (cell.y * cellSpacing) - stoneSize / 2,
        ) + startOffset).round(),
        alpha = alpha,
    )
}

/** Size ratio of spacing border compare to cell spacing **/
internal const val BORDER_SPACING_COEF: Float = 1f

private const val LINE_STROKE: Float = 2f
private const val STONE_SIZE_RATIO: Float = 0.95f
private const val LAST_STONE_RATIO: Float = 0.6f
private const val STONE_SHADOW_RATIO: Float = 1.02f
private const val REVIEW_MARKER_RATIO: Float = 0.3f
private const val REVIEW_MARKER_ALPHA: Float = 0.6f