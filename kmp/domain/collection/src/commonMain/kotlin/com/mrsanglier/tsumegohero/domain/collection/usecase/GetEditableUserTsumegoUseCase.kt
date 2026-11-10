package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.core.error.THGameError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.domain.collection.model.EditableUserTsumego
import com.mrsanglier.tsumegohero.domain.collection.model.SolutionTree
import com.mrsanglier.tsumegohero.domain.collection.utils.UserTsumegoSgf
import com.mrsanglier.tsumegohero.game.model.Stone
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlinx.coroutines.flow.first

class GetEditableUserTsumegoUseCase(
    private val repository: CollectionRepository,
) {
    suspend operator fun invoke(tsumegoId: String): THResult<EditableUserTsumego> = THResult.catchResult {
        val tsumego = repository.observeTsumego(tsumegoId).first()
            ?: throw THGameError.Code.SgfFormatNotSupported.toError("Problem $tsumegoId not found")
        val (board, root) = UserTsumegoSgf.read(tsumego.sgf)
        EditableUserTsumego(
            boardSize = board.boardSize,
            blackStones = board.blackStones,
            whiteStones = board.whiteStones,
            firstPlayer = root.children.firstOrNull()?.move?.stone ?: Stone.BLACK,
            tree = SolutionTree.fromRoot(root),
            title = tsumego.title,
            rank = tsumego.rank,
            notes = tsumego.notes,
        )
    }
}
