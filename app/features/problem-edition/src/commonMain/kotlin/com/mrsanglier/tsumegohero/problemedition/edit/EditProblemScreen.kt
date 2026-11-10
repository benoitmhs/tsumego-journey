package com.mrsanglier.tsumegohero.problemedition.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.Composable
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.tab.FOTabData
import com.mrsanglier.tsumegohero.coreui.componants.tab.TabRow
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
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditProblemRoute(navScope: EditProblemNavScope) {
    val viewModel: EditProblemViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bottomSheet by viewModel.bottomSheet.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            ProblemEditionViewModel.NavEvent.Back -> navScope.navigateBack()
        }
    }

    EditProblemScreen(
        uiState = uiState,
        close = viewModel::close,
        onSelectStep = viewModel::selectStep,
        actions = rememberProblemEditionActions(viewModel),
    )

    bottomSheet?.Composable()
}

/** Every step in a pager, reachable from the tabs or by swiping, with a save button always available. */
@Composable
private fun EditProblemScreen(
    uiState: ProblemEditionViewModelState,
    close: () -> Unit,
    onSelectStep: (EditionStep) -> Unit,
    actions: ProblemEditionActions,
) {
    val steps = EditionStep.entries
    val pagerState = rememberPagerState { steps.size }
    val scrollStates = steps.map { rememberScrollState() }
    val coroutineScope = rememberCoroutineScope()
    val topBarHazeState = remember { HazeState() }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> onSelectStep(steps[page]) }
    }

    THScreen(
        topBar = {
            THTopBar(
                title = uiState.screenTitle,
                hazeState = topBarHazeState,
                elevation = 8.dp,
                navAction = TopBarAction.close(close),
                bottomContent = {
                    TabRow(
                        tabs = steps.mapIndexed { index, step ->
                            FOTabData(
                                label = step.title,
                                isSelected = pagerState.currentPage == index,
                                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            )
                        },
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
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(topBarHazeState),
            verticalAlignment = Alignment.Top,
        ) { page ->
            EditionStepContent(
                step = steps[page],
                uiState = uiState,
                actions = actions,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollStates[page])
                    .padding(innerPadding)
                    .padding(THTheme.spacing.large),
            )
        }
    }
}
