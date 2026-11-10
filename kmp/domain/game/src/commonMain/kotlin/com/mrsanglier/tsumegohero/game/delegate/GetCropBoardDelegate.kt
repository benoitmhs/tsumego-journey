package com.mrsanglier.tsumegohero.game.delegate

import com.mrsanglier.tsumegohero.game.model.Board
import com.mrsanglier.tsumegohero.game.model.BoardSection
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Corner
import com.mrsanglier.tsumegohero.game.model.CropBoard
import com.mrsanglier.tsumegohero.game.model.MoveNode
import com.mrsanglier.tsumegohero.game.model.RootNode
import com.mrsanglier.tsumegohero.game.model.Tsumego
import kotlin.math.abs
import kotlin.math.sqrt

interface GetCropBoardDelegate {
    fun getCropBoard(tsumego: Tsumego): CropBoard

    /** Crop from an initial position and its solution tree (the tree may be empty). */
    fun getCropBoard(initialBoard: Board, root: RootNode): CropBoard
}

class GetCropBoardDelegateImpl : GetCropBoardDelegate {
    override fun getCropBoard(tsumego: Tsumego): CropBoard =
        getCropBoard(initialBoard = tsumego.initialBoard, root = tsumego.root)

    override fun getCropBoard(initialBoard: Board, root: RootNode): CropBoard {
        val boardBoardSection = BoardSection(
            0,
            0,
            initialBoard.boardSize.size - 1,
            initialBoard.boardSize.size - 1,
        )
        val tsumegoBoardSection = getTsumegoBoardSection(initialBoard, root)

        // Define closest corner from tsumego node & stones
        val tsumegoCorner: Corner = Corner.entries
            .map { corner ->
                cellDistance(
                    boardBoardSection.getClosestCell(corner),
                    tsumegoBoardSection.getClosestCell(corner),
                ) to corner
            }
            .minBy { (distance, _) -> distance }
            .second

        val cornerCell = boardBoardSection.getClosestCell(tsumegoCorner)
        val gameBoardSection = tsumegoBoardSection.includeCell(cornerCell)

        return CropBoard(gameBoardSection, tsumegoCorner)
    }

    private fun getTsumegoBoardSection(initialBoard: Board, root: RootNode): BoardSection {
        val initCells = initialBoard.blackStones + initialBoard.whiteStones
        val initBoardSection = BoardSection(
            xMin = initCells.minOf { it.x },
            xMax = initCells.maxOf { it.x },
            yMin = initCells.minOf { it.y },
            yMax = initCells.maxOf { it.y },
        )

        var boardSection = initBoardSection

        root.children.forEach { child ->
            boardSection = getBoardSectionNode(child, boardSection)
        }

        return boardSection
    }

    private fun getBoardSectionNode(node: MoveNode, boardSection: BoardSection): BoardSection {
        var newBoardSection = boardSection.includeCell(node.move.gameMove)
        node.children.forEach { child ->
            newBoardSection = getBoardSectionNode(child, newBoardSection)
        }

        return newBoardSection
    }

    private fun cellDistance(a: Cell, b: Cell): Double {
        val X = abs(a.x - b.x) * abs(a.x - b.x)
        val Y = abs(a.y - b.y) * abs(a.y - b.y)
        return sqrt(X.toDouble() + Y.toDouble())
    }

    private fun BoardSection.getClosestCell(corner: Corner): Cell = when (corner) {
        Corner.TopLeft -> Cell(xMin, yMin)
        Corner.TopRight -> Cell(xMax, yMin)
        Corner.BottomLeft -> Cell(xMin, yMax)
        Corner.BottomRight -> Cell(xMax, yMax)
    }
}