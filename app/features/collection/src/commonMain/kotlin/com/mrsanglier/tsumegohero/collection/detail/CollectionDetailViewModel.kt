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
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_delete_confirm
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_delete_message
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_delete_title
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_deleted
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.AlertDialogAction
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.AlertDialogManager
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.common.CommonAlertDialogAction
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.common.DefaultAlertDialog
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.domain.collection.usecase.DeleteUserTsumegoUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.RenameCollectionUseCase
import kotlinx.coroutines.flow.combine
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
    private val deleteUserTsumegoUseCase: DeleteUserTsumegoUseCase,
    private val renameCollectionUseCase: RenameCollectionUseCase,
    private val alertDialogManager: AlertDialogManager,
    observeCollectionDetailUseCase: ObserveCollectionDetailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args: CollectionDetailDestination = savedStateHandle.toRoute()

    internal val navEvent = MutableStateFlow<NavEvent?>(null)

    private val isEditMode = MutableStateFlow(false)

    /** Name typed in the top bar while in edit mode */
    private val editedName = MutableStateFlow<String?>(null)
    private var collectionName: String? = null

    internal val uiState: StateFlow<CollectionDetailViewModelState> =
        combine(
            observeCollectionDetailUseCase(args.collectionId)
                .onEach { detail ->
                    // The collection no longer exists: nothing to show
                    if (detail == null) navEvent.value = NavEvent.Back
                    collectionName = detail?.collection?.name
                },
            isEditMode,
            editedName,
        ) { detail, isEditMode, editedName ->
            CollectionDetailViewModelState(
                title = detail?.collection?.name?.toTextSpec(),
                problems = detail?.problems?.mapIndexed { index, problem -> problem.toItemState(index) },
                isEditMode = isEditMode,
                editedName = editedName.takeIf { isEditMode },
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                CollectionDetailViewModelState(),
            )

    internal fun addManually() {
        navEvent.value = NavEvent.TsumegoEdition(collectionId = args.collectionId, tsumegoId = null)
    }

    /** Entering edit mode makes the name editable; leaving it saves the new name (a blank name is ignored). */
    internal fun toggleEditMode() {
        if (isEditMode.value) {
            val name = editedName.value
            isEditMode.value = false
            editedName.value = null
            if (name != null && name.trim() != collectionName) saveName(name)
        } else {
            editedName.value = collectionName.orEmpty()
            isEditMode.value = true
        }
    }

    internal fun onNameChange(name: String) {
        editedName.value = name
    }

    private fun saveName(name: String) {
        viewModelScope.launch {
            renameCollectionUseCase(collectionId = args.collectionId, name = name).handleResult(
                onSuccess = {},
                onError = snackbarManager::showError,
            )
        }
    }

    /** Opens the problem: edition flow in edit mode, game otherwise. */
    internal fun openProblem(tsumegoId: String) {
        navEvent.value = if (isEditMode.value) {
            NavEvent.TsumegoEdition(collectionId = args.collectionId, tsumegoId = tsumegoId)
        } else {
            NavEvent.Problem(collectionId = args.collectionId, tsumegoId = tsumegoId)
        }
    }

    /** Asks for confirmation, then deletes the problem. */
    internal fun deleteProblem(tsumegoId: String) {
        alertDialogManager.showDialog(
            DefaultAlertDialog(
                title = THString.collection_detail_delete_title.toTextSpec(),
                description = THString.collection_detail_delete_message.toTextSpec(),
                actions = listOf(
                    CommonAlertDialogAction.cancel(),
                    AlertDialogAction(
                        text = THString.collection_detail_delete_confirm.toTextSpec(),
                        type = AlertDialogAction.Type.Dangerous,
                        onClick = { confirmDelete(tsumegoId) },
                    ),
                ),
            ),
        )
    }

    private fun confirmDelete(tsumegoId: String) {
        viewModelScope.launch {
            deleteUserTsumegoUseCase(tsumegoId).handleResult(
                onSuccess = { snackbarManager.showDone(THString.collection_detail_deleted.toTextSpec()) },
                onError = snackbarManager::showError,
            )
        }
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
        data class TsumegoEdition(val collectionId: String, val tsumegoId: String?) : NavEvent
        data class Problem(val collectionId: String, val tsumegoId: String) : NavEvent
    }
}
