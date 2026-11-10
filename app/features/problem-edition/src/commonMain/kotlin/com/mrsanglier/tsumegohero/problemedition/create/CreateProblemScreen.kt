package com.mrsanglier.tsumegohero.problemedition.create

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
import com.mrsanglier.tsumegohero.coreui.componants.loading.BackHandler
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.Composable
import com.mrsanglier.tsumegohero.coreui.componants.progressbar.THStepProgressBar
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.navigation.safeNavigation
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.problemedition.EditionStep
import com.mrsanglier.tsumegohero.problemedition.EditionStepContent
import com.mrsanglier.tsumegohero.problemedition.ProblemEditionActions
import com.mrsanglier.tsumegohero.problemedition.ProblemEditionViewModel
import com.mrsanglier.tsumegohero.problemedition.ProblemEditionViewModelState
import com.mrsanglier.tsumegohero.problemedition.rememberProblemEditionActions
import com.mrsanglier.tsumegohero.problemedition.title
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateProblemRoute(navScope: CreateProblemNavScope) {
    val viewModel: CreateProblemViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bottomSheet by viewModel.bottomSheet.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            ProblemEditionViewModel.NavEvent.Back -> navScope.navigateBack()
        }
    }

    BackHandler(enabled = true, onBack = viewModel::back)

    CreateProblemScreen(
        uiState = uiState,
        back = viewModel::back,
        actions = rememberProblemEditionActions(viewModel),
    )

    bottomSheet?.Composable()
}

/** Step by step flow: a progress bar, the current step and a main button to go to the next one. */
@Composable
private fun CreateProblemScreen(
    uiState: ProblemEditionViewModelState,
    back: () -> Unit,
    actions: ProblemEditionActions,
) {
    val scrollState = rememberScrollState()
    val topBarHazeState = remember { HazeState() }
    val topBarElevation by rememberTopBarElevation(scrollState)

    THScreen(
        topBar = {
            THTopBar(
                title = uiState.screenTitle,
                hazeState = topBarHazeState,
                elevation = topBarElevation,
                navAction = if (uiState.step == EditionStep.Board) TopBarAction.close(back) else TopBarAction.back(back),
                bottomContent = {
                    THStepProgressBar(
                        // Completed steps: the current step is shown as just started
                        progress = uiState.step.ordinal,
                        total = EditionStep.entries.size,
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
                THText(
                    text = step.title,
                    style = THTheme.typography.header200,
                )
            }

            EditionStepContent(
                step = uiState.step,
                uiState = uiState,
                actions = actions,
            )
        }
    }
}
