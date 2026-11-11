package com.mrsanglier.tsumegohero.collection.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_fab_import
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_fab_newCollection
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_list_empty_message
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_list_empty_title
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_list_title
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_book_open
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_book_open_filled
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_import
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_library_add
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_next
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_check
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_close
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_edit
import com.mrsanglier.tsumegohero.collection.common.EditionItemLayout
import com.mrsanglier.tsumegohero.collection.common.EmptyState
import com.mrsanglier.tsumegohero.coreui.componants.fabmenu.THFabMenu
import com.mrsanglier.tsumegohero.coreui.componants.fabmenu.rememberHideOnScrollState
import com.mrsanglier.tsumegohero.coreui.componants.fabmenu.THFabMenuItem
import com.mrsanglier.tsumegohero.coreui.componants.icon.Content
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.Composable
import com.mrsanglier.tsumegohero.coreui.componants.screen.LocalPiScreenPadding
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.rememberTopBarElevation
import com.mrsanglier.tsumegohero.coreui.extension.thCard
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.navigation.safeNavigation
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_create_new_folder
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollectionListRoute(
    navScope: CollectionListNavScope,
    viewModel: CollectionListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bottomSheet by viewModel.bottomSheet.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            is CollectionListViewModel.NavEvent.Collection -> navScope.navigateToCollection(event.collectionId)
        }
    }

    val zipPicker = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("zip")),
    ) { file ->
        file?.let(viewModel::onArchivePicked)
    }

    CollectionListScreen(
        uiState = uiState,
        onToggleEditMode = viewModel::toggleEditMode,
        fabItems = remember(viewModel, zipPicker) {
            listOf(
                THFabMenuItem(
                    label = THString.collection_fab_newCollection.toTextSpec(),
                    icon = THDrawable.ic_library_add.toIconSpec(),
                    onClick = viewModel::openNewCollection,
                ),
                THFabMenuItem(
                    label = THString.collection_fab_import.toTextSpec(),
                    icon = THDrawable.ic_import.toIconSpec(),
                    onClick = { zipPicker.launch() },
                ),
            )
        },
    )

    bottomSheet?.Composable()
}

@Composable
private fun CollectionListScreen(
    uiState: CollectionListViewModelState,
    onToggleEditMode: () -> Unit,
    fabItems: List<THFabMenuItem>,
) {
    val lazyListState = rememberLazyListState()
    val fabVisibility = rememberHideOnScrollState()
    val topBarHazeState = remember { HazeState() }
    val topBarElevation by rememberTopBarElevation(lazyListState)

    THScreen(
        modifier = Modifier.padding(bottom = LocalPiScreenPadding.current.calculateBottomPadding()),
        topBar = {
            THTopBar(
                title = THString.collection_list_title.toTextSpec(),
                hazeState = topBarHazeState,
                elevation = topBarElevation,
                trailingAction = if (uiState.collections.isNullOrEmpty()) {
                    null
                } else {
                    TopBarAction.Icon(
                        icon = (if (uiState.isEditMode) THDrawable.ic_close else THDrawable.ic_edit).toIconSpec(),
                        onClick = onToggleEditMode,
                    )
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(fabVisibility.nestedScrollConnection),
        ) {
            when {
                uiState.collections == null -> Unit
                uiState.collections.isEmpty() -> EmptyState(
                    icon = THDrawable.ic_book_open.toIconSpec(),
                    title = THString.collection_list_empty_title.toTextSpec(),
                    message = THString.collection_list_empty_message.toTextSpec(),
                    modifier = Modifier.padding(innerPadding),
                )

                else -> LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .hazeSource(topBarHazeState),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding() + THTheme.spacing.small,
                        bottom = innerPadding.calculateBottomPadding() + FabMenuContentPadding,
                        start = THTheme.spacing.large,
                        end = THTheme.spacing.large,
                    ),
                    verticalArrangement = Arrangement.spacedBy(THTheme.spacing.medium),
                ) {
                    items(
                        items = uiState.collections,
                        key = { it.id },
                        contentType = { "collection.item" },
                    ) { item ->
                        CollectionCell(item = item, isEditMode = uiState.isEditMode)
                    }
                }
            }

            THFabMenu(
                icon = THDrawable.ic_create_new_folder.toIconSpec(),
                label = THString.collection_fab_newCollection.toTextSpec(),
                items = fabItems,
                visible = fabVisibility.isVisible,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = innerPadding.calculateBottomPadding()),
            )
        }
    }
}

/** In edit mode, the collection is covered by the edition overlay with its delete button. */
@Composable
private fun CollectionCell(item: CollectionItemState, isEditMode: Boolean) {
    EditionItemLayout(
        isEditMode = isEditMode,
        onDelete = item.onDelete,
        showEditIcon = false,
        shape = THTheme.shape.roundLarge,
        deleteAlignment = Alignment.CenterEnd,
        modifier = Modifier.fillMaxWidth(),
    ) {
        CollectionCellContent(item = item, isEditMode = isEditMode)
    }
}

@Composable
private fun CollectionCellContent(item: CollectionItemState, isEditMode: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .thCard()
            .thClickable(item.onClick)
            .padding(THTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.medium),
    ) {
        THDrawable.ic_book_open_filled.toIconSpec().Content(
            iconSize = IconSize.Medium,
            tint = THTheme.colors.contentTint,
        )
        Column(modifier = Modifier.weight(1f)) {
            THText(
                text = item.name,
                style = THTheme.typography.title100,
            )
            THText(
                text = item.problemCount,
                style = THTheme.typography.content100,
                color = THTheme.colors.contentSecondary,
            )
        }
        // The delete button takes this place in edit mode
        if (!isEditMode) {
            THDrawable.ic_next.toIconSpec().Content(
                iconSize = IconSize.Small,
                tint = THTheme.colors.contentSecondary,
            )
        }
    }
}

/** Keeps the last items reachable above the FAB menu. */
internal val FabMenuContentPadding = 96.dp
