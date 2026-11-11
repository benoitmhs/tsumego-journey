package com.mrsanglier.tsumegohero.collection.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_import_error
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_import_noProblem
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_import_success
import com.mrsanglier.tsumegohero.coreui.componants.loading.LoadingManager
import com.mrsanglier.tsumegohero.domain.collection.usecase.ImportCollectionUseCase
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_problemCount
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.domain.collection.usecase.DeleteCollectionUseCase
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showDone
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.common.DefaultAlertDialog
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.common.CommonAlertDialogAction
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.AlertDialogManager
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.AlertDialogAction
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_list_deleted
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_list_delete_title
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_list_delete_message
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_detail_delete_confirm
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.THModalBottomSheetState
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.THSnackbarState
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THPlurals
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.domain.collection.usecase.CreateCollectionUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.ObserveCollectionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CollectionListViewModel(
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val importCollectionUseCase: ImportCollectionUseCase,
    private val loadingManager: LoadingManager,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val alertDialogManager: AlertDialogManager,
    private val snackbarManager: SnackbarManager,
    observeCollectionsUseCase: ObserveCollectionsUseCase,
) : ViewModel() {

    private val isEditMode = MutableStateFlow(false)

    internal val uiState: StateFlow<CollectionListViewModelState> =
        combine(observeCollectionsUseCase(), isEditMode) { summaries, isEditMode ->
            CollectionListViewModelState(
                isEditMode = isEditMode,
                collections = summaries.map { summary ->
                    CollectionItemState(
                        id = summary.collection.id,
                        name = summary.collection.name.toTextSpec(),
                        problemCount = THPlurals.collection_problemCount.toTextSpec(
                            summary.problemCount,
                            summary.problemCount,
                        ),
                        onClick = {
                            navEvent.value = NavEvent.Collection(summary.collection.id, summary.collection.name)
                        }.takeUnless { isEditMode },
                        onDelete = { deleteCollection(summary.collection.id) },
                    )
                },
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            CollectionListViewModelState(),
        )

    internal val navEvent = MutableStateFlow<NavEvent?>(null)

    private val _bottomSheet = MutableStateFlow<THModalBottomSheetState?>(null)
    internal val bottomSheet: StateFlow<THModalBottomSheetState?> = _bottomSheet.asStateFlow()

    internal fun openNewCollection() {
        _bottomSheet.value = NewCollectionBottomSheet(
            onCreate = ::createCollection,
            onDismiss = { _bottomSheet.value = null },
        )
    }

    internal fun toggleEditMode() {
        isEditMode.update { !it }
    }

    /** Asks for confirmation, then deletes the collection and its problems. */
    private fun deleteCollection(collectionId: String) {
        alertDialogManager.showDialog(
            DefaultAlertDialog(
                title = THString.collection_list_delete_title.toTextSpec(),
                description = THString.collection_list_delete_message.toTextSpec(),
                actions = listOf(
                    CommonAlertDialogAction.cancel(),
                    AlertDialogAction(
                        text = THString.collection_detail_delete_confirm.toTextSpec(),
                        type = AlertDialogAction.Type.Dangerous,
                        onClick = {
                            viewModelScope.launch {
                                deleteCollectionUseCase(collectionId).handleResult(
                                    onSuccess = {
                                        snackbarManager.showDone(THString.collection_list_deleted.toTextSpec())
                                    },
                                    onError = snackbarManager::showError,
                                )
                            }
                        },
                    ),
                ),
            ),
        )
    }

    /** Asks for the name of the collection, prefilled with the archive name, then imports it. */
    internal fun onArchivePicked(file: PlatformFile) {
        _bottomSheet.value = ImportCollectionBottomSheet(
            initialName = file.nameWithoutExtension,
            onImport = { name ->
                _bottomSheet.value = null
                importCollection(file, name)
            },
            onDismiss = { _bottomSheet.value = null },
        )
    }

    /** Creates the collection [name], with one problem per readable .sgf file of the archive, then opens it. */
    private fun importCollection(file: PlatformFile, name: String) {
        viewModelScope.launch {
            loadingManager.withLoading {
                val bytes = try {
                    file.readBytes()
                } catch (e: Exception) {
                    Logger.e(e) { "Cannot read ${file.name}" }
                    null
                }
                if (bytes == null) {
                    showImportError(listOf(file.name))
                    return@withLoading
                }
                importCollectionUseCase(name = name, zipBytes = bytes).handleResult(
                    onSuccess = { result ->
                        val collectionId = result.collectionId
                        if (collectionId == null) {
                            snackbarManager.showSnackBar(
                                THSnackbarState.Error(text = THString.collection_import_noProblem.toTextSpec()),
                            )
                            return@handleResult
                        }
                        snackbarManager.showDone(
                            THPlurals.collection_import_success.toTextSpec(result.importedCount, result.importedCount),
                        )
                        if (result.failedFiles.isNotEmpty()) showImportError(result.failedFiles)
                        navEvent.value = NavEvent.Collection(collectionId, name.trim())
                    },
                    onError = snackbarManager::showError,
                )
            }
        }
    }

    private fun showImportError(fileNames: List<String>) {
        snackbarManager.showSnackBar(
            THSnackbarState.Error(text = THString.collection_import_error.toTextSpec(fileNames.joinToString())),
        )
    }

    private fun createCollection(name: String) {
        _bottomSheet.value = null
        viewModelScope.launch {
            createCollectionUseCase(name).handleResult(
                onSuccess = { collectionId -> navEvent.value = NavEvent.Collection(collectionId, name.trim()) },
                onError = snackbarManager::showError,
            )
        }
    }

    internal fun consumeNavigation() {
        navEvent.value = null
    }

    internal sealed interface NavEvent {
        data class Collection(val collectionId: String, val collectionName: String) : NavEvent
    }
}
