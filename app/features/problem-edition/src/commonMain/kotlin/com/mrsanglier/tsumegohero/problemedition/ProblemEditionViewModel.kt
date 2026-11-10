package com.mrsanglier.tsumegohero.problemedition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.THModalBottomSheetState
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showDone
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.domain.collection.model.SolutionTree
import com.mrsanglier.tsumegohero.domain.collection.model.TsumegoDraft
import com.mrsanglier.tsumegohero.domain.collection.usecase.GetEditionCropBoardUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.PlayEditionMoveUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.SaveUserTsumegoUseCase
import com.mrsanglier.tsumegohero.game.game.section.BoardUiState
import com.mrsanglier.tsumegohero.game.model.Board
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.Stone
import com.mrsanglier.tsumegohero.game.model.Stone.Companion.getOpponent
import com.mrsanglier.tsumegohero.problemedition.composable.DifficultyBottomSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Logic shared by the creation and the edition of a problem: initial position, solution tree and information.
 * Subclasses decide how the steps are navigated and what the main button does.
 */
internal abstract class ProblemEditionViewModel(
    private val playEditionMoveUseCase: PlayEditionMoveUseCase,
    private val getEditionCropBoardUseCase: GetEditionCropBoardUseCase,
    private val saveUserTsumegoUseCase: SaveUserTsumegoUseCase,
    protected val snackbarManager: SnackbarManager,
) : ViewModel() {

    internal val data = MutableStateFlow(EditionData())

    internal val uiState: StateFlow<ProblemEditionViewModelState> by lazy {
        data
            .map { it.toUiState() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                data.value.toUiState(),
            )
    }

    internal val navEvent = MutableStateFlow<NavEvent?>(null)

    private val _bottomSheet = MutableStateFlow<THModalBottomSheetState?>(null)
    internal val bottomSheet: StateFlow<THModalBottomSheetState?> = _bottomSheet.asStateFlow()

    internal abstract fun EditionData.screenTitle(): TextSpec?
    internal abstract fun EditionData.mainButton(): THButtonState

    // region Board step

    internal fun selectTool(tool: EditionTool) {
        data.update { it.copy(tool = tool) }
    }

    /** Changing who plays first invalidates the recorded answers, as their colors no longer match. */
    internal fun selectFirstPlayer(stone: Stone) {
        data.update { current ->
            if (current.firstPlayer == stone) return@update current
            current.copy(firstPlayer = stone).withoutSolutions()
        }
    }

    /** Toggles the stone of the selected tool. Editing the position invalidates the recorded solutions. */
    internal fun editInitialStone(cell: Cell) {
        data.update { current ->
            var black = current.blackStones
            var white = current.whiteStones
            when (current.tool) {
                EditionTool.Black -> {
                    black = if (cell in black) black - cell else black + cell
                    white = white - cell
                }

                EditionTool.White -> {
                    white = if (cell in white) white - cell else white + cell
                    black = black - cell
                }

                EditionTool.Erase -> {
                    black = black - cell
                    white = white - cell
                }
            }
            current.copy(blackStones = black, whiteStones = white).withoutSolutions()
        }
    }

    // endregion

    // region Solutions step

    internal fun playSolutionMove(cell: Cell) {
        data.update { current ->
            val board = current.displayedBoard()
            val move = Move(stone = current.nextStone, gameMove = cell)
            val newBoard = playEditionMoveUseCase(board, move) ?: return@update current
            val path = current.currentMoves + move
            current.copy(
                tree = current.tree.add(path),
                currentMoves = path,
                currentBoard = newBoard,
            )
        }
    }

    /** Moves the board to a position of the move tree. */
    internal fun goToPosition(path: List<Move>) {
        data.update { current ->
            current.copy(currentMoves = path, currentBoard = replay(current.draft.initialBoard(), path))
        }
    }

    internal fun goToPrevious() {
        val current = data.value.currentMoves
        if (current.isNotEmpty()) goToPosition(current.dropLast(1))
    }

    internal fun goToNext() {
        val current = data.value
        current.tree.children(current.currentMoves).firstOrNull()?.let(::goToPosition)
    }

    /** Marks the current leaf as correct or wrong; tapping the active mark again removes it. */
    internal fun toggleOutcome(isCorrect: Boolean) {
        data.update { current ->
            val path = current.currentMoves
            val newOutcome = isCorrect.takeIf { current.tree.outcome(path) != isCorrect }
            val marked = current.copy(tree = current.tree.mark(path, newOutcome))
            // Once a line gets its result, start the next one from the root
            if (newOutcome != null) marked.copy(currentMoves = emptyList(), currentBoard = null) else marked
        }
    }

    /** Deletes the current move and everything played after it, then goes back to the previous move. */
    internal fun deleteCurrentMove() {
        data.update { current ->
            val path = current.currentMoves
            if (path.isEmpty()) return@update current
            val parent = path.dropLast(1)
            current.copy(
                tree = current.tree.remove(path),
                currentMoves = parent,
                currentBoard = replay(current.draft.initialBoard(), parent),
            )
        }
    }

    // endregion

    // region Info step

    internal fun onTitleChange(title: String) {
        data.update { it.copy(title = title) }
    }

    internal fun onNotesChange(notes: String) {
        data.update { it.copy(notes = notes) }
    }

    internal fun openDifficultyPicker() {
        _bottomSheet.value = DifficultyBottomSheet(
            onRankSelected = { rank ->
                selectDifficulty(rank)
                _bottomSheet.value = null
            },
            onDismiss = { _bottomSheet.value = null },
        )
    }

    internal fun selectDifficulty(rank: Rank?) {
        data.update { it.copy(rank = rank) }
    }

    // endregion

    /** Saves the problem in the collection (replacing [tsumegoId] when set), then closes the screen. */
    protected fun save(collectionId: String, tsumegoId: String?, savedMessage: TextSpec) {
        val current = data.value
        if (current.isSaving) return
        data.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            saveUserTsumegoUseCase(
                collectionId = collectionId,
                tsumegoId = tsumegoId,
                draft = current.draft,
                title = current.title,
                rank = current.rank,
                notes = current.notes,
            ).handleResult(
                onSuccess = {
                    snackbarManager.showDone(savedMessage)
                    navEvent.value = NavEvent.Back
                },
                onError = { error ->
                    data.update { it.copy(isSaving = false) }
                    snackbarManager.showError(error)
                },
            )
        }
    }

    internal fun consumeNavigation() {
        navEvent.value = null
    }

    private fun EditionData.withoutSolutions(): EditionData =
        copy(tree = SolutionTree(), currentMoves = emptyList(), currentBoard = null)

    private fun replay(initialBoard: Board, moves: List<Move>): Board =
        moves.fold(initialBoard) { board, move -> playEditionMoveUseCase(board, move) ?: board }

    private fun EditionData.toUiState(): ProblemEditionViewModelState {
        val solutionsBoard = displayedBoard()
        val (goodMoves, badMoves) = nextRecordedMoves()
        return ProblemEditionViewModelState(
            screenTitle = screenTitle(),
            step = step,
            boardSize = boardSize,
            mainButton = mainButton(),
            initialBoardUiState = BoardUiState(
                blackStones = blackStones,
                whiteStones = whiteStones,
            ),
            tool = tool,
            firstPlayer = firstPlayer,
            solutionsBoardUiState = BoardUiState(
                blackStones = solutionsBoard.blackStones,
                whiteStones = solutionsBoard.whiteStones,
                cropBoard = getEditionCropBoardUseCase(initialBoard = draft.initialBoard(), sequences = tree.paths),
            ),
            moveNumbers = moveNumbers(solutionsBoard),
            moveTree = buildMoveTree(tree, currentMoves),
            canGoPrevious = currentMoves.isNotEmpty(),
            canGoNext = tree.children(currentMoves).isNotEmpty(),
            canMark = currentMoves.isNotEmpty() && tree.isLeaf(currentMoves),
            currentOutcome = tree.outcome(currentMoves),
            canDelete = currentMoves.isNotEmpty(),
            goodMoves = goodMoves,
            badMoves = badMoves,
            title = title,
            notes = notes,
            difficulty = rank?.rawValue?.lowercase()?.toTextSpec(),
        )
    }

    /** Number of each played move, for stones still on the board (a later move on the same cell wins). */
    private fun EditionData.moveNumbers(board: Board): Map<Cell, Int> =
        currentMoves
            .mapIndexed { index, move -> move.gameMove to index + 1 }
            .toMap()
            .filterKeys { cell -> board.getStoneAt(cell) != null }

    /**
     * Next moves already entered from the current position, split between moves leading to a correct answer
     * and moves only leading to wrong ones.
     */
    private fun EditionData.nextRecordedMoves(): Pair<Set<Cell>, Set<Cell>> {
        val children = tree.children(currentMoves)
        val good = children.filter { tree.leadsTo(it, isCorrect = true) }.map { it.last().gameMove }.toSet()
        val bad = children.filter { tree.leadsTo(it, isCorrect = false) }.map { it.last().gameMove }.toSet() - good
        return good to bad
    }

    internal sealed interface NavEvent {
        data object Back : NavEvent
    }
}

/** @property currentBoard position of the solutions step; null means the initial position */
internal data class EditionData(
    val step: EditionStep = EditionStep.Board,
    val boardSize: BoardSize = BoardSize.X19,
    val tool: EditionTool = EditionTool.Black,
    val blackStones: Set<Cell> = emptySet(),
    val whiteStones: Set<Cell> = emptySet(),
    val firstPlayer: Stone = Stone.BLACK,
    val currentMoves: List<Move> = emptyList(),
    val currentBoard: Board? = null,
    val tree: SolutionTree = SolutionTree(),
    val title: String = "",
    /** Title of the edited problem as saved */
    val savedTitle: String? = null,
    val rank: Rank? = null,
    val notes: String = "",
    val isSaving: Boolean = false,
) {
    val draft: TsumegoDraft
        get() = TsumegoDraft(
            boardSize = boardSize,
            blackStones = blackStones,
            whiteStones = whiteStones,
            solutions = tree.toSequences(),
        )

    val nextStone: Stone
        get() = currentMoves.lastOrNull()?.stone?.getOpponent() ?: firstPlayer

    val isPositionValid: Boolean
        get() = blackStones.isNotEmpty() && whiteStones.isNotEmpty()

    fun displayedBoard(): Board = currentBoard ?: draft.initialBoard()
}
