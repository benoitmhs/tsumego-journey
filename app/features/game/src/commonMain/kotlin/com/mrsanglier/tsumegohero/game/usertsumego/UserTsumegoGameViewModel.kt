package com.mrsanglier.tsumegohero.game.usertsumego

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.app.coreui.resources.userTsumegoGame_defaultTitle
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.data.model.game.TsumegoSource
import com.mrsanglier.tsumegohero.domain.collection.usecase.ObserveUserTsumegoUseCase
import com.mrsanglier.tsumegohero.game.game.delegate.BoardViewModelDelegate
import com.mrsanglier.tsumegohero.game.game.delegate.GameViewModelDelegate
import com.mrsanglier.tsumegohero.game.game.delegate.GameViewModelDelegateImpl
import com.mrsanglier.tsumegohero.game.game.delegate.defaultRestartButton
import com.mrsanglier.tsumegohero.game.game.delegate.defaultReviewButton
import com.mrsanglier.tsumegohero.game.game.section.GameActionState
import com.mrsanglier.tsumegohero.game.model.Game
import com.mrsanglier.tsumegohero.game.model.GameOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn

/**
 * Game on a problem of a user collection: the opponent answers automatically, no result is recorded,
 * and the review is available from the start. The game restarts when the problem is edited.
 */
class UserTsumegoGameViewModel(
    private val gameViewModelDelegateImpl: GameViewModelDelegateImpl,
    observeUserTsumegoUseCase: ObserveUserTsumegoUseCase,
    savedStateHandle: SavedStateHandle,
) : GameViewModelDelegate by gameViewModelDelegateImpl,
    BoardViewModelDelegate by gameViewModelDelegateImpl,
    ViewModel() {

    private val args: UserTsumegoGameDestination = savedStateHandle.toRoute()
    private val problemTitle = MutableStateFlow<String?>(null)

    internal val navEvent = MutableStateFlow<NavEvent?>(null)

    internal val uiState: StateFlow<UserTsumegoGameViewModelState> = combine(
        gameFlow,
        problemTitle,
    ) { game, title ->
        UserTsumegoGameViewModelState(
            title = title?.toTextSpec() ?: THString.userTsumegoGame_defaultTitle.toTextSpec(),
            boardState = game?.mapBoardUiState() ?: initialBoardUiState(),
            gameActionState = game?.actionState() ?: initialGameActionState(),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UserTsumegoGameViewModelState(
            title = THString.userTsumegoGame_defaultTitle.toTextSpec(),
            boardState = initialBoardUiState(),
            gameActionState = initialGameActionState(),
        ),
    )

    init {
        // Plays the opponent answers; results of user problems are not recorded
        viewModelScope.launch { initObserveGame { _, _ -> } }

        viewModelScope.launch {
            observeUserTsumegoUseCase(args.tsumegoId)
                .distinctUntilChanged()
                .collect { tsumego ->
                    if (tsumego == null) {
                        navEvent.value = NavEvent.Back
                        return@collect
                    }
                    problemTitle.value = tsumego.title
                    startTsumego(
                        tsumegoId = args.tsumegoId,
                        gameOption = GameOption(autoPlay = true, ghost = false),
                        source = TsumegoSource.UserCollection,
                    )
                }
        }
    }

    internal fun edit() {
        navEvent.value = NavEvent.Edition(collectionId = args.collectionId, tsumegoId = args.tsumegoId)
    }

    internal fun consumeNavigation() {
        navEvent.value = null
    }

    private fun Game.actionState(): GameActionState = GameActionState(
        actionButton = defaultReviewButton { navEvent.value = NavEvent.Review(args.tsumegoId) },
        skipButton = null,
        validateButton = defaultRestartButton(gameViewModelDelegateImpl::reset).copy(enabled = lastMove != null),
    )

    internal sealed interface NavEvent {
        data object Back : NavEvent
        data class Review(val tsumegoId: String) : NavEvent
        data class Edition(val collectionId: String, val tsumegoId: String) : NavEvent
    }
}
