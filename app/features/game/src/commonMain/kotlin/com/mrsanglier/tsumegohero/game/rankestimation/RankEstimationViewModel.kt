package com.mrsanglier.tsumegohero.game.rankestimation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showDone
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.game.game.delegate.BoardViewModelDelegate
import com.mrsanglier.tsumegohero.game.game.delegate.GameViewModelDelegate
import com.mrsanglier.tsumegohero.game.game.delegate.GameViewModelDelegateImpl
import com.mrsanglier.tsumegohero.game.model.BoardConfig
import com.mrsanglier.tsumegohero.rankestimation.usecase.GetNextRankEstimationTsumegoUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.SubmitRankEstimationAnswerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RankEstimationViewModel(
    gameViewModelDelegateImpl: GameViewModelDelegateImpl,
    private val snackbarManager: SnackbarManager,
    private val submitRankEstimationAnswerUseCase: SubmitRankEstimationAnswerUseCase,
    private val getNextRankEstimationTsumegoUseCase: GetNextRankEstimationTsumegoUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    BoardViewModelDelegate by gameViewModelDelegateImpl,
    GameViewModelDelegate by gameViewModelDelegateImpl {

    private val args: RankEstimationDestination = savedStateHandle.toRoute()

    val uiState: StateFlow<RankEstimationViewModelState> = gameFlow.map { game ->
        if (game == null) return@map initialState()

        RankEstimationViewModelState(
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
                gameMode = args.gameMode,
            )
        }

        if (args.gameMode == GameMode.Standard) {
            viewModelScope.launch {
                initObserveGame { tsumegoId, result ->
                    submitRankEstimationAnswerUseCase(
                        result = result,
                        tsumegoId = tsumegoId,
                        gameMode = args.gameMode,
                        resolutionTimeMs = getElapsedTime(),
                    ).handleResult(
                        onSuccess = {},
                        onError = snackbarManager::showError,
                    )
                }
            }
        }
    }

    private fun initialState(): RankEstimationViewModelState = RankEstimationViewModelState(
        gameActionState = initialGameActionState(),
        boardState = initialBoardUiState(),
    )

    private fun skip() {
        val game = gameFlow.value ?: return
        viewModelScope.launch {
            val submitResult = submitRankEstimationAnswerUseCase(
                result = Attempt.Result.Skip,
                tsumegoId = game.sgf.id,
                gameMode = args.gameMode,
                resolutionTimeMs = getElapsedTime(),
            )
            if (submitResult is THResult.Failure) {
                snackbarManager.showError(submitResult.error)
                return@launch
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
                        gameMode = args.gameMode,
                    )
                } else {
                    snackbarManager.showDone("Estimation terminé".toTextSpec()) // TODO: loco
                    _navEvent.value = RankEstimationNavEvent.Back
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
    data object Back : RankEstimationNavEvent
}
