package com.mrsanglier.tsumegohero.game.delegate

import com.mrsanglier.tsumegohero.game.model.Board
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Grid
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.MoveNode
import com.mrsanglier.tsumegohero.game.model.RootNode
import com.mrsanglier.tsumegohero.game.model.Stone
import com.mrsanglier.tsumegohero.game.model.Stone.Companion.getOpponent
import com.mrsanglier.tsumegohero.game.model.Tsumego

interface DeriveTsumegoDelegate {
    fun deriveTsumego(tsumego: Tsumego): Tsumego
}

class DeriveTsumegoDelegateImpl : DeriveTsumegoDelegate {
    override fun deriveTsumego(tsumego: Tsumego): Tsumego {
        val rotation = Rotation.entries.random()
        val changeColor = setOf(true, false).random()

        val newGrid = tsumego.initialBoard.grid.rotate(rotation = rotation, swapColor = changeColor)
        val newRoot = tsumego.root.children.map { node ->
            node.derive(
                rotation = rotation,
                boardSize = tsumego.initialBoard.boardSize,
                swapColor = changeColor,
            )
        }.toMutableList()

        return Tsumego(
            initialBoard = Board(
                grid = newGrid,
                boardSize = tsumego.initialBoard.boardSize,
            ),
            root = RootNode(newRoot),
            rank = tsumego.rank,
        )
    }

    private enum class Rotation {
        `90`, `180`, `-90`, None;
    }

    private fun Cell.rotate(rotation: Rotation, boardSize: BoardSize): Cell {
        val n = boardSize.size - 1
        return when (rotation) {
            Rotation.`90` -> Cell(n - y, x)
            Rotation.`180` -> Cell(n - x, n - y)
            Rotation.`-90` -> Cell(y, n - x)
            Rotation.None -> this
        }
    }

    private fun MoveNode.derive(rotation: Rotation, boardSize: BoardSize, swapColor: Boolean): MoveNode {
        return this.copy(
            move = Move(
                stone = if (swapColor) move.stone.getOpponent() else move.stone,
                gameMove = move.gameMove.rotate(rotation, boardSize),
            ),
            children = children.map { child ->
                child.derive(
                    rotation = rotation,
                    boardSize = boardSize,
                    swapColor = swapColor,
                )
            }.toMutableList(),
        )
    }

    private fun Grid.rotate(rotation: Rotation, swapColor: Boolean): Grid {
        val yOldLength = this.size
        val xOldLength = this.firstOrNull()?.size ?: 0

        val (xLength, yLength) = when (rotation) {
            Rotation.`180`, Rotation.None -> xOldLength to yOldLength
            Rotation.`90`, Rotation.`-90` -> yOldLength to xOldLength
        }
        val newGrid: Grid = Array(yLength) { Array(xLength) { null } }


        for (y in 0 until yLength)
            for (x in 0 until xLength) {
                val stone: Stone? = when (rotation) {
                    Rotation.None -> this[y][x]
                    Rotation.`90` -> this[xLength - 1 - x][y]
                    Rotation.`180` -> this[yLength - 1 - y][xLength - 1 - x]
                    Rotation.`-90` -> this[x][yLength - 1 - y]
                }
                newGrid[y][x] = if (swapColor) stone?.getOpponent() else stone
            }
        return newGrid
    }
}