package com.mrsanglier.tsumegohero.collection.detail.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.collection.detail.TsumegoGridItemState
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.game.composable.Board

@Composable
internal fun TsumegoGridItem(
    state: TsumegoGridItemState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(THTheme.spacing.tiny),
    ) {
        Board(
            boardUiState = state.boardUiState,
            boardSize = state.boardSize,
            onClickCell = null,
            modifier = Modifier.fillMaxWidth(),
        )
        THText(
            text = state.title,
            style = THTheme.typography.label100Medium,
            maxLines = 1,
        )
        state.difficulty?.let { difficulty ->
            THText(
                text = difficulty,
                style = THTheme.typography.label50,
                color = THTheme.colors.contentSecondary,
            )
        }
    }
}
