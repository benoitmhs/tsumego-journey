package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.core.error.THGameError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import com.mrsanglier.tsumegohero.domain.collection.utils.UserTsumegoSgf
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegate
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegateImpl
import com.mrsanglier.tsumegohero.game.model.BoardConfig
import com.mrsanglier.tsumegohero.game.model.Game
import com.mrsanglier.tsumegohero.game.model.GameOption
import com.mrsanglier.tsumegohero.game.model.Rotation
import com.mrsanglier.tsumegohero.game.model.Tsumego
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlinx.coroutines.flow.first

/**
 * Starts a game on a problem of a user collection. The board is shown as entered (no rotation, no color swap),
 * and the SGF is read leniently: unlike bundled problems, the rank is optional.
 */
class StartUserTsumegoGameUseCase(
    private val repository: CollectionRepository,
    getCropBoardDelegateImpl: GetCropBoardDelegateImpl,
) : GetCropBoardDelegate by getCropBoardDelegateImpl {

    suspend operator fun invoke(tsumegoId: String, option: GameOption): THResult<Game> = THResult.catchResult {
        val userTsumego = repository.observeTsumego(tsumegoId).first()
            ?: throw THGameError.Code.SgfFormatNotSupported.toError("Problem $tsumegoId not found")
        val (board, root) = UserTsumegoSgf.read(userTsumego.sgf)
        if (root.children.isEmpty()) {
            throw THGameError.Code.SgfFormatNotSupported.toError("Problem $tsumegoId has no solution")
        }
        // The game model needs a rank: an unranked problem gets the lowest one, it is not displayed
        val rank = userTsumego.rank ?: Rank.entries.first()

        Game(
            sgf = RawTsumego(
                id = userTsumego.id,
                name = userTsumego.title.orEmpty(),
                data = userTsumego.sgf,
                rank = rank,
                updatedAt = userTsumego.createdAt,
            ),
            tsumego = Tsumego(initialBoard = board, root = root, rank = rank),
            board = board,
            moveStack = emptyList(),
            cropBoard = getCropBoard(initialBoard = board, root = root),
            option = option,
            boardConfig = BoardConfig(rotation = Rotation.None, changeColor = false),
        )
    }
}
