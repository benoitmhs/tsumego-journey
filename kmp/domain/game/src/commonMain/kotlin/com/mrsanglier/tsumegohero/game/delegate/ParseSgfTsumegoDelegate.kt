package com.mrsanglier.tsumegohero.game.delegate

import com.mrsanglier.tsumegohero.core.error.THGameError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.game.model.Board
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Stone
import com.mrsanglier.tsumegohero.game.model.Tsumego
import com.mrsanglier.tsumegohero.game.utils.SgfParser.parseInitialStones
import com.mrsanglier.tsumegohero.game.utils.SgfParser.parseTree
import com.mrsanglier.tsumegohero.game.utils.SgfParser.tokenize

interface ParseSgfTsumegoDelegate {
    fun parseSgfTsumego(sgf: String): Tsumego
}

class ParseSgfTsumegoDelegateImpl : ParseSgfTsumegoDelegate {
    override fun parseSgfTsumego(sgf: String): Tsumego {
        val cleaned = sgf.replace(Regex("\\s"), "")

        val size = Regex("SZ\\[(\\d+)]")
            .find(cleaned)?.groupValues?.get(1)?.toInt() ?: 19
        val difficulty = Regex("C\\[([^]]*)]")
            .find(cleaned)?.groupValues?.get(1)
            ?.uppercase()
            ?.let(Rank::safeValueOf)
            ?: throw THGameError.Code.SgfFormatNotSupported.toError("Tsumego rank is missing")
        val boardSize = BoardSize.fromValue(size)
            ?: throw THGameError.Code.SgfFormatNotSupported.toError("Invalid board size", "Board size: $size")

        val board = Board(boardSize)

        parseInitialStones(cleaned, "AB")
            .ifEmpty {
                throw THGameError.Code.SgfFormatNotSupported.toError("Invalid initial black stones")
            }
            .forEach { cell ->
                board.setupStone(cell, Stone.BLACK)
            }
        parseInitialStones(cleaned, "AW")
            .ifEmpty {
                throw THGameError.Code.SgfFormatNotSupported.toError("Invalid initial white stones")
            }
            .forEach { cell ->
                board.setupStone(cell, Stone.WHITE)
            }

        val tokens = tokenize(cleaned).drop(2)
        val root = parseTree(tokens)

        if (root.children.isEmpty()) {
            throw THGameError.Code.SgfFormatNotSupported.toError("Invalid node format")
        }

        return Tsumego(
            initialBoard = board,
            root = root,
            rank = difficulty,
        )
    }
}