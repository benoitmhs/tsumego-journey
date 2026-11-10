package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.game.delegate.PlayMoveDelegate
import com.mrsanglier.tsumegohero.game.delegate.PlayMoveDelegateImpl
import com.mrsanglier.tsumegohero.game.model.Board
import com.mrsanglier.tsumegohero.game.model.Move

/** Plays a move while entering solutions, with captures, suicide and ko rules applied. */
class PlayEditionMoveUseCase(
    playMoveDelegateImpl: PlayMoveDelegateImpl,
) : PlayMoveDelegate by playMoveDelegateImpl {

    /** @return the updated board, null if the move is illegal */
    operator fun invoke(board: Board, move: Move): Board? =
        playMoveOnBoard(previousBoard = board, move = move)
}
