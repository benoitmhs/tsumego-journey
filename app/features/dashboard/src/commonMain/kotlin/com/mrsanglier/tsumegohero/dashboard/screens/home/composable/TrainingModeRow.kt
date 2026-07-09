package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_arrow_forward
import com.mrsanglier.tsumegohero.coreui.componants.icon.Content
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.icon.THIcon
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

@Composable
fun TrainingModeRow(
    icon: IconSpec,
    tintColor: Color,
    title: TextSpec,
    description: TextSpec,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .thClickable(onClick)
            .padding(THTheme.spacing.large),
        horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon.Content(
            tint = tintColor,
            iconSize = IconSize.Regular,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(THTheme.spacing.tiny),
        ) {
            THText(
                text = title,
                style = THTheme.typography.label200,
            )
            THText(
                text = description,
                style = THTheme.typography.content100,
            )
        }

        THIcon(
            icon = THDrawable.ic_arrow_forward.toIconSpec(),
            iconSize = IconSize.Small,
        )
    }
}