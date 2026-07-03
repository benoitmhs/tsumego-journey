package com.mrsanglier.tsumegohero.game.game

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
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
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_arrow_back
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.button.THButton
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.spacer.THVerticalSpacer
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.navigation.safeNavigation
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.game.composable.Board
import com.mrsanglier.tsumegohero.game.model.Cell
import dev.chrisbanes.haze.HazeState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameRoute(
    navScope: GameNavScope,
    viewModel: GameViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            GameViewModel.NavEvent.Finished -> navScope.navigateBack()
        }
    }

    GameScreen(
        uiState = uiState,
        navigateBack = navScope.navigateBack,
        onClickCell = viewModel::onClickCell,
    )
}

@Composable
private fun GameScreen(
    uiState: GameViewModelState,
    navigateBack: () -> Unit,
    onClickCell: (Cell) -> Unit,
) {
    val scrollState = rememberScrollState()
    val topBarHazeState = remember { HazeState() }
    val topBarElevation by rememberTopBarElevation(scrollState)

    THScreen(
        topBar = {
            THTopBar(
                title = uiState.title,
                hazeState = topBarHazeState,
                navAction = TopBarAction.back(navigateBack),
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
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(THTheme.spacing.large, alignment = Alignment.Bottom),
                ) {
                    THText(
                        text = uiState.playerStone ?: "".toTextSpec(),
                        style = THTheme.typography.title200
                    )

                    THText(
                        text = uiState.result ?: "".toTextSpec(),
                        style = THTheme.typography.title100,
                    )
                }

                THTheme.spacing.small.THVerticalSpacer()

                Column(
                    verticalArrangement = Arrangement.spacedBy(THTheme.spacing.xsmall),
                ) {
                    Board(
                        whiteStones = uiState.whiteStones,
                        blackStones = uiState.blackStones,
                        cropBoard = uiState.cropBoard,
                        onClickCell = onClickCell,
                        lastMove = uiState.lastMove,
                        goodMoves = uiState.goodMoves,
                        badMoves = uiState.badMoves,
                        isGhostMode = uiState.isGhostMode,
                        isReview = uiState.isReview,
                        modifier = Modifier
                            .border(
                                width = THTheme.stroke.regular,
                                color = uiState.borderColor(),
                                shape = THTheme.shape.roundSmall,
                            )
                    )
                    THText(
                        modifier = Modifier.padding(horizontal = THTheme.spacing.small),
                        text = (if (uiState.isReview) "Review 📖" else "").toTextSpec(),
                        style = THTheme.typography.label100,
                    )
                }

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
                    uiState.reviewPreviousButton?.Content()
                    uiState.reviewResetButton?.Content()
                    uiState.reviewButton?.Content()
                    uiState.reviewNextButton?.Content()
                }
            }

            Row(
                modifier = Modifier.padding(THTheme.spacing.large),
                horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
            ) {

                uiState.resetButton.Content(
                    modifier = Modifier
                        .weight(1f)
                )

                uiState.skipButton?.Content(
                    modifier = Modifier.animateContentSize()
                )

                if (uiState.submitButton != null) {
                    uiState.submitButton.Content(
                        modifier = Modifier.animateContentSize()
                    )
                } else {
                    uiState.nextButton.Content(
                        modifier = Modifier.animateContentSize()
                    )
                }
            }
        }
    }
}
