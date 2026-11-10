package com.mrsanglier.tsumegohero.collection.edition

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_step_board_title
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_step_info_title
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_step_solutions_title
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_title
import com.mrsanglier.tsumegohero.collection.edition.step.BoardStepContent
import com.mrsanglier.tsumegohero.collection.edition.step.InfoStepContent
import com.mrsanglier.tsumegohero.collection.edition.step.SolutionsStepContent
import com.mrsanglier.tsumegohero.coreui.componants.loading.BackHandler
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.Composable
import com.mrsanglier.tsumegohero.coreui.componants.progressbar.THStepProgressBar
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.navigation.safeNavigation
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.Stone
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TsumegoEditionRoute(
    navScope: TsumegoEditionNavScope,
    viewModel: TsumegoEditionViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bottomSheet by viewModel.bottomSheet.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            TsumegoEditionViewModel.NavEvent.Back -> navScope.navigateBack()
        }
    }

    BackHandler(enabled = true, onBack = viewModel::back)

    TsumegoEditionScreen(
        uiState = uiState,
        actions = remember(viewModel) {
            TsumegoEditionActions(
                back = viewModel::back,
                onClickCell = viewModel::onClickCell,
                selectTool = viewModel::selectTool,
                selectFirstPlayer = viewModel::selectFirstPlayer,
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
        },
    )

    bottomSheet?.Composable()
}

private class TsumegoEditionActions(
    val back: () -> Unit,
    val onClickCell: (Cell) -> Unit,
    val selectTool: (EditionTool) -> Unit,
    val selectFirstPlayer: (Stone) -> Unit,
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
private fun TsumegoEditionScreen(
    uiState: TsumegoEditionViewModelState,
    actions: TsumegoEditionActions,
) {
    val scrollState = rememberScrollState()
    val topBarHazeState = remember { HazeState() }
    val topBarElevation by rememberTopBarElevation(scrollState)

    THScreen(
        topBar = {
            THTopBar(
                title = THString.tsumegoEdition_title.toTextSpec(),
                hazeState = topBarHazeState,
                elevation = topBarElevation,
                navAction = if (uiState.step == EditionStep.Board) {
                    TopBarAction.close(actions.back)
                } else {
                    TopBarAction.back(actions.back)
                },
                bottomContent = {
                    THStepProgressBar(
                        progress = uiState.stepNumber,
                        total = uiState.stepCount,
                        modifier = Modifier.padding(
                            horizontal = THTheme.spacing.large,
                            vertical = THTheme.spacing.small,
                        ),
                    )
                },
            )
        },
        bottomBar = {
            uiState.mainButton?.Content(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(THTheme.spacing.large),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .hazeSource(topBarHazeState)
                .padding(innerPadding)
                .padding(THTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
        ) {
            AnimatedContent(targetState = uiState.step) { step ->
                StepTitle(step)
            }

            when (uiState.step) {
                EditionStep.Board -> BoardStepContent(
                    uiState = uiState,
                    onClickCell = actions.onClickCell,
                    onSelectTool = actions.selectTool,
                    onSelectFirstPlayer = actions.selectFirstPlayer,
                )

                EditionStep.Solutions -> SolutionsStepContent(
                    uiState = uiState,
                    onClickCell = actions.onClickCell,
                    onClickNode = actions.goToPosition,
                    onPrevious = actions.goToPrevious,
                    onNext = actions.goToNext,
                    onToggleOutcome = actions.toggleOutcome,
                    onDelete = actions.deleteCurrentMove,
                )

                EditionStep.Info -> InfoStepContent(
                    uiState = uiState,
                    onTitleChange = actions.onTitleChange,
                    onNotesChange = actions.onNotesChange,
                    onClickDifficulty = actions.openDifficultyPicker,
                    onClearDifficulty = actions.clearDifficulty,
                )
            }
        }
    }
}

@Composable
private fun StepTitle(step: EditionStep) {
    val title = when (step) {
        EditionStep.Board -> THString.tsumegoEdition_step_board_title
        EditionStep.Solutions -> THString.tsumegoEdition_step_solutions_title
        EditionStep.Info -> THString.tsumegoEdition_step_info_title
    }
    THText(
        text = title.toTextSpec(),
        style = THTheme.typography.header200,
    )
}
