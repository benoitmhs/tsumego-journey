package com.mrsanglier.tsumegohero.coreui.componants.tab

import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

/** Fixed tab row (Material [PrimaryTabRow]): the tabs share the whole width, for a few tabs only. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabRow(
    tabs: List<FOTabData>,
    modifier: Modifier = Modifier,
) {
    val selectedTabIndex = remember(tabs) {
        tabs.indexOfFirst { it.isSelected }.coerceAtLeast(0)
    }

    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        contentColor = THTheme.colors.content,
        containerColor = Color.Transparent,
        divider = { },
        modifier = modifier,
        indicator = @Composable {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = true),
                color = THTheme.colors.contentTint,
                width = Dp.Unspecified,
            )
        },
        tabs = {
            tabs.forEach {
                it.Content()
            }
        },
    )
}
