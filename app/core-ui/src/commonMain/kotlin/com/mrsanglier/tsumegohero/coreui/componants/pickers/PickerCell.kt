package com.mrsanglier.tsumegohero.coreui.componants.pickers

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_close
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.icon.THIcon
import com.mrsanglier.tsumegohero.coreui.componants.iconbutton.IconButtonSize
import com.mrsanglier.tsumegohero.coreui.componants.iconbutton.FOIconButton
import com.mrsanglier.tsumegohero.coreui.componants.text.THTextResponsive
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.surface
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

@Composable
fun PickerCell(
    label: TextSpec,
    leadingIcon: IconSpec,
    onClick: () -> Unit,
    onClose: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val contentColor by animateColorAsState(if (enabled) THTheme.colors.content else THTheme.colors.contentDisable)

    Row(
        modifier = modifier
            .surface(
                shape = THTheme.shape.roundMedium,
                background = THTheme.colors.surface2,
                borderWidth = THTheme.stroke.xthin,
            ),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .thClickable(
                    onClick = onClick,
                    enabled = enabled,
                )
                .padding(vertical = THTheme.spacing.small)
                .padding(
                    start = THTheme.spacing.small,
                    end = if (onClose == null) THTheme.spacing.small else THTheme.spacing.none,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.small),
        ) {
            THIcon(
                icon = leadingIcon,
                iconSize = IconSize.Small,
                tint = contentColor,
            )
            THTextResponsive(
                text = label,
                style = THTheme.typography.content100,
                color = contentColor,
                maxLines = 1,
            )
        }

        if (onClose != null && enabled) {
            FOIconButton(
                icon = THDrawable.ic_close.toIconSpec(),
                onClick = onClose,
                size = IconButtonSize.Small,
            )
        }
    }
}