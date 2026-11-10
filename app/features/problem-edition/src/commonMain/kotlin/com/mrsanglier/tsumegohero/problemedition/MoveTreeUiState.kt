package com.mrsanglier.tsumegohero.problemedition

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.domain.collection.model.SolutionTree
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.Stone

/** Grid layout of the solution tree: one column per move depth, one row per variation. */
@Immutable
internal data class MoveTreeUiState(
    val nodes: List<MoveTreeNode> = emptyList(),
    val edges: List<MoveTreeEdge> = emptyList(),
    val columnCount: Int = 1,
    val rowCount: Int = 1,
) {
    val currentNode: MoveTreeNode?
        get() = nodes.firstOrNull { it.isCurrent }
}

internal enum class MoveTreeOutcome {
    None, Correct, Wrong;
}

/** @property path moves from the initial position to this node (empty for the root) */
@Immutable
internal data class MoveTreeNode(
    val path: List<Move>,
    val column: Int,
    val row: Int,
    val outcome: MoveTreeOutcome,
    val isCurrent: Boolean,
) {
    val stone: Stone?
        get() = path.lastOrNull()?.stone

    val number: Int
        get() = path.size
}

@Immutable
internal data class MoveTreeEdge(
    val fromColumn: Int,
    val fromRow: Int,
    val toColumn: Int,
    val toRow: Int,
)

/** Lays out the solution tree; [currentMoves] is the position shown on the board. */
internal fun buildMoveTree(
    tree: SolutionTree,
    currentMoves: List<Move>,
): MoveTreeUiState {
    val nodes = mutableListOf<MoveTreeNode>()
    val edges = mutableListOf<MoveTreeEdge>()
    var maxRow = 0

    // The first child stays on its parent's row, each other variation opens a new row below everything placed
    fun place(path: List<Move>, column: Int, row: Int) {
        nodes += MoveTreeNode(
            path = path,
            column = column,
            row = row,
            outcome = when (tree.outcome(path)) {
                true -> MoveTreeOutcome.Correct
                false -> MoveTreeOutcome.Wrong
                null -> MoveTreeOutcome.None
            },
            isCurrent = path == currentMoves,
        )
        tree.children(path).forEachIndexed { index, child ->
            val childRow = if (index == 0) row else ++maxRow
            edges += MoveTreeEdge(fromColumn = column, fromRow = row, toColumn = column + 1, toRow = childRow)
            place(child, column + 1, childRow)
        }
    }
    place(emptyList(), column = 0, row = 0)

    return MoveTreeUiState(
        nodes = nodes,
        edges = edges,
        columnCount = nodes.maxOf { it.column } + 1,
        rowCount = maxRow + 1,
    )
}
