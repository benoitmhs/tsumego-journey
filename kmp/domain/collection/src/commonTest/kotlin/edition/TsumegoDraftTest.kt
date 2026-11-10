package edition

import com.mrsanglier.tsumegohero.domain.collection.model.TsumegoDraft
import com.mrsanglier.tsumegohero.domain.collection.usecase.GetEditionCropBoardUseCase
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegateImpl
import com.mrsanglier.tsumegohero.game.model.BoardSize
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Corner
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.Stone
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TsumegoDraftTest : FunSpec({

    val draft = TsumegoDraft(
        boardSize = BoardSize.X19,
        blackStones = setOf(Cell(1, 0), Cell(2, 1)),
        whiteStones = setOf(Cell(0, 1), Cell(3, 2)),
    )

    test("initial board contains the draft stones") {
        val board = draft.initialBoard()

        board.blackStones shouldBe draft.blackStones
        board.whiteStones shouldBe draft.whiteStones
    }

    test("edition zoom fits the stones in their corner, like in a game") {
        val crop = GetEditionCropBoardUseCase(GetCropBoardDelegateImpl())(
            initialBoard = draft.initialBoard(),
            sequences = listOf(listOf(Move(Stone.BLACK, Cell(4, 0)))),
        )

        crop?.corner shouldBe Corner.TopLeft
        crop?.boardSection?.xMax shouldBe 4
        crop?.boardSection?.yMax shouldBe 2
    }
})
