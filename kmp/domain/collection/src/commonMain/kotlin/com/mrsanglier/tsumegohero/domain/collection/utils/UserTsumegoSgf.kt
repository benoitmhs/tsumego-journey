package com.mrsanglier.tsumegohero.domain.collection.utils

import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.domain.collection.model.TsumegoDraft
import com.mrsanglier.tsumegohero.game.model.Board
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.RootNode
import com.mrsanglier.tsumegohero.game.model.Stone
import com.mrsanglier.tsumegohero.game.utils.SgfParser

/**
 * Reads and writes the SGF of user problems.
 *
 * The written SGF stays readable by the training parser (`ParseSgfTsumegoDelegate`) when a rank is set:
 * the rank is the first comment of the root node and correct moves are tagged with `C[+]`.
 * Title and notes are not written: free text could break the parser, they are stored next to the SGF.
 */
object UserTsumegoSgf {

    fun write(draft: TsumegoDraft, rank: Rank?): String = buildString {
        append("(;GM[1]FF[4]CA[UTF-8]SZ[${draft.boardSize.size}]")
        rank?.let { append("C[${it.rawValue}]") }
        if (draft.blackStones.isNotEmpty()) {
            append("AB")
            draft.blackStones.sortedWith(cellComparator).forEach { append("[${it.toSgf()}]") }
        }
        if (draft.whiteStones.isNotEmpty()) {
            append("AW")
            draft.whiteStones.sortedWith(cellComparator).forEach { append("[${it.toSgf()}]") }
        }
        buildTree(draft).forEach { appendNode(it) }
        append(")")
    }

    /** @return the initial position and the solution tree of a user problem. */
    fun read(sgf: String): Pair<Board, RootNode> {
        val cleaned = sgf.replace(Regex("\\s"), "")
        val size = Regex("SZ\\[(\\d+)]").find(cleaned)?.groupValues?.get(1)?.toInt() ?: BoardSize.X19.size
        val board = Board(BoardSize.fromValue(size) ?: BoardSize.X19)
        SgfParser.parseInitialStones(cleaned, "AB").forEach { board.setupStone(it, Stone.BLACK) }
        SgfParser.parseInitialStones(cleaned, "AW").forEach { board.setupStone(it, Stone.WHITE) }
        val root = SgfParser.parseTree(SgfParser.tokenize(cleaned).drop(2))
        return board to root
    }

    /** Rank written as the first comment of the root node (same convention as the bundled problems). */
    fun readRank(sgf: String): Rank? =
        rootNode(sgf.replace(Regex("\\s"), ""))
            ?.let { Regex("(?<![A-Z])C\\[([^]]*)]").find(it)?.groupValues?.get(1) }
            ?.uppercase()
            ?.let(Rank::safeValueOf)

    /** Game name (`GN`) of the root node, if any. */
    fun readGameName(sgf: String): String? =
        rootNode(sgf)
            ?.let { Regex("GN\\[([^]]*)]").find(it)?.groupValues?.get(1) }
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

    private fun rootNode(sgf: String): String? =
        SgfParser.tokenize(sgf).getOrNull(1)?.takeIf { it.startsWith(";") }

    private class Node(
        val move: Move,
        var isCorrect: Boolean = false,
        val children: MutableList<Node> = mutableListOf(),
    )

    /** Merges every sequence into one tree: sequences sharing their first moves share the same nodes. */
    private fun buildTree(draft: TsumegoDraft): List<Node> {
        val roots = mutableListOf<Node>()
        draft.solutions.forEach { sequence ->
            var siblings = roots
            var last: Node? = null
            sequence.moves.forEach { move ->
                val node = siblings.firstOrNull { it.move == move } ?: Node(move).also(siblings::add)
                last = node
                siblings = node.children
            }
            last?.isCorrect = sequence.isCorrect
        }
        return roots
    }

    private fun StringBuilder.appendNode(node: Node) {
        append("(;")
        append(if (node.move.stone == Stone.BLACK) "B" else "W")
        append("[${node.move.gameMove.toSgf()}]")
        if (node.isCorrect) append("C[+]")
        node.children.forEach { appendNode(it) }
        append(")")
    }

    private fun Cell.toSgf(): String = "${'a' + x}${'a' + y}"

    private val cellComparator: Comparator<Cell> = compareBy<Cell> { it.y }.thenBy { it.x }
}
