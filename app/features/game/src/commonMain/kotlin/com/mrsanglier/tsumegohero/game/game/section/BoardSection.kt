package com.mrsanglier.tsumegohero.game.game.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.coreui.componants.spacer.THVerticalSpacer
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.game.composable.Board
import com.mrsanglier.tsumegohero.game.model.Cell

@Composable
fun BoardSection(
    boardState: BoardUiState,
    onClickCell: (Cell) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(THTheme.spacing.large, alignment = Alignment.Bottom),
        ) {
            THText(
                text = boardState.playerStone ?: "".toTextSpec(),
                style = THTheme.typography.title200
            )

            THText(
                text = boardState.result ?: "".toTextSpec(),
                style = THTheme.typography.title100,
            )
        }

        THTheme.spacing.small.THVerticalSpacer()

        Board(
            boardUiState = boardState,
            onClickCell = onClickCell,
        )
    }
}