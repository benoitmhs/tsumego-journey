package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegate
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegateImpl
import com.mrsanglier.tsumegohero.game.model.Board
import com.mrsanglier.tsumegohero.game.model.CropBoard
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.MoveNode
import com.mrsanglier.tsumegohero.game.model.RootNode
import com.mrsanglier.tsumegohero.game.model.SgfNodeOutcome

/**
 * Zoom used while entering solutions, computed like in a game: the problem corner,
 * fitted to the initial stones and to every move played so far.
 */
class GetEditionCropBoardUseCase(
    getCropBoardDelegateImpl: GetCropBoardDelegateImpl,
) : GetCropBoardDelegate by getCropBoardDelegateImpl {

    /** @return null (full board) when the board is empty */
    operator fun invoke(initialBoard: Board, sequences: List<List<Move>>): CropBoard? {
        if (initialBoard.blackStones.isEmpty() && initialBoard.whiteStones.isEmpty()) return null
        val root = RootNode(
            children = sequences
                .filter { it.isNotEmpty() }
                .mapNotNull { it.toNodeChain() }
                .toMutableList(),
        )
        return getCropBoard(initialBoard = initialBoard, root = root)
    }

    private fun List<Move>.toNodeChain(): MoveNode? =
        foldRight(null as MoveNode?) { move, child ->
            MoveNode(
                children = listOfNotNull(child).toMutableList(),
                move = move,
                outcome = SgfNodeOutcome.NONE,
            )
        }
}
