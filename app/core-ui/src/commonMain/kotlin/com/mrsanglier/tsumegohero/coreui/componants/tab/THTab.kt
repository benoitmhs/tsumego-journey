package com.mrsanglier.tsumegohero.coreui.componants.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import androidx.compose.material3.Tab
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.THIcon
import com.mrsanglier.tsumegohero.coreui.componants.spacer.THVerticalSpacer
import com.mrsanglier.tsumegohero.coreui.componants.text.THText

@Composable
fun THTab(
    text: TextSpec,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    icon: IconSpec? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val contentColor = if (enabled) THTheme.colors.content else THTheme.colors.contentDisable
    val textStyle = if (isSelected) THTheme.typography.label100Medium else THTheme.typography.label100Light

    Tab(
        selected = isSelected,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = minTabHeight)
                .padding(horizontal = THTheme.spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            icon?.let {
                THIcon(
                    icon = icon,
                    iconSize = IconSize.Small,
                    tint = contentColor,
                )
            }
            THText(
                text = text,
                color = contentColor,
                style = textStyle,
            )
            THTheme.spacing.medium.THVerticalSpacer()
        }
    }
}

@Immutable
data class FOTabData(
    val label: TextSpec,
    val isSelected: Boolean,
    val icon: IconSpec? = null,
    val enabled: Boolean = true,
    val onClick: () -> Unit,
) {
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        THTab(
            text = label,
            isSelected = isSelected,
            icon = icon,
            enabled = enabled,
            onClick = onClick,
            modifier = modifier,
        )
    }
}

private val minTabHeight: Dp = 42.dp
