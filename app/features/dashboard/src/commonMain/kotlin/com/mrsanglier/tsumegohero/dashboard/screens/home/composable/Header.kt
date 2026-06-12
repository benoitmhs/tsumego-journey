package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

@Composable
fun Header(
    text: TextSpec,
    modifier: Modifier = Modifier,
) {
    THText(
        text = text,
        style = THTheme.typography.header100,
        color = THTheme.colors.content,
        modifier = modifier
            .padding(horizontal = THTheme.spacing.large)
            .padding(
                top = THTheme.spacing.xlarge,
                bottom = THTheme.spacing.tiny,
            )
    )
}
