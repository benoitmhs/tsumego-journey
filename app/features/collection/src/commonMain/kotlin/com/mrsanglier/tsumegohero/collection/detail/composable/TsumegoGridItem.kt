package com.mrsanglier.tsumegohero.collection.detail.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.mrsanglier.tsumegohero.collection.common.EditionItemLayout
import com.mrsanglier.tsumegohero.collection.detail.TsumegoGridItemState
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.game.composable.Board

/** A problem of the collection. The whole item is clickable; edit mode adds the [EditionItemLayout] overlay. */
@Composable
internal fun TsumegoGridItem(
    state: TsumegoGridItemState,
    isEditMode: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EditionItemLayout(
        isEditMode = isEditMode,
        onDelete = onDelete,
        modifier = modifier
            .fillMaxWidth()
            .thClickable(onClick = onClick, role = Role.Button),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(THTheme.spacing.tiny),
        ) {
            Board(
                boardUiState = state.boardUiState,
                boardSize = state.boardSize,
                onClickCell = null,
                modifier = Modifier.fillMaxWidth(),
            )
            Row (
                horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.small),
            ){
                THText(
                    text = state.title,
                    style = THTheme.typography.label100Medium,
                    maxLines = 3,
                    modifier = Modifier.weight(1f),
                )
                state.difficulty?.let { difficulty ->
                    THText(
                        text = difficulty,
                        style = THTheme.typography.label50,
                        color = THTheme.colors.contentSecondary,
                    )
                }
            }
        }
    }
}
