package com.mrsanglier.tsumegohero.coreui.componants.scrollbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.theme.dimens.THCornerRadius
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

/**
 * Scroll indicator for a lazy grid, to place over it: a small thumb on the right edge shows the scroll position.
 * Only the right edge handles touches, the rest of the area lets them through to the grid.
 * Dragging the thumb (or touching the track) scrolls fast to the matching item, showing the number of the first
 * visible item next to it. Shown while scrolling, it fades out slowly after a few seconds of inactivity,
 * and is hidden when everything fits on screen.
 */
@Composable
fun THLazyGridScrollbar(
    state: LazyGridState,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    var isDragging by remember { mutableStateOf(false) }
    // Position of the finger on the track while dragging: the thumb follows it, not the list
    var dragFraction by remember { mutableStateOf(0f) }

    // Scroll position in pixels, from the row size: moves continuously, unlike the first visible index
    val metrics by remember(state) { derivedStateOf { state.gridMetrics() } }
    val listFraction by remember(state) {
        derivedStateOf {
            val m = metrics ?: return@derivedStateOf 0f
            val scrolledPx = (state.firstVisibleItemIndex / m.columns) * m.rowHeightPx + state.firstVisibleItemScrollOffset
            (scrolledPx / m.maxScrollPx).coerceIn(0f, 1f)
        }
    }
    val scrollFraction = if (isDragging) dragFraction else listFraction
    val thumbWidth by animateDpAsState(if (isDragging) ThumbDraggedWidth else ThumbWidth)

    // Appears with any scroll, disappears after a while without scroll nor drag
    var isVisible by remember { mutableStateOf(false) }
    val isActive = state.isScrollInProgress || isDragging
    LaunchedEffect(isActive) {
        if (isActive) {
            isVisible = true
        } else {
            delay(HideDelay)
            isVisible = false
        }
    }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = if (isVisible) FadeInDurationMs else FadeOutDurationMs),
    )

    val currentMetrics = metrics ?: return

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val trackHeightPx = with(density) { maxHeight.toPx() }
        val thumbHeightPx = with(density) { ThumbHeight.toPx() }
        val thumbOffsetPx = (scrollFraction * (trackHeightPx - thumbHeightPx)).roundToInt()

        fun scrollTo(y: Float) {
            val fraction = ((y - thumbHeightPx / 2) / (trackHeightPx - thumbHeightPx)).coerceIn(0f, 1f)
            dragFraction = fraction
            // Row and offset inside it, so that the list moves pixel by pixel with the finger
            val targetPx = fraction * currentMetrics.maxScrollPx
            val row = (targetPx / currentMetrics.rowHeightPx).toInt()
            val offsetInRow = (targetPx - row * currentMetrics.rowHeightPx).roundToInt()
            val index = (row * currentMetrics.columns).coerceAtMost(state.layoutInfo.totalItemsCount - 1)
            coroutineScope.launch { state.scrollToItem(index, offsetInRow) }
        }

        // Touch area along the right edge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
                .width(TouchWidth)
                // A hidden scrollbar cannot be grabbed: the edge must not jump the list unexpectedly
                .pointerInput(state, isVisible) {
                    if (!isVisible) return@pointerInput
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            scrollTo(offset.y)
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onVerticalDrag = { change, _ ->
                            change.consume()
                            scrollTo(change.position.y)
                        },
                    )
                },
        )

        // Thumb
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(0, thumbOffsetPx) }
                .padding(end = THTheme.spacing.tiny)
                .size(width = thumbWidth, height = ThumbHeight)
                .graphicsLayer { this.alpha = alpha }
                .background(color = THTheme.colors.surfaceAccent, shape = THTheme.shape.circle),
        )

        // Number of the first visible item while dragging, left of the touch area with its bottom just above
        // the thumb top, so that the finger on the thumb does not hide it
        AnimatedVisibility(
            visible = isDragging,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(0, thumbOffsetPx) }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, -placeable.height - BubbleGap.roundToPx())
                    }
                }
                .padding(end = TouchWidth),
        ) {
            THText(
                text = "${state.firstVisibleItemIndex + 1}".toTextSpec(),
                style = THTheme.typography.label100Medium,
                color = THTheme.colors.contentOnSurfaceTint,
                maxLines = 1,
                modifier = Modifier
                    .background(color = THTheme.colors.surfaceAccent, shape = BubbleShape)
                    .padding(horizontal = THTheme.spacing.medium, vertical = THTheme.spacing.xsmall),
            )
        }
    }
}

// Matches the usual 16dp screen margin, so the touch area does not overlap the items
private val TouchWidth: Dp = 16.dp
private val ThumbWidth: Dp = 4.dp
private val ThumbDraggedWidth: Dp = 8.dp
private val ThumbHeight: Dp = 40.dp
private val BubbleGap: Dp = 8.dp

// Square corner pointing to the thumb
private val BubbleShape = RoundedCornerShape(
    topStart = THCornerRadius.medium,
    topEnd = THCornerRadius.medium,
    bottomEnd = 0.dp,
    bottomStart = THCornerRadius.medium,
)
private val HideDelay = 3.seconds
private const val FadeInDurationMs = 150
private const val FadeOutDurationMs = 800

/** Grid geometry, estimated from the visible rows (their height may vary, e.g. with the title length). */
private class GridMetrics(
    val columns: Int,
    val rowHeightPx: Float,
    val maxScrollPx: Float,
)

/** @return null when the grid is not laid out yet or does not scroll */
private fun LazyGridState.gridMetrics(): GridMetrics? {
    val info = layoutInfo
    if (info.visibleItemsInfo.isEmpty()) return null
    val columns = (info.visibleItemsInfo.maxOf { it.column } + 1).coerceAtLeast(1)
    // A row is as high as its highest item; the average smooths the differences between rows
    val rowHeights = info.visibleItemsInfo.groupBy { it.row }.values.map { items -> items.maxOf { it.size.height } }
    val rowHeightPx = rowHeights.average().toFloat() + info.mainAxisItemSpacing
    val rowCount = (info.totalItemsCount + columns - 1) / columns
    val contentPx = rowCount * rowHeightPx - info.mainAxisItemSpacing + info.beforeContentPadding + info.afterContentPadding
    val maxScrollPx = contentPx - info.viewportSize.height
    return if (rowHeightPx > 0f && maxScrollPx > 0f) GridMetrics(columns, rowHeightPx, maxScrollPx) else null
}
