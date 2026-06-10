package com.example.database_generator

import com.example.database_generator.model.Board
import com.example.database_generator.model.BoardSize
import com.example.database_generator.model.Rank
import com.example.database_generator.model.RawTsumego
import com.example.database_generator.model.Stone
import com.example.database_generator.model.Tsumego
import com.example.database_generator.utils.SgfParseException
import com.example.database_generator.utils.SgfParser.parseInitialStones
import com.example.database_generator.utils.SgfParser.parseTree
import com.example.database_generator.utils.SgfParser.tokenize
import kotlin.collections.forEach
import kotlin.collections.ifEmpty
import kotlin.time.Clock

fun parseRawTsumego(
    fileName: String,
    sgfData: String,
): RawTsumego? {
    // test parsing
    val tsumego = try {
        parseSgfTsumego(sgfData)
    } catch (e: Exception) {
        println("parsing Error $fileName : ${e.message}")
        return null
    }


    return RawTsumego(
        id = sgfData.hashCode().toString(),
        name = fileName,
        data = sgfData,
        updatedAt = Clock.System.now(),
        rank = tsumego.rank,
    )
}

private fun parseSgfTsumego(sgf: String): Tsumego {
    val cleaned = sgf.replace(Regex("\\s"), "")

    val size = Regex("SZ\\[(\\d+)]")
        .find(cleaned)?.groupValues?.get(1)?.toInt() ?: 19
    val difficulty = Regex("C\\[([^]]*)]")
        .find(cleaned)?.groupValues?.get(1)
        ?.uppercase()
        ?.let(Rank::safeValueOf)
        ?: throw SgfParseException("Tsumego rank is missing")
    val boardSize = BoardSize.fromValue(size)
        ?: throw SgfParseException("Invalid board size, Board size: $size")

    val board = Board(boardSize)

    parseInitialStones(cleaned, "AB")
        .ifEmpty {
            throw SgfParseException("Invalid initial black stones")
        }
        .forEach { cell ->
            board.setupStone(cell, Stone.BLACK)
        }
    parseInitialStones(cleaned, "AW")
        .ifEmpty {
            throw SgfParseException("Invalid initial white stones")
        }
        .forEach { cell ->
            board.setupStone(cell, Stone.WHITE)
        }

    val tokens = tokenize(cleaned).drop(2)
    val root = parseTree(tokens)

    if (root.children.isEmpty()) {
        throw SgfParseException("Invalid node format")
    }

    return Tsumego(
        initialBoard = board,
        root = root,
        rank = difficulty,
    )
}