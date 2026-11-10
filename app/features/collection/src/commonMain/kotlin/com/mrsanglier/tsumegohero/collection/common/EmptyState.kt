package com.mrsanglier.tsumegohero.collection.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mrsanglier.tsumegohero.coreui.componants.icon.Content
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

@Composable
internal fun EmptyState(
    icon: IconSpec,
    title: TextSpec,
    message: TextSpec,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = THTheme.spacing.huge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(THTheme.spacing.medium, Alignment.CenterVertically),
    ) {
        icon.Content(
            iconSize = IconSize.Large,
            tint = THTheme.colors.contentSecondary,
        )
        THText(
            text = title,
            style = THTheme.typography.title100,
            textAlign = TextAlign.Center,
        )
        THText(
            text = message,
            style = THTheme.typography.content100,
            color = THTheme.colors.contentSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
