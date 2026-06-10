package com.example.database_generator.utils

import com.example.database_generator.model.Cell
import com.example.database_generator.model.Move
import com.example.database_generator.model.MoveNode
import com.example.database_generator.model.RootNode
import com.example.database_generator.model.SgfNode
import com.example.database_generator.model.SgfNodeOutcome
import com.example.database_generator.model.Stone

object SgfParser {
    fun parseInitialStones(sgf: String, key: String): List<Cell> =
        Regex("$key(\\[[a-z]{2}])+")
            .find(sgf)?.value
            ?.let {
                Regex("\\[([a-z]{2})]")
                    .findAll(it)
                    .map { parseCell(it.groupValues[1]) }
                    .toList()
            } ?: emptyList()

    fun parseCell(s: String): Cell =
        Cell(s[0] - 'a', s[1] - 'a')

    fun tokenize(s: String): List<String> =
        Regex("\\(|\\)|;[^;()]+")
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
        val moveMatch = Regex(";([BW])\\[([^]]*)]")
            .find(token) ?: return null

        val stone =
            if (moveMatch.groupValues[1] == "B") Stone.BLACK else Stone.WHITE

        val coord = moveMatch.groupValues[2]
        if (coord.isEmpty()) return null

        val point = parseCell(coord)

        val comment =
            Regex("C\\[([^]]+)]")
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