package com.mrsanglier.tsumegohero.game.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.coreui.componants.topbanner.TopBannerManager
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.game.game.delegate.BoardViewModelDelegate
import com.mrsanglier.tsumegohero.game.game.delegate.GameViewModelDelegate
import com.mrsanglier.tsumegohero.game.game.delegate.GameViewModelDelegateImpl
import com.mrsanglier.tsumegohero.game.model.BoardConfig
import com.mrsanglier.tsumegohero.game.model.GameOption
import com.mrsanglier.tsumegohero.game.training.composable.DailyStreakTopBannerState
import com.mrsanglier.tsumegohero.game.training.composable.ObjectiveProgressTopBannerState
import com.mrsanglier.tsumegohero.game.usecase.GetNextTsumegoIdUseCase
import com.mrsanglier.tsumegohero.game.usecase.SendGameResultData
import com.mrsanglier.tsumegohero.game.usecase.SendGameResultUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrainingViewModel(
    gameViewModelDelegateImpl: GameViewModelDelegateImpl,
    private val snackbarManager: SnackbarManager,
    private val sendGameResultUseCase: SendGameResultUseCase,
    private val getNextTsumegoIdUseCase: GetNextTsumegoIdUseCase,
    private val topBannerManager: TopBannerManager,
    savedStateHandle: SavedStateHandle,
) : GameViewModelDelegate by gameViewModelDelegateImpl,
    BoardViewModelDelegate by gameViewModelDelegateImpl,
    ViewModel() {

    private val args: TrainingDestination = savedStateHandle.toRoute()
    private val gameOption = GameOption(autoPlay = args.autoPlay, ghost = args.ghost)

    val uiState: StateFlow<TrainingViewModelState> = gameFlow.map { game ->
        if (game == null) return@map initialState()

        TrainingViewModelState(
            boardState = game.mapBoardUiState(),
            gameActionState = game.mapGameActionState(
                onClickReview = {
                    _navEvent.value = TrainingNavEvent.Review(game.sgf.id, game.boardConfig)
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

    private val _navEvent = MutableStateFlow<TrainingNavEvent?>(null)
    internal val navEvent: StateFlow<TrainingNavEvent?> = _navEvent.asStateFlow()

    init {
        viewModelScope.launch {
            startTsumego(
                tsumegoId = args.tsumegoId,
                gameOption = gameOption,
            )
        }

        viewModelScope.launch {
            initObserveGame { rawTsumego, result ->
                sendGameResultUseCase(
                    result = result,
                    tsumego = rawTsumego,
                    resolutionTimeMs = getElapsedTime(),
                    gameContext = GameContext.Training(args.trainingMode),
                ).handleResult(
                    onSuccess = { data ->
                        data.classicalPromotion?.let { promotedRank ->
                            _navEvent.value = TrainingNavEvent.Promotion(promotedRank)
                        }
                        showProgressBanners(data)
                    },
                    onError = snackbarManager::showError,
                )
            }
        }
    }

    private fun initialState(): TrainingViewModelState = TrainingViewModelState(
        gameActionState = initialGameActionState(),
        boardState = initialBoardUiState(),
    )

    private fun skip() {
        val game = gameFlow.value ?: return
        viewModelScope.launch {
            val submitResult = sendGameResultUseCase(
                result = Attempt.Result.Skip,
                tsumego = game.sgf,
                resolutionTimeMs = getElapsedTime(),
                gameContext = GameContext.Training(args.trainingMode),
            )
            when (submitResult) {
                is THResult.Failure -> {
                    snackbarManager.showError(submitResult.error)
                    return@launch
                }

                is THResult.Success -> {
                    submitResult.successData.classicalPromotion?.let { promotedRank ->
                        _navEvent.value = TrainingNavEvent.Promotion(promotedRank)
                    }
                    showProgressBanners(submitResult.successData)
                }
            }

            loadNextTsumego()
        }
    }

    private fun next() {
        viewModelScope.launch {
            loadNextTsumego()
        }
    }

    private fun showProgressBanners(data: SendGameResultData) {
        data.dailyStreakNotif?.let { streakCount ->
            topBannerManager.show(DailyStreakTopBannerState(streakCount))
        }

        data.objectiveNotif?.let { (trainingMode, results) ->
            if (results.contains(null)) {
                topBannerManager.show(
                    ObjectiveProgressTopBannerState(
                        trainingMode = trainingMode,
                        attempts = results,
                    )
                )
            } else {
                // TODO handle results complete
            }
        }
    }

    private suspend fun loadNextTsumego() {
        getNextTsumegoIdUseCase(trainingMode = args.trainingMode).handleResult(
            onSuccess = { data ->
                startTsumego(
                    tsumegoId = data,
                    gameOption = gameOption,
                )
            },
            onError = {
                snackbarManager.showError(it)
            }
        )
    }

    fun consumeNavigation() {
        _navEvent.value = null
    }
}

internal sealed interface TrainingNavEvent {
    data class Review(val tsumegoId: String, val boardConfig: BoardConfig) : TrainingNavEvent
    data class Promotion(val rank: Rank) : TrainingNavEvent
}
