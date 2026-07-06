package com.mrsanglier.tsumegohero.game.rankestimation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.navigation.safeNavigation
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.game.section.BoardSection
import com.mrsanglier.tsumegohero.game.game.section.GameActionsSection
import com.mrsanglier.tsumegohero.game.model.Cell
import dev.chrisbanes.haze.HazeState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RankEstimationRoute(
    navScope: RankEstimationNavScope,
    viewModel: RankEstimationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            is RankEstimationNavEvent.Review -> navScope.navigateToReview(event.tsumegoId, event.boardConfig)
            is RankEstimationNavEvent.Result -> navScope.navigateToResult(event.level)
            is RankEstimationNavEvent.Back -> navScope.navigateBack()
        }
    }
    RankEstimationScreen(
        uiState = uiState,
        navigateBack = navScope.navigateBack,
        onClickCell = viewModel::onClickCell,
    )
}

@Composable
private fun RankEstimationScreen(
    uiState: RankEstimationViewModelState,
    navigateBack: () -> Unit,
    onClickCell: (Cell) -> Unit,
) {
    val scrollState = rememberScrollState()
    val topBarHazeState = remember { HazeState() }
    val topBarElevation by rememberTopBarElevation(scrollState)

    val animatedProgress by animateFloatAsState(uiState.progress)

    THScreen(
        topBar = {
            THTopBar(
                title = "Estimation du niveau".toTextSpec(),
                hazeState = topBarHazeState,
                navAction = TopBarAction.back(navigateBack),
                elevation = topBarElevation,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            LinearProgressIndicator(
                color = THTheme.colors.contentTint,
                trackColor = THTheme.colors.surface2,
                strokeCap = StrokeCap.Round,
                progress = { animatedProgress },
                gapSize = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = THTheme.spacing.large)
                    .height(THTheme.spacing.small),
                drawStopIndicator = {},
            )


            BoardSection(
                boardState = uiState.boardState,
                onClickCell = onClickCell,
                modifier = Modifier.weight(1f),
            )

            GameActionsSection(uiState.gameActionState)
        }
    }
}