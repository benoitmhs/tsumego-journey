package com.mrsanglier.tsumegohero.domain.collection.model

import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.MoveNode
import com.mrsanglier.tsumegohero.game.model.RootNode
import com.mrsanglier.tsumegohero.game.model.SgfNodeOutcome

/**
 * Solution tree being entered. A node is identified by its path: the moves played from the initial position
 * (the empty path is the root). Only leaves can be marked as correct or wrong.
 *
 * @property paths every node except the root, in insertion order (a parent is always before its children)
 * @property outcomes true: correct answer, false: wrong answer, absent: not marked
 */
data class SolutionTree(
    val paths: List<List<Move>> = emptyList(),
    val outcomes: Map<List<Move>, Boolean> = emptyMap(),
) {
    fun contains(path: List<Move>): Boolean = path.isEmpty() || path in paths

    fun children(path: List<Move>): List<List<Move>> =
        paths.filter { it.size == path.size + 1 && it.startsWith(path) }

    fun isLeaf(path: List<Move>): Boolean = children(path).isEmpty()

    fun outcome(path: List<Move>): Boolean? = outcomes[path]

    /** Adds the node and its missing parents. A parent that gets a new child loses its outcome. */
    fun add(path: List<Move>): SolutionTree {
        if (contains(path)) return this
        val missing = (1..path.size).map { path.take(it) }.filterNot { it in paths }
        return copy(
            paths = paths + missing,
            outcomes = path.dropLast(1).let { parent -> outcomes.filterKeys { it != parent } },
        )
    }

    /** Marks a leaf; [isCorrect] null removes the mark. Ignored for the root and for nodes with children. */
    fun mark(path: List<Move>, isCorrect: Boolean?): SolutionTree {
        if (path.isEmpty() || !contains(path) || !isLeaf(path)) return this
        val others = outcomes.filterKeys { it != path }
        return copy(outcomes = if (isCorrect == null) others else others + (path to isCorrect))
    }

    /** Removes the node and its whole subtree. */
    fun remove(path: List<Move>): SolutionTree {
        if (path.isEmpty()) return this
        return copy(
            paths = paths.filterNot { it.startsWith(path) },
            outcomes = outcomes.filterKeys { !it.startsWith(path) },
        )
    }

    /** True if a correct (or wrong if [isCorrect] is false) leaf is reachable from this node. */
    fun leadsTo(path: List<Move>, isCorrect: Boolean): Boolean =
        outcomes.any { (leaf, correct) -> correct == isCorrect && leaf.startsWith(path) }

    val hasCorrectAnswer: Boolean
        get() = outcomes.containsValue(true)

    /** One sequence per leaf: correct leaves are answers, other leaves are refutations. */
    fun toSequences(): List<SolutionSequence> =
        paths.filter(::isLeaf).map { leaf ->
            SolutionSequence(moves = leaf, isCorrect = outcomes[leaf] == true)
        }

    private fun List<Move>.startsWith(prefix: List<Move>): Boolean =
        size >= prefix.size && subList(0, prefix.size) == prefix

    companion object {
        /** Tree of a parsed SGF: only leaves keep their outcome (success = correct, failure = wrong). */
        fun fromRoot(root: RootNode): SolutionTree {
            val paths = mutableListOf<List<Move>>()
            val outcomes = mutableMapOf<List<Move>, Boolean>()
            fun visit(node: MoveNode, parent: List<Move>) {
                val path = parent + node.move
                paths += path
                if (node.children.isEmpty()) {
                    when (node.outcome) {
                        SgfNodeOutcome.SUCCESS -> outcomes[path] = true
                        SgfNodeOutcome.FAILURE -> outcomes[path] = false
                        SgfNodeOutcome.NONE -> Unit
                    }
                }
                node.children.forEach { visit(it, path) }
            }
            root.children.forEach { visit(it, emptyList()) }
            return SolutionTree(paths = paths, outcomes = outcomes)
        }
    }
}
