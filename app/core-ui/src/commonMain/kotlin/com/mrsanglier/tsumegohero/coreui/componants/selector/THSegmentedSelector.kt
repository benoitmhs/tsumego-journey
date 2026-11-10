package com.mrsanglier.tsumegohero.coreui.componants.selector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStatus
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.icon.Content
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.surface
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

/**
 * Single choice selector: segments joined in one rounded bar.
 * The selected segment uses the primary button colors (or [selectedBackground] when set), the others the text button ones.
 */
@Composable
fun THSegmentedSelector(
    segments: List<THSegment>,
    selectedIndex: Int,
    onSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedBackground: Color? = null,
) {
    Row(
        modifier = modifier
            .clip(THTheme.shape.roundMedium),
        horizontalArrangement = Arrangement.spacedBy(SegmentSpacing),
    ) {
        segments.forEachIndexed { index, segment ->
            val isSelected = index == selectedIndex
            val style = if (isSelected) ButtonStyle.Primary else ButtonStyle.Text
            val customBackground = selectedBackground.takeIf { isSelected }
            val contentColor = if (customBackground != null) {
                THTheme.colors.content
            } else {
                style.contentColor(ButtonStatus.Enabled)
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = SegmentMinHeight)
                    .then(
                        if (customBackground != null) {
                            Modifier.surface(background = customBackground)
                        } else {
                            Modifier.surface(background = style.background(ButtonStatus.Enabled))
                        },
                    )
                    .thClickable(onClick = { onSelect(index) }, role = Role.Tab)
                    .padding(horizontal = THTheme.spacing.small, vertical = THTheme.spacing.xsmall),
                horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.tiny, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                segment.icon?.Content(
                    iconSize = IconSize.Small,
                    tint = contentColor,
                )
                THText(
                    text = segment.label,
                    style = THTheme.typography.label100,
                    color = contentColor,
                    maxLines = 1,
                )
            }
        }
    }
}

private val SegmentSpacing: Dp = 2.dp
private val SegmentMinHeight: Dp = 40.dp
