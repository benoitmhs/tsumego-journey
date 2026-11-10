package com.mrsanglier.tsumegohero.collection.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_empty_message
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_empty_title
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_fab_importSgf
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_fab_manualEntry
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_add
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_edit
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_import
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_puzzle
import com.mrsanglier.tsumegohero.collection.common.EmptyState
import com.mrsanglier.tsumegohero.collection.detail.composable.TsumegoGridItem
import com.mrsanglier.tsumegohero.collection.list.FabMenuContentPadding
import com.mrsanglier.tsumegohero.coreui.componants.fabmenu.THFabMenu
import com.mrsanglier.tsumegohero.coreui.componants.fabmenu.THFabMenuItem
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.navigation.safeNavigation
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_fab_addProblem
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import dev.chrisbanes.haze.HazeState
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import dev.chrisbanes.haze.hazeSource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollectionDetailRoute(
    navScope: CollectionDetailNavScope,
    viewModel: CollectionDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            CollectionDetailViewModel.NavEvent.Back -> navScope.navigateBack()
            is CollectionDetailViewModel.NavEvent.TsumegoEdition -> navScope.navigateToTsumegoEdition(event.collectionId)
        }
    }

    // No extension filter: .sgf has no reliable MIME type, so it may not be selectable on Android
    val sgfPicker = rememberFilePickerLauncher(
        type = FileKitType.File(),
        mode = FileKitMode.Multiple(),
    ) { files ->
        files?.let(viewModel::importSgfFiles)
    }

    CollectionDetailScreen(
        uiState = uiState,
        navigateBack = navScope.navigateBack,
        fabItems = remember(viewModel, sgfPicker) {
            listOf(
                THFabMenuItem(
                    label = THString.collection_detail_fab_manualEntry.toTextSpec(),
                    icon = THDrawable.ic_edit.toIconSpec(),
                    onClick = viewModel::addManually,
                ),
                THFabMenuItem(
                    label = THString.collection_detail_fab_importSgf.toTextSpec(),
                    icon = THDrawable.ic_import.toIconSpec(),
                    onClick = { sgfPicker.launch() },
                ),
            )
        },
    )
}

@Composable
private fun CollectionDetailScreen(
    uiState: CollectionDetailViewModelState,
    navigateBack: () -> Unit,
    fabItems: List<THFabMenuItem>,
) {
    val gridState = rememberLazyGridState()
    val topBarHazeState = remember { HazeState() }
    val topBarElevation by rememberTopBarElevation(gridState)

    THScreen(
        topBar = {
            THTopBar(
                title = uiState.title,
                hazeState = topBarHazeState,
                elevation = topBarElevation,
                navAction = TopBarAction.back(navigateBack),
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.problems == null -> Unit
                uiState.problems.isEmpty() -> EmptyState(
                    icon = THDrawable.ic_puzzle.toIconSpec(),
                    title = THString.collection_detail_empty_title.toTextSpec(),
                    message = THString.collection_detail_empty_message.toTextSpec(),
                    modifier = Modifier.padding(innerPadding),
                )

                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(GridColumnCount),
                    state = gridState,
                    modifier = Modifier
                        .fillMaxSize()
                        .hazeSource(topBarHazeState),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding() + THTheme.spacing.small,
                        bottom = innerPadding.calculateBottomPadding() + FabMenuContentPadding,
                        start = THTheme.spacing.large,
                        end = THTheme.spacing.large,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
                    verticalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
                ) {
                    items(
                        items = uiState.problems,
                        key = { it.id },
                        contentType = { "tsumego.item" },
                    ) { item ->
                        TsumegoGridItem(item)
                    }
                }
            }

            THFabMenu(
                icon = THDrawable.ic_add.toIconSpec(),
                label = THString.collection_detail_fab_addProblem.toTextSpec(),
                items = fabItems,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding(),
            )
        }
    }
}

private const val GridColumnCount: Int = 2
