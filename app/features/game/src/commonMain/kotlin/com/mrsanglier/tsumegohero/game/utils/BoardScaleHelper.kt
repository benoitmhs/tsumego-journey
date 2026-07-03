package com.mrsanglier.tsumegohero.game.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.mrsanglier.tsumegohero.game.game.composable.BORDER_SPACING_COEF
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Corner
import com.mrsanglier.tsumegohero.game.model.CropBoard

internal fun CropBoard?.getScaleFactor(boardSize: BoardSize): Float {
    if (this == null) return 1f
    val originSize = boardSize.size

    val frameMaxLength = maxOf(boardSection.xMax - boardSection.xMin, boardSection.yMax - boardSection.yMin)
    val scaledSize = (frameMaxLength + 2).coerceAtMost(originSize)

    if (scaledSize == originSize) return 1f

    return (originSize - 1 + (2 * BORDER_SPACING_COEF)) / (scaledSize + BORDER_SPACING_COEF + 0.5f)
}

/** @return Cell touched or null if outside the board */
internal fun convertOffsetToCell(
    offset: Offset,
    size: Size,
    scaleFactor: Float,
    boardSize: BoardSize,
    cropBoard: CropBoard?,
): Cell? {
    val scaledOffset = unscaleOffset(
        offset = offset,
        scale = scaleFactor,
        pivot = cropBoard?.corner.getScalingPivot(size),
    )

    val cellSize = size.width / (BORDER_SPACING_COEF + boardSize.size)

    // Remove border offset
    val borderDiff = ((cellSize * BORDER_SPACING_COEF) - (cellSize / 2)).coerceAtLeast(0f)
    val rawOffset = scaledOffset - Offset(borderDiff, borderDiff)

    // Convert into Cell
    val (x, y) = rawOffset.div(cellSize).let { (xf, yf) -> xf.toInt() to yf.toInt() }

    return Cell(x, y).takeIf {
        x in 0..<boardSize.size && y in 0..<boardSize.size
    }
}

internal fun Corner?.getScalingPivot(size: Size): Offset {
    return when (this) {
        Corner.TopLeft, null -> Offset.Zero
        Corner.TopRight -> Offset(size.width, 0f)
        Corner.BottomLeft -> Offset(0f, size.height)
        Corner.BottomRight -> Offset(size.width, size.height)
    }
}

private fun unscaleOffset(
    offset: Offset,
    scale: Float,
    pivot: Offset
): Offset {
    return (offset - pivot) / scale + pivot
}