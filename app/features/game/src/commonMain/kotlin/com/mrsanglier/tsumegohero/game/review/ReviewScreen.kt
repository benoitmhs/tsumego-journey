package com.mrsanglier.tsumegohero.game.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.button.THButton
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.spacer.THSpacerWeight
import com.mrsanglier.tsumegohero.coreui.componants.spacer.THVerticalSpacer
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.game.composable.Board
import com.mrsanglier.tsumegohero.game.model.Cell
import dev.chrisbanes.haze.HazeState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReviewRoute(
    navScope: ReviewNavScope,
    viewModel: ReviewViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReviewScreen(
        uiState = uiState,
        navigateBack = navScope.navigateBack,
        onClickCell = viewModel::onClickCell,
    )
}

@Composable
private fun ReviewScreen(
    uiState: ReviewViewModelState,
    navigateBack: () -> Unit,
    onClickCell: (Cell) -> Unit,
) {
    val scrollState = rememberScrollState()
    val topBarHazeState = remember { HazeState() }
    val topBarElevation by rememberTopBarElevation(scrollState)

    THScreen(
        topBar = {
            THTopBar(
                title = "Review".toTextSpec(), // TODO: loco
                hazeState = topBarHazeState,
                navAction = TopBarAction.close(navigateBack),
                elevation = topBarElevation,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                THSpacerWeight()

                Board(
                    boardUiState = uiState.boardState,
                    onClickCell = onClickCell,
                    goodMoves = uiState.goodMoves,
                    badMoves = uiState.badMoves,
                    isReview = true,
                )

                THTheme.spacing.xlarge.THVerticalSpacer()

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = THTheme.spacing.large,
                        alignment = Alignment.CenterHorizontally
                    ),
                ) {
                    uiState.previousButton?.Content()
                    uiState.resetButton?.Content()
                    uiState.nextButton?.Content()
                }
            }

            Row(
                modifier = Modifier.padding(THTheme.spacing.large),
            ) {
                THButton(
                    text = "Exit Review".toTextSpec(), // TODO: loco
                    style = ButtonStyle.Secondary,
                    onClick = navigateBack,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
