package com.mrsanglier.tsumegohero.dashboard.screens.play.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumego_icon
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.THIcon
import com.mrsanglier.tsumegohero.coreui.componants.spacer.THSpacerWeight
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.dashboardgame.model.TsumegoItem

@Composable
internal fun TsumegoItemCell(
    tsumegoItem: TsumegoItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .thClickable(onClick)
            .padding(
                vertical = THTheme.spacing.small,
                horizontal = THTheme.spacing.large,
            ),
        horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        THIcon(
            icon = THDrawable.tsumego_icon.toIconSpec(),
            iconSize = IconSize.Medium,
            tint = THTheme.colors.contentSecondary,
        )
        THText(
            text = tsumegoItem.title.toTextSpec(),
            style = THTheme.typography.label200,
        )
        THSpacerWeight()
        THText(
            text = tsumegoItem.rank.rawValue.toTextSpec(),
            style = THTheme.typography.label100,
            color = THTheme.colors.contentSecondary,
        )
    }
}

internal object TsumegoItemCell {
    fun lazyItems(
        scope: LazyListScope,
        items: List<TsumegoItem>,
        modifier: Modifier = Modifier,
        onClickItem: (String) -> Unit,
    ) {
        scope.items(
            items = items,
            key = { it.tsumegoId },
            contentType = { contentType },
        ) { item ->
            TsumegoItemCell(
                tsumegoItem = item,
                modifier = modifier,
                onClick = {
                    onClickItem(item.tsumegoId)
                }
            )
        }
    }
}

private const val contentType: String = "TsumegoItemCell"