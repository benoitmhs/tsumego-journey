package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.core.error.THGameError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.game.delegate.PlayMoveDelegate
import com.mrsanglier.tsumegohero.game.delegate.PlayMoveDelegateImpl
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Game
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.MoveNode
import com.mrsanglier.tsumegohero.game.model.SgfNodeOutcome
import com.mrsanglier.tsumegohero.game.model.Stone.Companion.getOpponent

/**
 * Plays a move for either color when autoPlay is disabled: the player controls both sides,
 * alternating colors, while the move is still matched against the solution tree so the outcome
 * can be determined once the sequence is submitted.
 */
class PlayBothColorsMoveUseCase(
    playMoveDelegateImpl: PlayMoveDelegateImpl,
) : PlayMoveDelegate by playMoveDelegateImpl {

    operator fun invoke(
        game: Game,
        cell: Cell,
    ): THResult<Game> = THResult.catchResult {
        val stone = game.lastMove?.move?.stone?.getOpponent() ?: game.playerStone
        val move = Move(stone = stone, gameMove = cell)

        val boardUpdated = playMoveOnBoard(
            previousBoard = game.board,
            move = move,
        ) ?: throw THGameError.Code.InvalidMove.toError()

        val possibleNodes = game.lastMove?.children
            ?: game.tsumego.root.children.map { it.clone() }
        val currentNode = possibleNodes.firstOrNull { (_, nodeMove, _) ->
            nodeMove == move
        } ?: MoveNode(
            children = mutableListOf(),
            move = move,
            outcome = SgfNodeOutcome.FAILURE,
        )

        return@catchResult game.copy(
            board = boardUpdated,
            moveStack = game.moveStack + listOf(currentNode),
        )
    }
}
