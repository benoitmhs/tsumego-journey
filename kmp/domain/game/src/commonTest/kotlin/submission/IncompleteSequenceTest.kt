package submission

import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.MoveNode
import com.mrsanglier.tsumegohero.game.model.SgfNodeOutcome
import com.mrsanglier.tsumegohero.game.model.Stone
import com.mrsanglier.tsumegohero.game.model.isIncompleteSequence
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun node(
    x: Int,
    stone: Stone,
    outcome: SgfNodeOutcome = SgfNodeOutcome.NONE,
    vararg children: MoveNode,
) = MoveNode(
    children = children.toMutableList(),
    move = Move(stone = stone, gameMove = Cell(x, 0)),
    outcome = outcome,
)

class IncompleteSequenceTest : FunSpec({
    // Winning line: B0 -> W1 -> B2 (success). Losing line: B3 -> W4 (failure).
    val win = node(2, Stone.BLACK, SgfNodeOutcome.SUCCESS)
    val w1 = node(1, Stone.WHITE, SgfNodeOutcome.NONE, win)
    val b0 = node(0, Stone.BLACK, SgfNodeOutcome.NONE, w1)
    val lose = node(4, Stone.WHITE, SgfNodeOutcome.FAILURE)
    val b3 = node(3, Stone.BLACK, SgfNodeOutcome.NONE, lose)
    val offTree = node(9, Stone.BLACK, SgfNodeOutcome.FAILURE)

    test("empty sequence is not incomplete") {
        emptyList<MoveNode>().isIncompleteSequence() shouldBe false
    }

    test("prefix of a winning line is incomplete") {
        listOf(b0).isIncompleteSequence() shouldBe true
        listOf(b0, w1).isIncompleteSequence() shouldBe true
    }

    test("full winning line is not incomplete") {
        listOf(b0, w1, win).isIncompleteSequence() shouldBe false
    }

    test("move outside the tree is not incomplete") {
        listOf(offTree).isIncompleteSequence() shouldBe false
        listOf(b0, offTree).isIncompleteSequence() shouldBe false
    }

    test("prefix of a losing-only line is not incomplete") {
        listOf(b3).isIncompleteSequence() shouldBe false
    }
})
