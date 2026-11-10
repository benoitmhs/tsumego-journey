package com.mrsanglier.tsumegohero.collection.edition.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_firstPlayer_title
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_tools_title
import com.mrsanglier.tsumegohero.collection.edition.EditionTool
import com.mrsanglier.tsumegohero.collection.edition.TsumegoEditionViewModelState
import com.mrsanglier.tsumegohero.collection.edition.composable.EditionSectionHeader
import com.mrsanglier.tsumegohero.collection.edition.composable.FirstPlayerSelector
import com.mrsanglier.tsumegohero.collection.edition.composable.StoneToolSelector
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.game.composable.Board
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Stone

/** Step 1: stone tools, the board to place the initial stones, and who plays first. */
@Composable
internal fun BoardStepContent(
    uiState: TsumegoEditionViewModelState,
    onClickCell: (Cell) -> Unit,
    onSelectTool: (EditionTool) -> Unit,
    onSelectFirstPlayer: (Stone) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(THTheme.spacing.small)) {
            EditionSectionHeader(THString.tsumegoEdition_tools_title.toTextSpec())
            StoneToolSelector(
                selectedTool = uiState.tool,
                onSelectTool = onSelectTool,
            )
        }

        Board(
            boardUiState = uiState.boardUiState,
            boardSize = uiState.boardSize,
            onClickCell = onClickCell,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = THTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(THTheme.spacing.small),
        ) {
            EditionSectionHeader(THString.tsumegoEdition_firstPlayer_title.toTextSpec())
            FirstPlayerSelector(
                firstPlayer = uiState.firstPlayer,
                onSelectFirstPlayer = onSelectFirstPlayer,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}
