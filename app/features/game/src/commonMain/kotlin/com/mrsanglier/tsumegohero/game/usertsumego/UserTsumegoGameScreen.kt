package com.mrsanglier.tsumegohero.game.usertsumego

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_edit
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.navigation.safeNavigation
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.game.game.section.BoardSection
import com.mrsanglier.tsumegohero.game.game.section.GameActionsSection
import com.mrsanglier.tsumegohero.game.model.Cell
import dev.chrisbanes.haze.HazeState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserTsumegoGameRoute(
    navScope: UserTsumegoGameNavScope,
    viewModel: UserTsumegoGameViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            UserTsumegoGameViewModel.NavEvent.Back -> navScope.navigateBack()
            is UserTsumegoGameViewModel.NavEvent.Review -> navScope.navigateToReview(event.tsumegoId)
            is UserTsumegoGameViewModel.NavEvent.Edition ->
                navScope.navigateToEdition(event.collectionId, event.tsumegoId)
        }
    }

    UserTsumegoGameScreen(
        uiState = uiState,
        navigateBack = navScope.navigateBack,
        onEdit = viewModel::edit,
        onClickCell = viewModel::onClickCell,
    )
}

@Composable
private fun UserTsumegoGameScreen(
    uiState: UserTsumegoGameViewModelState,
    navigateBack: () -> Unit,
    onEdit: () -> Unit,
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
                trailingAction = TopBarAction.Icon(
                    icon = THDrawable.ic_edit.toIconSpec(),
                    onClick = onEdit,
                ),
                elevation = topBarElevation,
            )
        },
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
