package com.example.database_generator.model

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
}

data class RootNode(
    override val children: MutableList<MoveNode> = mutableListOf(),
) : SgfNode