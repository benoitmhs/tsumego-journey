package com.mrsanglier.tsumegohero.problemedition.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_firstPlayer_title
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_tools_title
import com.mrsanglier.tsumegohero.problemedition.EditionTool
import com.mrsanglier.tsumegohero.problemedition.ProblemEditionViewModelState
import com.mrsanglier.tsumegohero.problemedition.composable.EditionSectionHeader
import com.mrsanglier.tsumegohero.problemedition.composable.FirstPlayerSelector
import com.mrsanglier.tsumegohero.problemedition.composable.StoneToolSelector
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.game.composable.Board
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Stone

/** Step 1: stone tools, the board to place the initial stones, and who plays first. */
@Composable
internal fun BoardStepContent(
    uiState: ProblemEditionViewModelState,
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
            boardUiState = uiState.initialBoardUiState,
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
