package com.mrsanglier.tsumegohero.collection.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_delete_filled
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_edit
import com.mrsanglier.tsumegohero.coreui.componants.icon.Content
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

/**
 * Shows [content] as is, and in edit mode covers it with a light focus overlay and a delete button.
 *
 * @param showEditIcon shows a pencil in the middle (decorative: the click belongs to the item)
 * @param shape shape of the overlay, matching the item
 * @param deleteAlignment position of the delete button
 */
@Composable
internal fun EditionItemLayout(
    isEditMode: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    showEditIcon: Boolean = true,
    shape: Shape = THTheme.shape.roundSmall,
    deleteAlignment: Alignment = Alignment.BottomEnd,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        if (isEditMode) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(color = THTheme.colors.surfaceFocusLight),
            ) {
                if (showEditIcon) {
                    THDrawable.ic_edit.toIconSpec().Content(
                        iconSize = IconSize.Medium,
                        tint = THTheme.colors.content,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                THDrawable.ic_delete_filled.toIconSpec().Content(
                    iconSize = IconSize.Regular,
                    tint = THTheme.colors.contentOnCritical,
                    modifier = Modifier
                        .align(deleteAlignment)
                        .padding(THTheme.spacing.tiny)
                        .clip(THTheme.shape.circle)
                        .background(THTheme.colors.surfaceCritical)
                        .thClickable(onClick = onDelete, role = Role.Button)
                        .padding(THTheme.spacing.xsmall),
                )
            }
        }
    }
}
