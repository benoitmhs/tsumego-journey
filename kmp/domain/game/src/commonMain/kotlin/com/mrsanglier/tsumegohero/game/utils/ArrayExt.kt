package com.mrsanglier.tsumegohero.game.utils

import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Grid
import com.mrsanglier.tsumegohero.game.model.Stone
import kotlin.random.Random

internal fun Grid.zobristHash(boardSize: BoardSize): Long {
    var hash = 0L
    for (y in 0 until boardSize.size) {
        for (x in 0 until boardSize.size) {
            when (this[y][x]) {
                Stone.BLACK -> hash = hash xor ArrayExt.zobristTable[y][x][0]
                Stone.WHITE -> hash = hash xor ArrayExt.zobristTable[y][x][1]
                null -> {}
            }
        }
    }
    return hash
}

private object ArrayExt {
    private val rng = Random(0xCAFEBABEL)
    val zobristTable: Array<Array<LongArray>> = Array(19) {
        Array(19) {
            LongArray(2) { rng.nextLong() }
        }
    }
}

internal fun Grid.copy(): Grid {
    val yLength = this.size
    val xLength = this.firstOrNull()?.size ?: 0
    val newGrid: Grid = Array(yLength) {
        Array(xLength) { null }
    }
    for (y in 0 until yLength)
        for (x in 0 until xLength)
            newGrid[y][x] = this[y][x]

    return newGrid
}