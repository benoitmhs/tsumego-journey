package com.mrsanglier.tsumegohero.game.training

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.navigation.safeNavigation
import com.mrsanglier.tsumegohero.game.game.section.BoardSection
import com.mrsanglier.tsumegohero.game.game.section.GameActionsSection
import com.mrsanglier.tsumegohero.game.model.Cell
import dev.chrisbanes.haze.HazeState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TrainingRoute(
    navScope: TrainingNavScope,
    viewModel: TrainingViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            is TrainingNavEvent.Review -> navScope.navigateToReview(event.tsumegoId, event.boardConfig)
        }
    }
    TrainingScreen(
        uiState = uiState,
        navigateBack = navScope.navigateBack,
        onClickCell = viewModel::onClickCell,
    )
}

@Composable
private fun TrainingScreen(
    uiState: TrainingViewModelState,
    navigateBack: () -> Unit,
    onClickCell: (Cell) -> Unit,
) {
    val scrollState = rememberScrollState()
    val topBarHazeState = remember { HazeState() }
    val topBarElevation by rememberTopBarElevation(scrollState)

    THScreen(
        topBar = {
            THTopBar(
                title = uiState.boardState.title,
                hazeState = topBarHazeState,
                navAction = TopBarAction.back(navigateBack),
                elevation = topBarElevation,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            BoardSection(
                boardState = uiState.boardState,
                modifier = Modifier.weight(1f),
                onClickCell = onClickCell,
            )

            GameActionsSection(uiState.gameActionState)
        }
    }
}
