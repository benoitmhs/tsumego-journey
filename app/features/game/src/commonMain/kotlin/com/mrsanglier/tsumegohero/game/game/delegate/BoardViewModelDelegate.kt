package com.mrsanglier.tsumegohero.game.game.delegate

import androidx.compose.ui.graphics.Color
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.coreui.extension.composed
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.model.BoardConfig
import com.mrsanglier.tsumegohero.game.model.Game
import com.mrsanglier.tsumegohero.game.model.SgfNodeOutcome
import com.mrsanglier.tsumegohero.game.model.Stone
import com.mrsanglier.tsumegohero.game.usecase.StartGameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Loads a tsumego onto a board and keeps the resulting [Game] state, shared by every screen that
 * needs to display a board (Training, RankEstimation, Review) regardless of how they let the
 * player interact with it.
 */
interface BoardViewModelDelegate {
    val gameFlow: StateFlow<Game?>

    suspend fun loadTsumego(
        tsumegoId: String,
        mode: GameMode,
        boardConfig: BoardConfig? = null,
    ): THResult<Game>

    fun updateGame(game: Game)
    fun Game.mapBoardUiState(): BoardUiState
    fun initialBoardUiState(): BoardUiState
}

class BoardViewModelDelegateImpl(
    private val startGameUseCase: StartGameUseCase,
) : BoardViewModelDelegate {

    private val _gameFlow = MutableStateFlow<Game?>(null)
    override val gameFlow: StateFlow<Game?> = _gameFlow.asStateFlow()

    override suspend fun loadTsumego(
        tsumegoId: String,
        mode: GameMode,
        boardConfig: BoardConfig?,
    ): THResult<Game> {
        val result = startGameUseCase(tsumegoId, mode, boardConfig)
        if (result is THResult.Success) {
            updateGame(result.successData)
        }
        return result
    }

    override fun updateGame(game: Game) {
        _gameFlow.value = game
    }

    override fun Game.mapBoardUiState(): BoardUiState {
        val isGhostRevealed = !isGhostMode || isGhostSubmitted

        return BoardUiState(
            title = sgf.name.toTextSpec(),
            whiteStones = if (isGhostRevealed) board.whiteStones else tsumego.initialBoard.whiteStones,
            blackStones = if (isGhostRevealed) board.blackStones else tsumego.initialBoard.blackStones,
            cropBoard = cropBoard,
            lastMove = lastMove?.move,
            isGhostMode = isGhostMode,
            playerStone = when (playerStone) {
                Stone.BLACK -> "Black to play".toTextSpec() // TODO: loco
                Stone.WHITE -> "White to play".toTextSpec() // TODO: loco
            },
            result = when (outcome) {
                SgfNodeOutcome.NONE -> null
                SgfNodeOutcome.SUCCESS -> "✅ Correct".toTextSpec() // TODO: loco
                SgfNodeOutcome.FAILURE -> "❌ Incorrect".toTextSpec() // TODO: loco
            },
            borderColor = THTheme.composed {
                when (outcome) {
                    SgfNodeOutcome.NONE -> Color.Transparent
                    SgfNodeOutcome.SUCCESS -> colors.strokeSuccess
                    SgfNodeOutcome.FAILURE -> colors.strokeCritical
                }
            },
        )
    }

    override fun initialBoardUiState(): BoardUiState = BoardUiState()
}
