package com.mrsanglier.tsumegohero.problemedition.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.problemedition.ProblemEditionViewModelState
import com.mrsanglier.tsumegohero.problemedition.composable.MoveTree
import com.mrsanglier.tsumegohero.problemedition.composable.SolutionsStepActions
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.game.game.composable.Board
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move

/** Step 2: zoomed board with numbered moves, the move tree and the review-like controls. */
@Composable
internal fun SolutionsStepContent(
    uiState: ProblemEditionViewModelState,
    onClickCell: (Cell) -> Unit,
    onClickNode: (List<Move>) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleOutcome: (isCorrect: Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
    ) {
        Board(
            boardUiState = uiState.solutionsBoardUiState,
            boardSize = uiState.boardSize,
            onClickCell = onClickCell,
            goodMoves = uiState.goodMoves,
            badMoves = uiState.badMoves,
            moveNumbers = uiState.moveNumbers,
            modifier = Modifier.fillMaxWidth(),
        )
        MoveTree(
            state = uiState.moveTree,
            onClickNode = onClickNode,
        )
        SolutionsStepActions(
            uiState = uiState,
            onPrevious = onPrevious,
            onNext = onNext,
            onToggleOutcome = onToggleOutcome,
            onDelete = onDelete,
        )
    }
}
