package com.mrsanglier.tsumegohero.collection.list

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec

/** @property collections null while loading */
@Immutable
internal data class CollectionListViewModelState(
    val collections: List<CollectionItemState>? = null,
    val isEditMode: Boolean = false,
)

@Immutable
internal data class CollectionItemState(
    val id: String,
    val name: TextSpec,
    val problemCount: TextSpec,
    /** null in edit mode: the collection can only be deleted */
    val onClick: (() -> Unit)?,
    val onDelete: () -> Unit,
)
