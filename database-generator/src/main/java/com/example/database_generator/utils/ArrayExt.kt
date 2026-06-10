package com.example.database_generator.utils

import com.example.database_generator.model.BoardSize
import com.example.database_generator.model.Grid
import com.example.database_generator.model.Stone
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
    val yLenght = this.size
    val xLenght = this.firstOrNull()?.size ?: 0
    val newGrid: Grid = Array(yLenght) {
        Array(xLenght) { null }
    }
    for (y in 0 until yLenght)
        for (x in 0 until xLenght)
            newGrid[y][x] = this[y][x]

    return newGrid
}