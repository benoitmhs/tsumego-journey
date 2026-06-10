package com.example.database_generator.model

import com.example.database_generator.utils.copy

typealias Grid = Array<Array<Stone?>>

class Board(
    val boardSize: BoardSize,
    val grid: Grid =
        Array(boardSize.size) {
            Array(boardSize.size) { null }
        },
    val moveIndex: Int = 0,
    val previousHash: Long? = null,
    val capturedStone: Map<Int, Set<Cell>> = mapOf(),
) {

    val blackStones: Set<Cell>
        get() = grid.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, stone ->
                Cell(x, y).takeIf {
                    stone == Stone.BLACK
                }
            }
        }.toSet()

    val whiteStones: Set<Cell>
        get() = grid.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, stone ->
                Cell(x, y).takeIf {
                    stone == Stone.WHITE
                }
            }
        }.toSet()

    fun copy(
        previousHash: Long? = this.previousHash,
        moveIndex: Int = this.moveIndex,
        capturedStone: Map<Int, Set<Cell>> = this.capturedStone,
        grid: Grid = this.grid.copy(),
    ): Board = Board(
        boardSize = boardSize,
        grid = grid,
        previousHash = previousHash,
        moveIndex = moveIndex,
        capturedStone = capturedStone,
    )

    fun getStoneAt(cell: Cell): Stone? =
        if (onBoard(cell)) grid[cell.y][cell.x] else null

    fun setupStone(cell: Cell, stone: Stone) {
        if (cell.x in 0 until boardSize.size && cell.y in 0 until boardSize.size)
            grid[cell.y][cell.x] = stone
    }

    fun onBoard(cell: Cell): Boolean =
        cell.x in 0 until boardSize.size && cell.y in 0 until boardSize.size
}