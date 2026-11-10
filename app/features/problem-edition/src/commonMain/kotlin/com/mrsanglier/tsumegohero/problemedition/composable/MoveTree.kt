package com.mrsanglier.tsumegohero.problemedition.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.problemedition.MoveTreeNode
import com.mrsanglier.tsumegohero.problemedition.MoveTreeOutcome
import com.mrsanglier.tsumegohero.problemedition.MoveTreeUiState
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.thCard
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.theme.color.DarkGreen
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.Stone

/**
 * Interactive tree of the entered moves: a tap on a node moves the board to that position.
 * Green ring: correct answer, red ring: wrong answer.
 */
@Composable
internal fun MoveTree(
    state: MoveTreeUiState,
    onClickNode: (path: List<Move>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()
    val density = LocalDensity.current
    val edgeColor = THTheme.colors.strokeSecondary

    // Keep the current position visible
    LaunchedEffect(state.currentNode?.column, state.currentNode?.row) {
        val node = state.currentNode ?: return@LaunchedEffect
        with(density) {
            horizontalScroll.animateScrollTo((TreeCellSize * (node.column - 2)).roundToPx().coerceAtLeast(0))
            verticalScroll.animateScrollTo((TreeCellSize * (node.row - 1)).roundToPx().coerceAtLeast(0))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .thCard()
            .heightIn(max = TreeCellSize * MaxVisibleRows + THTheme.spacing.small * 2)
            .verticalScroll(verticalScroll)
            .horizontalScroll(horizontalScroll)
            .padding(THTheme.spacing.small),
    ) {
        Canvas(
            modifier = Modifier.size(
                width = TreeCellSize * state.columnCount,
                height = TreeCellSize * state.rowCount,
            ),
        ) {
            val cell = TreeCellSize.toPx()
            fun center(column: Int, row: Int) = Offset((column + 0.5f) * cell, (row + 0.5f) * cell)
            state.edges.forEach { edge ->
                val from = center(edge.fromColumn, edge.fromRow)
                val to = center(edge.toColumn, edge.toRow)
                // Elbow: down along the parent column, then right to the child
                val corner = Offset(from.x, to.y)
                drawLine(edgeColor, from, corner, strokeWidth = EdgeStroke.toPx())
                drawLine(edgeColor, corner, to, strokeWidth = EdgeStroke.toPx())
            }
        }

        state.nodes.forEach { node ->
            TreeNodeItem(
                node = node,
                onClick = { onClickNode(node.path) },
                modifier = Modifier.offset(
                    x = TreeCellSize * node.column,
                    y = TreeCellSize * node.row,
                ),
            )
        }
    }
}

@Composable
private fun TreeNodeItem(
    node: MoveTreeNode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outcomeColor = when (node.outcome) {
        MoveTreeOutcome.Correct -> DarkGreen
        MoveTreeOutcome.Wrong -> Color.Red
        MoveTreeOutcome.None -> null
    }

    Box(
        modifier = modifier.size(TreeCellSize),
        contentAlignment = Alignment.Center,
    ) {
        // Current position highlight
        Box(
            modifier = Modifier
                .size(NodeSize + HighlightWidth * 2 + 2.dp)
                .border(
                    width = HighlightWidth,
                    color = if (node.isCurrent) THTheme.colors.contentTint else Color.Transparent,
                    shape = THTheme.shape.circle,
                )
                .thClickable(onClick = onClick, role = Role.Button),
            contentAlignment = Alignment.Center,
        ) {
            val stone = node.stone
            Box(
                modifier = Modifier
                    .size(if (stone == null) RootSize else NodeSize)
                    .background(
                        color = when (stone) {
                            Stone.BLACK -> Color.Black
                            Stone.WHITE -> Color.White
                            null -> THTheme.colors.surface3
                        },
                        shape = THTheme.shape.circle,
                    )
                    .border(
                        width = if (outcomeColor != null) OutcomeWidth else 1.dp,
                        color = outcomeColor ?: THTheme.colors.strokeSecondary,
                        shape = THTheme.shape.circle,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (stone != null) {
                    THText(
                        text = node.number.toString().toTextSpec(),
                        style = THTheme.typography.label50,
                        color = if (stone == Stone.BLACK) Color.White else Color.Black,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

private val TreeCellSize: Dp = 36.dp
private val NodeSize: Dp = 24.dp
private val RootSize: Dp = 14.dp
private val HighlightWidth: Dp = 2.dp
private val OutcomeWidth: Dp = 2.5.dp
private val EdgeStroke: Dp = 1.5.dp
private const val MaxVisibleRows: Int = 4
