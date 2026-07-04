package com.mrsanglier.tsumegohero.game.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.game.game.delegate.BoardViewModelDelegate
import com.mrsanglier.tsumegohero.game.game.delegate.BoardViewModelDelegateImpl
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.SgfNodeOutcome
import com.mrsanglier.tsumegohero.game.usecase.NavigateReviewUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayReviewMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.StartReviewUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReviewViewModel(
    boardViewModelDelegateImpl: BoardViewModelDelegateImpl,
    private val startReviewUseCase: StartReviewUseCase,
    private val navigateReviewUseCase: NavigateReviewUseCase,
    private val playReviewMoveUseCase: PlayReviewMoveUseCase,
    private val snackbarManager: SnackbarManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    BoardViewModelDelegate by boardViewModelDelegateImpl {

    private val args: ReviewDestination = savedStateHandle.toRoute()

    init {
        viewModelScope.launch {
            loadTsumego(args.tsumegoId, GameMode.Standard).handleResult(
                onSuccess = { game -> updateGame(startReviewUseCase(game)) },
                onError = snackbarManager::showError,
            )
        }
    }

    val uiState: StateFlow<ReviewViewModelState> = gameFlow.map { game ->
        if (game == null) return@map ReviewViewModelState()

        val (goodMoves, badMoves) = game.reviewNextMove
            ?.partition { it.outcome == SgfNodeOutcome.SUCCESS }
            ?: (null to null)

        ReviewViewModelState(
            boardState = game.mapBoardUiState(),
            goodMoves = goodMoves?.map { it.move.gameMove }?.toSet(),
            badMoves = badMoves?.map { it.move.gameMove }?.toSet(),
            previousButton = defaultReviewPreviousButton.copy(enabled = game.reviewIndex > 0),
            nextButton = defaultReviewNextButton
                .copy(enabled = game.reviewIndex < (game.moveStack.size - 1) || game.nextGoodMove.isNotEmpty()),
            resetButton = defaultReviewResetButton.copy(enabled = game.moveStack.isNotEmpty()),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReviewViewModelState())

    fun onClickCell(cell: Cell) {
        val game = gameFlow.value ?: return
        playReviewMoveUseCase(cell = cell, game = game)?.let(::updateGame)
    }

    internal fun navigate(isBack: Boolean) {
        val game = gameFlow.value ?: return
        navigateReviewUseCase(game = game, isBack = isBack)?.let(::updateGame)
    }

    internal fun restart() {
        gameFlow.value?.let { updateGame(startReviewUseCase(it)) }
    }
}
