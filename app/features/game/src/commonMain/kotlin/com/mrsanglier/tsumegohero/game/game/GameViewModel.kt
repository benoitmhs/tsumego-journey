package com.mrsanglier.tsumegohero.game.game

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.core.error.THGameError
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.core.extension.suspendHandleResult
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showDone
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.coreui.extension.composed
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Game
import com.mrsanglier.tsumegohero.game.model.SgfNodeOutcome
import com.mrsanglier.tsumegohero.game.model.Stone
import com.mrsanglier.tsumegohero.game.usecase.GetNextTsumegoIdUseCase
import com.mrsanglier.tsumegohero.game.usecase.NavigateReviewUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayFreeMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayGhostMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayOpponentMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayPlayerMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayReviewMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.RestartGameUseCase
import com.mrsanglier.tsumegohero.game.usecase.SendGameResultUseCase
import com.mrsanglier.tsumegohero.game.usecase.StartGameUseCase
import com.mrsanglier.tsumegohero.game.usecase.StartReviewUseCase
import com.mrsanglier.tsumegohero.game.usecase.SubmitGhostSequenceUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.GetNextRankEstimationTsumegoUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.GetRankEstimationResultUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.SubmitRankEstimationAnswerUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

internal val OPPONENT_TURN_DELAY: Duration = 300.milliseconds
internal val GHOST_STONE_FADE_DURATION: Duration = 1000.milliseconds

class GameViewModel(
    private val playFreeMoveUseCase: PlayFreeMoveUseCase,
    private val playPlayerMoveUseCase: PlayPlayerMoveUseCase,
    private val playOpponentMoveUseCase: PlayOpponentMoveUseCase,
    private val playReviewMoveUseCase: PlayReviewMoveUseCase,
    private val playGhostMoveUseCase: PlayGhostMoveUseCase,
    private val submitGhostSequenceUseCase: SubmitGhostSequenceUseCase,
    private val sendGameResultUseCase: SendGameResultUseCase,
    private val startGameUseCase: StartGameUseCase,
    private val startReviewUseCase: StartReviewUseCase,
    private val restartGameUseCase: RestartGameUseCase,
    private val snackbarManager: SnackbarManager,
    private val getNextTsumegoIdUseCase: GetNextTsumegoIdUseCase,
    private val navigateReviewUseCase: NavigateReviewUseCase,
    private val getNextRankEstimationTsumegoUseCase: GetNextRankEstimationTsumegoUseCase,
    private val submitRankEstimationAnswerUseCase: SubmitRankEstimationAnswerUseCase,
    private val getRankEstimationResultUseCase: GetRankEstimationResultUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args: GameDestination = savedStateHandle.toRoute()

    private val gameFlow = MutableStateFlow<Game?>(null)
    private val lockTouch = MutableStateFlow(false)
    private var problemStartedAt: Instant? = null

    internal val navEvent = MutableStateFlow<NavEvent?>(null)

    init {

        viewModelScope.launch {
            loadTsumego(
                tsumegoId = args.tsumegoId,
                mode = args.gameMode,
            )
        }

        viewModelScope.launch {
            gameFlow.collect { data ->
                if (data?.isReview == true) return@collect
                lockTouch.emit(data?.isOpponentTurn == true)
                if (data?.isOpponentTurn == true) {
                    playOpponentTurn(data)
                }

                data?.let {
                    sendGameResult(data)
                }
            }
        }
    }

    val uiState: StateFlow<GameViewModelState> = gameFlow.map { game ->
        if (game == null) return@map initialState()

        val outcome = game.outcome.takeIf { !game.isReview }
            ?: SgfNodeOutcome.NONE
        val (goodMoves, badMoves) = game.reviewNextMove?.let { reviewMoves ->
            reviewMoves.partition { it.outcome == SgfNodeOutcome.SUCCESS }
        } ?: (null to null)
        val isGhostRevealed = !game.isGhostMode || game.isGhostSubmitted

        GameViewModelState(
            title = game.sgf.name.toTextSpec(),
            whiteStones = if (isGhostRevealed) game.board.whiteStones else game.tsumego.initialBoard.whiteStones,
            blackStones = if (isGhostRevealed) game.board.blackStones else game.tsumego.initialBoard.blackStones,
            lastMove = game.lastMove?.move,
            cropBoard = game.cropBoard,
            playerStone = when (game.playerStone) {
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
            nextButton = defaultNextButton.copy(
                style = if (outcome == SgfNodeOutcome.SUCCESS) {
                    ButtonStyle.Primary
                } else ButtonStyle.Secondary,
                text = if (outcome == SgfNodeOutcome.SUCCESS) {
                    "Next".toTextSpec() // TODO: loco
                } else null,
            ),
            resetButton = defaultRestartButton.copy(
                style = if (isGhostRevealed && game.lastMove?.outcome == SgfNodeOutcome.FAILURE) {
                    ButtonStyle.Primary
                } else ButtonStyle.Secondary,
                text = "Restart".toTextSpec(), // TODO: loco
            ),
            isReview = game.isReview,
            reviewButton = defaultReviewButton.takeIf { !game.isReview && outcome != SgfNodeOutcome.NONE },
            reviewPreviousButton = defaultReviewPreviousButton
                .takeIf { game.isReview }
                ?.copy(enabled = game.reviewIndex > 0),
            reviewNextButton = defaultReviewNextButton
                .takeIf { game.isReview }
                ?.copy(enabled = game.reviewIndex < (game.moveStack.size - 1) || game.nextGoodMove.isNotEmpty()),
            reviewResetButton = defaultReviewResetButton
                .takeIf { game.isReview }
                ?.copy(enabled = game.moveStack.isNotEmpty()),
            goodMoves = goodMoves?.map { it.move.gameMove }?.toSet(),
            badMoves = badMoves?.map { it.move.gameMove }?.toSet(),
            isGhostMode = game.isGhostMode,
            submitButton = defaultSubmitButton
                .takeIf { game.isGhostMode && !game.isGhostSubmitted }
                ?.copy(enabled = game.moveStack.isNotEmpty()),
            skipButton = defaultSkipButton.takeIf { outcome == SgfNodeOutcome.NONE },
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState(),
        )

    fun onClickCell(cell: Cell) {
        val game = gameFlow.value ?: return

        when {
            game.isReview -> playReviewMove(cell = cell, game = game)
            game.isGhostMode && !game.isGhostSubmitted -> playGhostMove(cell = cell, game = game)
            game.outcome == SgfNodeOutcome.NONE -> playPlayerMove(cell = cell, game = game)
            else -> playFreeMove(cell = cell, game = game)
        }
    }

    private fun playGhostMove(cell: Cell, game: Game) {
        playGhostMoveUseCase(game, cell).handleResult(
            onSuccess = { data ->
                gameFlow.value = data
            },
            onError = { error ->
                when (error?.code) {
                    THGameError.Code.InvalidMove -> Unit
                    else -> snackbarManager.showError(error)
                }
            },
        )
    }

    internal fun submitGhostSequence() {
        gameFlow.value?.let { game ->
            gameFlow.value = submitGhostSequenceUseCase(game)
        }
    }

    private fun playFreeMove(cell: Cell, game: Game) {
        playFreeMoveUseCase(game = game, cell = cell)?.let { gameUpdated ->
            gameFlow.value = gameUpdated
        }
    }

    private fun playPlayerMove(cell: Cell, game: Game) {
        if (!lockTouch.value) {
            lockTouch.value = true

            playPlayerMoveUseCase(game, cell).handleResult(
                onSuccess = { data ->
                    gameFlow.value = data
                },
                onError = { error ->
                    when (error?.code) {
                        THGameError.Code.InvalidMove, THGameError.Code.WrongPlayerTurn -> Unit

                        else -> snackbarManager.showError(error)
                    }
                    lockTouch.value = false
                },
            )
        }
    }

    internal fun navigateReview(isBack: Boolean) {
        gameFlow.value?.let { game ->
            navigateReviewUseCase(
                game = game,
                isBack = isBack,
            )
        }?.let { newGame ->
            gameFlow.value = newGame
        }
    }

    private fun playReviewMove(cell: Cell, game: Game) {
        playReviewMoveUseCase(cell = cell, game = game)?.let { gameUpdated ->
            gameFlow.value = gameUpdated
        }
    }

    internal fun next() {
        viewModelScope.launch {
            when (args.context) {
                GameContext.RankEstimation -> loadNextRankEstimationTsumego()
                GameContext.Training -> loadNextTsumego()
            }
        }
    }

    internal fun skip() {
        val game = gameFlow.value ?: return

        viewModelScope.launch {
            when (args.context) {
                GameContext.RankEstimation -> {
                    submitRankEstimationAnswerUseCase(
                        tsumegoId = game.sgf.id,
                        resolutionTimeMs = elapsedResolutionTimeMs(),
                        result = Attempt.Result.Skip,
                        gameMode = args.gameMode,
                    ).suspendHandleResult(
                        onSuccess = { loadNextRankEstimationTsumego() },
                        onError = snackbarManager::showError,
                    )
                }

                GameContext.Training -> {
                    sendGameResultUseCase(
                        result = Attempt.Result.Skip,
                        tsumegoId = args.tsumegoId,
                        mode = args.gameMode,
                        resolutionTimeMs = elapsedResolutionTimeMs(),
                        gameContext = args.context,
                    ).handleResult(
                        onSuccess = {},
                        onError = snackbarManager::showError,
                    )
                }
            }
        }
    }

    private fun loadNextTsumego() {
        gameFlow.value?.let { currentGame ->
            viewModelScope.launch {
                getNextTsumegoIdUseCase().handleResult(
                    onSuccess = { tsumegoId ->
                        loadTsumego(tsumegoId, mode = currentGame.mode)
                    },
                    onError = snackbarManager::showError,
                )
            }
        }
    }

    internal fun reset() {
        gameFlow.value?.let { game ->
            gameFlow.value = restartGameUseCase(game)
        }
    }

    internal fun startReview() {
        gameFlow.value?.let { game ->
            gameFlow.value = startReviewUseCase(game = game)
        }
    }

    private fun initialState(): GameViewModelState = GameViewModelState(
        borderColor = { Color.Transparent },
        nextButton = defaultNextButton,
        resetButton = defaultRestartButton,
    )

    private suspend fun loadTsumego(tsumegoId: String, mode: GameMode = GameMode.Ghost) {
        startGameUseCase(tsumegoId, mode).handleResult(
            onSuccess = { data ->
                gameFlow.value = data
                problemStartedAt = Clock.System.now()
            },
            onError = snackbarManager::showError,
        )
    }

    internal suspend fun loadNextRankEstimationTsumego() {
        getNextRankEstimationTsumegoUseCase().handleResult(
            onSuccess = { tsumegoId ->
                if (tsumegoId == null) {
                    finishRankEstimation()
                } else {
                    loadTsumego(
                        tsumegoId = tsumegoId,
                        mode = GameMode.Ghost,
                    )
                }
            },
            onError = snackbarManager::showError,
        )
    }

    private fun elapsedResolutionTimeMs(): Long =
        problemStartedAt?.let { (Clock.System.now() - it).inWholeMilliseconds } ?: 0L

    private suspend fun finishRankEstimation() {
        getRankEstimationResultUseCase().handleResult(
            onSuccess = { level ->
                val message = if (level != null) {
                    "Level determined — difficult: ${level.difficultRank.rawValue}".toTextSpec() // TODO: loco
                } else {
                    "No rank validated yet, try again later".toTextSpec() // TODO: loco
                }
                snackbarManager.showDone(message)
                navEvent.value = NavEvent.Finished
            },
            onError = snackbarManager::showError,
        )
    }

    internal fun consumeNavigation() {
        navEvent.value = null
    }

    internal sealed interface NavEvent {
        data object Finished : NavEvent
    }

    private suspend fun playOpponentTurn(game: Game) {
        game.lastMove?.children?.randomOrNull()?.let { opponentNode ->
            playOpponentMoveUseCase(game).handleResult(
                onSuccess = { data ->
                    if (data != null) {
                        delay(OPPONENT_TURN_DELAY)
                        gameFlow.emit(data)
                    }
                },
                onError = snackbarManager::showError,
            )
        }
    }

    private suspend fun sendGameResult(data: Game) {
        val outcome = data.outcome
        if (outcome != SgfNodeOutcome.NONE) {
            val elapsedMs = elapsedResolutionTimeMs()
            val result = if (outcome == SgfNodeOutcome.SUCCESS) Attempt.Result.Success else Attempt.Result.Failure

            when (args.context) {
                GameContext.RankEstimation -> {
                    submitRankEstimationAnswerUseCase(
                        tsumegoId = data.sgf.id,
                        resolutionTimeMs = elapsedMs,
                        result = result,
                        gameMode = args.gameMode,
                    ).handleResult(
                        onSuccess = {},
                        onError = snackbarManager::showError,
                    )
                }

                GameContext.Training -> {
                    sendGameResultUseCase(
                        result = result,
                        tsumegoId = data.sgf.id,
                        mode = args.gameMode,
                        resolutionTimeMs = elapsedMs,
                        gameContext = args.context,
                    ).handleResult(
                        onSuccess = {},
                        onError = snackbarManager::showError,
                    )
                }
            }
        }
    }
}