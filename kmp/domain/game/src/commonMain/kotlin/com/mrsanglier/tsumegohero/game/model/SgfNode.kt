package com.mrsanglier.tsumegohero.game.model

sealed interface SgfNode {
    val children: MutableList<MoveNode>
}

data class MoveNode(
    override val children: MutableList<MoveNode>,
    val move: Move,
    var outcome: SgfNodeOutcome,
) : SgfNode {
    fun clone(): MoveNode =
        this.copy(
            children = children.map { it.clone() }.toMutableList(),
        )

    /** True if this node or one of its descendants is a winning node. */
    fun leadsToSuccess(): Boolean =
        outcome == SgfNodeOutcome.SUCCESS || children.any { it.leadsToSuccess() }
}

/**
 * True when every move of the sequence follows the solution tree without reaching an outcome yet,
 * and the solution can still be completed from the last move (a winning node exists below it).
 */
fun List<MoveNode>.isIncompleteSequence(): Boolean {
    val last = lastOrNull() ?: return false
    return none { it.outcome != SgfNodeOutcome.NONE } && last.leadsToSuccess()
}

data class RootNode(
    override val children: MutableList<MoveNode> = mutableListOf(),
) : SgfNode