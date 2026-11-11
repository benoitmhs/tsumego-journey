package com.mrsanglier.tsumegohero.game.utils

import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.MoveNode
import com.mrsanglier.tsumegohero.game.model.RootNode
import com.mrsanglier.tsumegohero.game.model.SgfNode
import com.mrsanglier.tsumegohero.game.model.SgfNodeOutcome
import com.mrsanglier.tsumegohero.game.model.Stone

/**
 * Regexes are compiled once: compiling one costs far more than matching the short SGF tokens,
 * and [parseMoveNode] runs for every move of the tree.
 */
object SgfParser {
    private val TOKEN_REGEX = Regex("\\(|\\)|;[^;()]+")
    private val MOVE_REGEX = Regex(";([BW])\\[([^]]*)]")
    private val COMMENT_REGEX = Regex("C\\[([^]]+)]")
    private val CELL_REGEX = Regex("\\[([a-z]{2})]")
    // Immutable: the parser may run on several threads at once
    private val INITIAL_STONES_REGEXES: Map<String, Regex> =
        listOf("AB", "AW").associateWith { key -> Regex("$key(\\[[a-z]{2}])+") }

    private fun initialStonesRegex(key: String): Regex =
        INITIAL_STONES_REGEXES[key] ?: Regex("$key(\\[[a-z]{2}])+")

    fun parseInitialStones(sgf: String, key: String): List<Cell> =
        initialStonesRegex(key)
            .find(sgf)?.value
            ?.let {
                CELL_REGEX
                    .findAll(it)
                    .map { parseCell(it.groupValues[1]) }
                    .toList()
            } ?: emptyList()

    fun parseCell(s: String): Cell =
        Cell(s[0] - 'a', s[1] - 'a')

    fun tokenize(s: String): List<String> =
        TOKEN_REGEX
            .findAll(s)
            .map { it.value }
            .toList()

    fun parseTree(tokens: List<String>): RootNode {
        val root = RootNode()
        val nodeStack = ArrayDeque<SgfNode>()
        var currentNode: SgfNode = root

        tokens.forEach { token ->
            when (token) {
                "(" -> nodeStack.addLast(currentNode)
                ")" -> {
                    (currentNode as? MoveNode)?.resolveOutcome()
                    nodeStack.removeLastOrNull()?.let { node ->
                        currentNode = node
                    }
                }

                else -> {
                    parseMoveNode(token)?.let { node ->
                        currentNode.children.add(node)
                        currentNode = node
                    }
                }
            }
        }
        return root
    }

    fun parseMoveNode(token: String): MoveNode? {
        val moveMatch = MOVE_REGEX
            .find(token) ?: return null

        val stone =
            if (moveMatch.groupValues[1] == "B") Stone.BLACK else Stone.WHITE

        val coord = moveMatch.groupValues[2]
        if (coord.isEmpty()) return null

        val point = parseCell(coord)

        val comment =
            COMMENT_REGEX
                .find(token)?.groupValues?.get(1)?.lowercase() ?: ""

        val outcome = if (SUCCESS_COMMENT.contains(comment)) SgfNodeOutcome.SUCCESS else SgfNodeOutcome.NONE

        return MoveNode(
            move = Move(stone, point),
            children = mutableListOf(),
            outcome = outcome,
        )
    }

    fun MoveNode.resolveOutcome() {
        if (outcome != SgfNodeOutcome.SUCCESS && children.isEmpty()) {
            outcome = SgfNodeOutcome.FAILURE
        }
    }
}

private val SUCCESS_COMMENT: Set<String> = setOf("+", "correct")