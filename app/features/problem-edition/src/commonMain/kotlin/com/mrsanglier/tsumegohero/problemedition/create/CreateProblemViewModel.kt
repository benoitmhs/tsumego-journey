package com.mrsanglier.tsumegohero.problemedition.create

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.app.coreui.resources.common_next
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_save
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_saved
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_title
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStatus
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.domain.collection.usecase.GetEditionCropBoardUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.PlayEditionMoveUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.SaveUserTsumegoUseCase
import com.mrsanglier.tsumegohero.problemedition.EditionData
import com.mrsanglier.tsumegohero.problemedition.EditionStep
import com.mrsanglier.tsumegohero.problemedition.ProblemEditionViewModel
import kotlinx.coroutines.flow.update

/** New problem, entered step by step: initial position, solutions, then information. */
internal class CreateProblemViewModel(
    playEditionMoveUseCase: PlayEditionMoveUseCase,
    getEditionCropBoardUseCase: GetEditionCropBoardUseCase,
    saveUserTsumegoUseCase: SaveUserTsumegoUseCase,
    snackbarManager: SnackbarManager,
    savedStateHandle: SavedStateHandle,
) : ProblemEditionViewModel(
    playEditionMoveUseCase = playEditionMoveUseCase,
    getEditionCropBoardUseCase = getEditionCropBoardUseCase,
    saveUserTsumegoUseCase = saveUserTsumegoUseCase,
    snackbarManager = snackbarManager,
) {

    private val args: CreateProblemDestination = savedStateHandle.toRoute()

    override fun EditionData.screenTitle(): TextSpec = THString.tsumegoEdition_title.toTextSpec()

    override fun EditionData.mainButton(): THButtonState {
        val (text, enabled) = when (step) {
            EditionStep.Board -> THString.common_next to isPositionValid
            EditionStep.Solutions -> THString.common_next to tree.hasCorrectAnswer
            EditionStep.Info -> THString.tsumegoEdition_save to true
        }
        return THButtonState(
            text = text.toTextSpec(),
            onClick = ::next,
            status = when {
                isSaving -> ButtonStatus.Loading
                enabled -> ButtonStatus.Enabled
                else -> ButtonStatus.Disabled
            },
        )
    }

    /** Back from a step returns to the previous one, the first step closes the flow. */
    internal fun back() {
        when (data.value.step) {
            EditionStep.Board -> navEvent.value = NavEvent.Back
            EditionStep.Solutions -> data.update { it.copy(step = EditionStep.Board) }
            EditionStep.Info -> data.update { it.copy(step = EditionStep.Solutions) }
        }
    }

    private fun next() {
        when (data.value.step) {
            EditionStep.Board -> data.update {
                it.copy(step = EditionStep.Solutions, currentMoves = emptyList(), currentBoard = null)
            }

            EditionStep.Solutions -> data.update { it.copy(step = EditionStep.Info) }
            EditionStep.Info -> save(
                collectionId = args.collectionId,
                tsumegoId = null,
                savedMessage = THString.tsumegoEdition_saved.toTextSpec(),
            )
        }
    }
}
