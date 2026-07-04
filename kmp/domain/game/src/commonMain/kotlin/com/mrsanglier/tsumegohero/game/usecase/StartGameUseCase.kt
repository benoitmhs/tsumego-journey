package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.game.delegate.DeriveTsumegoDelegate
import com.mrsanglier.tsumegohero.game.delegate.DeriveTsumegoDelegateImpl
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegate
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegateImpl
import com.mrsanglier.tsumegohero.game.delegate.ParseSgfTsumegoDelegate
import com.mrsanglier.tsumegohero.game.delegate.ParseSgfTsumegoDelegateImpl
import com.mrsanglier.tsumegohero.game.model.BoardConfig
import com.mrsanglier.tsumegohero.game.model.Game
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import kotlinx.coroutines.flow.first

class StartGameUseCase(
    private val tsumegoRepository: TsumegoRepository,
    parseSgfTsumegoDelegateImpl: ParseSgfTsumegoDelegateImpl,
    getCropBoardDelegateImpl: GetCropBoardDelegateImpl,
    deriveTsumegoDelegateImpl: DeriveTsumegoDelegateImpl,
) : ParseSgfTsumegoDelegate by parseSgfTsumegoDelegateImpl,
    GetCropBoardDelegate by getCropBoardDelegateImpl,
    DeriveTsumegoDelegate by deriveTsumegoDelegateImpl {

    suspend operator fun invoke(
        tsumegoId: String,
        mode: GameMode,
        boardConfig: BoardConfig? = null,
    ): THResult<Game> = THResult.catchResult {
        val sgf = tsumegoRepository.observeGame(tsumegoId).first()
        val tsumego = parseSgfTsumego(sgf.data)

        val boardConfigToApply = boardConfig ?: BoardConfig.random()
        val derivedTsumego = applyBoardConfig(tsumego, boardConfigToApply)

        return@catchResult Game(
            sgf = sgf,
            tsumego = derivedTsumego,
            board = derivedTsumego.initialBoard,
            moveStack = emptyList(),
            cropBoard = getCropBoard(derivedTsumego),
            mode = mode,
            boardConfig = boardConfigToApply,
        )
    }
}