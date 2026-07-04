package com.mrsanglier.tsumegohero.game.game.delegate

import com.mrsanglier.tsumegohero.core.error.THGameError
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.game.section.GameActionState
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Game
import com.mrsanglier.tsumegohero.game.model.SgfNodeOutcome
import com.mrsanglier.tsumegohero.game.usecase.PlayFreeMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayGhostMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayOpponentMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayPlayerMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.RestartGameUseCase
import com.mrsanglier.tsumegohero.game.usecase.SubmitGhostSequenceUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

internal val OPPONENT_TURN_DELAY: Duration = 300.milliseconds
internal val GHOST_STONE_FADE_DURATION: Duration = 1000.milliseconds

interface GameViewModelDelegate {
    fun Game.mapGameActionState(
        onClickReview: () -> Unit,
        onClickNext: () -> Unit,
        onSkipClick: () -> Unit,
    ): GameActionState

    fun initialGameActionState(): GameActionState

    fun onClickCell(cell: Cell)
    suspend fun startTsumego(tsumegoId: String, gameMode: GameMode)
    fun getElapsedTime(): Long
    suspend fun initClassicGame(submitResult: suspend (String, Attempt.Result) -> Unit)

}

class GameViewModelDelegateImpl(
    boardDelegate: BoardViewModelDelegateImpl,
    private val playFreeMoveUseCase: PlayFreeMoveUseCase,
    private val playPlayerMoveUseCase: PlayPlayerMoveUseCase,
    private val playGhostMoveUseCase: PlayGhostMoveUseCase,
    private val playOpponentMoveUseCase: PlayOpponentMoveUseCase,
    private val restartGameUseCase: RestartGameUseCase,
    private val submitGhostSequenceUseCase: SubmitGhostSequenceUseCase,
    private val snackbarManager: SnackbarManager,
) : GameViewModelDelegate,
    BoardViewModelDelegate by boardDelegate {

    private val lockTouch = MutableStateFlow(false)
    private var problemStartedAt: Instant? = null

    override fun Game.mapGameActionState(
        onClickReview: () -> Unit,
        onClickNext: () -> Unit,
        onSkipClick: () -> Unit,
    ): GameActionState {
        val ghostMovePending = isGhostMode && !isGhostSubmitted
        return GameActionState(
            validateButton = when {
                ghostMovePending && lastMove != null -> defaultSubmitButton(::submitGhostSequence)
                ghostMovePending -> defaultSubmitButton(::submitGhostSequence).copy(enabled = false)
                !isGhostMode && outcome == SgfNodeOutcome.NONE -> defaultNextButton(onClickNext).copy(enabled = false)
                else -> defaultNextButton(onClickNext).copy(enabled = true)

            },
            skipButton = when {
                isGhostMode && !isGhostSubmitted || !isGhostMode && outcome == SgfNodeOutcome.NONE -> defaultSkipButton(onSkipClick)
                else -> null
            },
            actionButton = when {
                ghostMovePending && lastMove != null -> defaultRestartButton(::reset)
                !isGhostMode && outcome == SgfNodeOutcome.NONE && lastMove != null -> defaultRestartButton(::reset)
                outcome != SgfNodeOutcome.NONE || isGhostSubmitted -> defaultReviewButton(onClickReview)
                else -> null
            },
        )
    }

    override fun initialGameActionState(): GameActionState = GameActionState(
        validateButton = defaultNextButton {},
        skipButton = null,
        actionButton = null,
    )

    override fun Game.mapBoardUiState(): BoardUiState = mapBoardUiState()

    override fun onClickCell(cell: Cell) {
        val game = gameFlow.value ?: return

        when {
            game.isGhostMode && !game.isGhostSubmitted -> playGhostMove(cell, game)
            game.outcome == SgfNodeOutcome.NONE -> playPlayerMove(cell, game)
            else -> playFreeMove(cell, game)
        }
    }

    private fun playGhostMove(cell: Cell, game: Game) {
        playGhostMoveUseCase(game, cell).handleResult(
            onSuccess = ::updateGame,
            onError = { error ->
                when (error?.code) {
                    THGameError.Code.InvalidMove -> Unit
                    else -> snackbarManager.showError(error)
                }
            },
        )
    }

    private fun playFreeMove(cell: Cell, game: Game) {
        playFreeMoveUseCase(game = game, cell = cell)?.let(::updateGame)
    }

    private fun playPlayerMove(cell: Cell, game: Game) {
        if (lockTouch.value) return
        lockTouch.value = true

        playPlayerMoveUseCase(game, cell).handleResult(
            onSuccess = ::updateGame,
            onError = { error ->
                when (error?.code) {
                    THGameError.Code.InvalidMove, THGameError.Code.WrongPlayerTurn -> Unit
                    else -> snackbarManager.showError(error)
                }
                lockTouch.value = false
            },
        )
    }

    internal fun reset() {
        gameFlow.value?.let { updateGame(restartGameUseCase(it)) }
    }

    internal fun submitGhostSequence() {
        gameFlow.value?.let { updateGame(submitGhostSequenceUseCase(it)) }
    }

    override suspend fun startTsumego(tsumegoId: String, gameMode: GameMode) {
        loadTsumego(tsumegoId, gameMode).handleResult(
            onSuccess = { problemStartedAt = Clock.System.now() },
            onError = snackbarManager::showError,
        )
    }

    override suspend fun initClassicGame(
        submitResult: suspend (tsumegoId: String, Attempt.Result) -> Unit,
    ) {
        gameFlow.collect { game ->
            if (game == null) return@collect
            lockTouch.value = game.isOpponentTurn
            if (game.isOpponentTurn) playOpponentTurn(game)

            if (game.outcome != SgfNodeOutcome.NONE) {
                submitResult(
                    game.sgf.id,
                    if (game.outcome == SgfNodeOutcome.SUCCESS) Attempt.Result.Success else Attempt.Result.Failure,
                )
            }
        }
    }

    override fun getElapsedTime(): Long =
        problemStartedAt?.let { (Clock.System.now() - it).inWholeMilliseconds } ?: 0L

    private suspend fun playOpponentTurn(game: Game) {
        game.lastMove?.children?.randomOrNull()?.let {
            playOpponentMoveUseCase(game).handleResult(
                onSuccess = { data ->
                    if (data != null) {
                        delay(OPPONENT_TURN_DELAY)
                        updateGame(data)
                    }
                },
                onError = snackbarManager::showError,
            )
        }
    }
}
