package com.mrsanglier.tsumegohero.problemedition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.Stone
import com.mrsanglier.tsumegohero.problemedition.step.BoardStepContent
import com.mrsanglier.tsumegohero.problemedition.step.InfoStepContent
import com.mrsanglier.tsumegohero.problemedition.step.SolutionsStepContent

/** Actions of the step contents, shared by the creation and the edition screens. */
internal class ProblemEditionActions(
    val editInitialStone: (Cell) -> Unit,
    val selectTool: (EditionTool) -> Unit,
    val selectFirstPlayer: (Stone) -> Unit,
    val playSolutionMove: (Cell) -> Unit,
    val goToPosition: (List<Move>) -> Unit,
    val goToPrevious: () -> Unit,
    val goToNext: () -> Unit,
    val toggleOutcome: (Boolean) -> Unit,
    val deleteCurrentMove: () -> Unit,
    val onTitleChange: (String) -> Unit,
    val onNotesChange: (String) -> Unit,
    val openDifficultyPicker: () -> Unit,
    val clearDifficulty: () -> Unit,
)

@Composable
internal fun rememberProblemEditionActions(viewModel: ProblemEditionViewModel): ProblemEditionActions =
    remember(viewModel) {
        ProblemEditionActions(
            editInitialStone = viewModel::editInitialStone,
            selectTool = viewModel::selectTool,
            selectFirstPlayer = viewModel::selectFirstPlayer,
            playSolutionMove = viewModel::playSolutionMove,
            goToPosition = viewModel::goToPosition,
            goToPrevious = viewModel::goToPrevious,
            goToNext = viewModel::goToNext,
            toggleOutcome = viewModel::toggleOutcome,
            deleteCurrentMove = viewModel::deleteCurrentMove,
            onTitleChange = viewModel::onTitleChange,
            onNotesChange = viewModel::onNotesChange,
            openDifficultyPicker = viewModel::openDifficultyPicker,
            clearDifficulty = { viewModel.selectDifficulty(null) },
        )
    }

/** Content of one step, displayed by the step by step flow as well as by each page of the pager. */
@Composable
internal fun EditionStepContent(
    step: EditionStep,
    uiState: ProblemEditionViewModelState,
    actions: ProblemEditionActions,
    modifier: Modifier = Modifier,
) {
    when (step) {
        EditionStep.Board -> BoardStepContent(
            uiState = uiState,
            onClickCell = actions.editInitialStone,
            onSelectTool = actions.selectTool,
            onSelectFirstPlayer = actions.selectFirstPlayer,
            modifier = modifier,
        )

        EditionStep.Solutions -> SolutionsStepContent(
            uiState = uiState,
            onClickCell = actions.playSolutionMove,
            onClickNode = actions.goToPosition,
            onPrevious = actions.goToPrevious,
            onNext = actions.goToNext,
            onToggleOutcome = actions.toggleOutcome,
            onDelete = actions.deleteCurrentMove,
            modifier = modifier,
        )

        EditionStep.Info -> InfoStepContent(
            uiState = uiState,
            onTitleChange = actions.onTitleChange,
            onNotesChange = actions.onNotesChange,
            onClickDifficulty = actions.openDifficultyPicker,
            onClearDifficulty = actions.clearDifficulty,
            modifier = modifier,
        )
    }
}
