package com.mrsanglier.tsumegohero.collection.detail

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.model.BoardSize

/** @property problems null while loading */
@Immutable
internal data class CollectionDetailViewModelState(
    val title: TextSpec? = null,
    val problems: List<TsumegoGridItemState>? = null,
    val isEditMode: Boolean = false,
    /** Collection name being edited in the top bar (edit mode only) */
    val editedName: String? = null,
)

@Immutable
internal data class TsumegoGridItemState(
    val id: String,
    val title: TextSpec,
    val difficulty: TextSpec?,
    val boardSize: BoardSize,
    val boardUiState: BoardUiState,
)
