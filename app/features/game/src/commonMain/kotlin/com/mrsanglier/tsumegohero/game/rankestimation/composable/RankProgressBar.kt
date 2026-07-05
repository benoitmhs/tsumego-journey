package com.mrsanglier.tsumegohero.game.rankestimation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.coreui.componants.progressbar.THStepProgressBar
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

@Composable
internal fun RankProgressBar(
    rankText: TextSpec,
    progress: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.small),
    ) {
        THText(
            text = rankText,
            style = THTheme.typography.label100,
        )

        THStepProgressBar(
            progress = progress,
            total = total,
            modifier = Modifier.weight(1f),
        )
    }
}

@Immutable
internal data class RankProgressBarState(
    val rankText: TextSpec,
    val progress: Int,
    val total: Int,
) {
    @Composable
    fun Composable(modifier: Modifier = Modifier) {
        RankProgressBar(
            rankText = rankText,
            progress = progress,
            total = total,
            modifier = modifier,
        )
    }
}
