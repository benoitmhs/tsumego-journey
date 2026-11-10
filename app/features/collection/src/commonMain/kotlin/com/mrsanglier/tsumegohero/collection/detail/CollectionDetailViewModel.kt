package com.mrsanglier.tsumegohero.collection.detail

import androidx.lifecycle.SavedStateHandle
import co.touchlab.kermit.Logger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_untitledProblem
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_import_error
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_import_success
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.coreui.componants.loading.LoadingManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.THSnackbarState
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showDone
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THPlurals
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.domain.collection.model.UserTsumegoPreview
import com.mrsanglier.tsumegohero.domain.collection.usecase.ImportUserTsumegoUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.ObserveCollectionDetailUseCase
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readString

class CollectionDetailViewModel(
    private val snackbarManager: SnackbarManager,
    private val loadingManager: LoadingManager,
    private val importUserTsumegoUseCase: ImportUserTsumegoUseCase,
    observeCollectionDetailUseCase: ObserveCollectionDetailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args: CollectionDetailDestination = savedStateHandle.toRoute()

    internal val navEvent = MutableStateFlow<NavEvent?>(null)

    internal val uiState: StateFlow<CollectionDetailViewModelState> =
        observeCollectionDetailUseCase(args.collectionId)
            .onEach { detail ->
                // The collection no longer exists: nothing to show
                if (detail == null) navEvent.value = NavEvent.Back
            }
            .map { detail ->
                CollectionDetailViewModelState(
                    title = detail?.collection?.name?.toTextSpec(),
                    problems = detail?.problems?.mapIndexed { index, problem -> problem.toItemState(index) },
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                CollectionDetailViewModelState(),
            )

    internal fun addManually() {
        navEvent.value = NavEvent.TsumegoEdition(args.collectionId)
    }

    /** Imports each file as a problem, then reports successes and the files that could not be read. */
    internal fun importSgfFiles(files: List<PlatformFile>) {
        if (files.isEmpty()) return
        viewModelScope.launch {
            loadingManager.withLoading {
                val failedFiles = mutableListOf<String>()
                var importedCount = 0

                files.forEach { file ->
                    val result = try {
                        importUserTsumegoUseCase(
                            collectionId = args.collectionId,
                            fileName = file.nameWithoutExtension,
                            sgf = file.readString(),
                        )
                    } catch (e: Exception) {
                        Logger.e(e) { "Cannot read ${file.name}" }
                        null
                    }
                    if (result is THResult.Success) importedCount++ else failedFiles += file.name
                }

                if (importedCount > 0) {
                    snackbarManager.showDone(
                        THPlurals.collection_import_success.toTextSpec(importedCount, importedCount),
                    )
                }
                if (failedFiles.isNotEmpty()) {
                    snackbarManager.showSnackBar(
                        THSnackbarState.Error(
                            text = THString.collection_import_error.toTextSpec(failedFiles.joinToString()),
                        ),
                    )
                }
            }
        }
    }

    internal fun consumeNavigation() {
        navEvent.value = null
    }

    private fun UserTsumegoPreview.toItemState(index: Int): TsumegoGridItemState = TsumegoGridItemState(
        id = id,
        title = title?.toTextSpec() ?: THString.collection_detail_untitledProblem.toTextSpec(index + 1),
        difficulty = rank?.rawValue?.lowercase()?.toTextSpec(),
        boardSize = boardSize,
        boardUiState = BoardUiState(
            blackStones = blackStones,
            whiteStones = whiteStones,
            cropBoard = cropBoard,
        ),
    )

    internal sealed interface NavEvent {
        data object Back : NavEvent
        data class TsumegoEdition(val collectionId: String) : NavEvent
    }
}
