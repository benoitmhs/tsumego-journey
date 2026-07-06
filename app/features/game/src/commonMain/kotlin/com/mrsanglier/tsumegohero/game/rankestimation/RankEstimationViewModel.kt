package com.mrsanglier.tsumegohero.game.rankestimation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.game.game.delegate.BoardViewModelDelegate
import com.mrsanglier.tsumegohero.game.game.delegate.GameViewModelDelegate
import com.mrsanglier.tsumegohero.game.game.delegate.GameViewModelDelegateImpl
import com.mrsanglier.tsumegohero.game.model.BoardConfig
import com.mrsanglier.tsumegohero.game.model.GameOption
import com.mrsanglier.tsumegohero.game.rankestimation.composable.RankProgressBarState
import com.mrsanglier.tsumegohero.rankestimation.usecase.GetNextRankEstimationTsumegoUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.ObserveRankEstimationProgressUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.SubmitRankEstimationAnswerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val GAME_OPTION: GameOption = GameOption(autoPlay = true, ghost = true)
class RankEstimationViewModel(
    gameViewModelDelegateImpl: GameViewModelDelegateImpl,
    private val snackbarManager: SnackbarManager,
    private val submitRankEstimationAnswerUseCase: SubmitRankEstimationAnswerUseCase,
    private val getNextRankEstimationTsumegoUseCase: GetNextRankEstimationTsumegoUseCase,
    observeRankEstimationProgressUseCase: ObserveRankEstimationProgressUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    BoardViewModelDelegate by gameViewModelDelegateImpl,
    GameViewModelDelegate by gameViewModelDelegateImpl {

    private val args: RankEstimationDestination = savedStateHandle.toRoute()

    private var estimatedLevel: Level? = null

    internal val uiState: StateFlow<RankEstimationViewModelState> = combine(
        gameFlow,
        observeRankEstimationProgressUseCase(),
    ) { game, progress ->
        if (game == null) return@combine initialState()

        RankEstimationViewModelState(
            progress = progress,
            boardState = game.mapBoardUiState(),
            gameActionState = game.mapGameActionState(
                onClickReview = {
                    _navEvent.value = RankEstimationNavEvent.Review(game.sgf.id, game.boardConfig)
                },
                onClickNext = ::next,
                onSkipClick = ::skip,
            ),
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState(),
        )

    private val _navEvent = MutableStateFlow<RankEstimationNavEvent?>(null)
    internal val navEvent: StateFlow<RankEstimationNavEvent?> = _navEvent.asStateFlow()

    init {
        viewModelScope.launch {
            startTsumego(
                tsumegoId = args.tsumegoId,
                gameOption = GAME_OPTION,
            )
        }

        viewModelScope.launch {
            initObserveGame { rawTsumego, result ->
                submitRankEstimationAnswerUseCase(
                    result = result,
                    tsumego = rawTsumego,
                    resolutionTimeMs = getElapsedTime(),
                ).handleResult(
                    onSuccess = { level -> level?.let { estimatedLevel = it } },
                    onError = snackbarManager::showError,
                )
            }
        }
    }

    private fun initialState(): RankEstimationViewModelState = RankEstimationViewModelState(
        progress = 0f,
        gameActionState = initialGameActionState(),
        boardState = initialBoardUiState(),
    )

    private fun skip() {
        val game = gameFlow.value ?: return
        viewModelScope.launch {
            val submitResult = submitRankEstimationAnswerUseCase(
                result = Attempt.Result.Skip,
                tsumego = game.sgf,
                resolutionTimeMs = getElapsedTime(),
            )
            when (submitResult) {
                is THResult.Failure -> {
                    snackbarManager.showError(submitResult.error)
                    return@launch
                }

                is THResult.Success -> submitResult.successData?.let { estimatedLevel = it }
            }

            loadNextTsumego()
        }
    }

    private fun next() {
        viewModelScope.launch {
            loadNextTsumego()
        }
    }

    private suspend fun loadNextTsumego() {
        getNextRankEstimationTsumegoUseCase().handleResult(
            onSuccess = { tsumegoId ->
                if (tsumegoId != null) {
                    startTsumego(
                        tsumegoId = tsumegoId,
                        gameOption = GAME_OPTION,
                    )
                } else {
                    _navEvent.value = estimatedLevel
                        ?.let { RankEstimationNavEvent.Result(it) }
                        ?: RankEstimationNavEvent.Back
                    println("flash: ${estimatedLevel?.flashRank} | classical: ${estimatedLevel?.classicalRank} | difficile: ${estimatedLevel?.difficultRank}")
                }
            },
            onError = {
                snackbarManager.showError(it)
            }
        )
    }

    internal fun consumeNavigation() {
        _navEvent.value = null
    }
}

internal sealed interface RankEstimationNavEvent {
    data class Review(val tsumegoId: String, val boardConfig: BoardConfig) : RankEstimationNavEvent
    data class Result(val level: Level) : RankEstimationNavEvent
    data object Back : RankEstimationNavEvent
}
