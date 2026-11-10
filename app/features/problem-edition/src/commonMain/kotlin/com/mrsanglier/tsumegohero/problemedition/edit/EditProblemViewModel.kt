package com.mrsanglier.tsumegohero.problemedition.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_save
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_updated
import com.mrsanglier.tsumegohero.app.coreui.resources.userTsumegoGame_defaultTitle
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStatus
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.domain.collection.usecase.GetEditableUserTsumegoUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.GetEditionCropBoardUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.PlayEditionMoveUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.SaveUserTsumegoUseCase
import com.mrsanglier.tsumegohero.problemedition.EditionData
import com.mrsanglier.tsumegohero.problemedition.EditionStep
import com.mrsanglier.tsumegohero.problemedition.ProblemEditionViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Saved problem, edited with every step available in a pager. */
internal class EditProblemViewModel(
    playEditionMoveUseCase: PlayEditionMoveUseCase,
    getEditionCropBoardUseCase: GetEditionCropBoardUseCase,
    saveUserTsumegoUseCase: SaveUserTsumegoUseCase,
    getEditableUserTsumegoUseCase: GetEditableUserTsumegoUseCase,
    snackbarManager: SnackbarManager,
    savedStateHandle: SavedStateHandle,
) : ProblemEditionViewModel(
    playEditionMoveUseCase = playEditionMoveUseCase,
    getEditionCropBoardUseCase = getEditionCropBoardUseCase,
    saveUserTsumegoUseCase = saveUserTsumegoUseCase,
    snackbarManager = snackbarManager,
) {

    private val args: EditProblemDestination = savedStateHandle.toRoute()

    init {
        // If the problem cannot be read, the screen is closed
        viewModelScope.launch {
            getEditableUserTsumegoUseCase(args.tsumegoId).handleResult(
                onSuccess = { problem ->
                    data.update {
                        it.copy(
                            boardSize = problem.boardSize,
                            blackStones = problem.blackStones,
                            whiteStones = problem.whiteStones,
                            firstPlayer = problem.firstPlayer,
                            tree = problem.tree,
                            title = problem.title.orEmpty(),
                            savedTitle = problem.title,
                            rank = problem.rank,
                            notes = problem.notes.orEmpty(),
                        )
                    }
                },
                onError = { error ->
                    snackbarManager.showError(error)
                    navEvent.value = NavEvent.Back
                },
            )
        }
    }

    override fun EditionData.screenTitle(): TextSpec =
        savedTitle?.toTextSpec() ?: THString.userTsumegoGame_defaultTitle.toTextSpec()

    override fun EditionData.mainButton(): THButtonState = THButtonState(
        text = THString.tsumegoEdition_save.toTextSpec(),
        onClick = {
            save(
                collectionId = args.collectionId,
                tsumegoId = args.tsumegoId,
                savedMessage = THString.tsumegoEdition_updated.toTextSpec(),
            )
        },
        status = when {
            isSaving -> ButtonStatus.Loading
            isPositionValid && tree.hasCorrectAnswer -> ButtonStatus.Enabled
            else -> ButtonStatus.Disabled
        },
    )

    /** Page shown by the pager. */
    internal fun selectStep(step: EditionStep) {
        data.update { it.copy(step = step) }
    }

    internal fun close() {
        navEvent.value = NavEvent.Back
    }
}
