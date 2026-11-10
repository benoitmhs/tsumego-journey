package edition

import com.mrsanglier.tsumegohero.domain.collection.model.SolutionSequence
import com.mrsanglier.tsumegohero.domain.collection.model.SolutionTree
import com.mrsanglier.tsumegohero.game.model.Cell
import com.mrsanglier.tsumegohero.game.model.Move
import com.mrsanglier.tsumegohero.game.model.Stone
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SolutionTreeTest : FunSpec({

    val b1 = Move(Stone.BLACK, Cell(0, 0))
    val w2 = Move(Stone.WHITE, Cell(1, 1))
    val b1bis = Move(Stone.BLACK, Cell(2, 2))

    test("adding a node adds its missing parents") {
        val tree = SolutionTree().add(listOf(b1, w2))

        tree.paths shouldBe listOf(listOf(b1), listOf(b1, w2))
    }

    test("only a leaf can be marked") {
        val tree = SolutionTree().add(listOf(b1, w2))

        tree.mark(listOf(b1), isCorrect = true).outcome(listOf(b1)) shouldBe null
        tree.mark(listOf(b1, w2), isCorrect = true).outcome(listOf(b1, w2)) shouldBe true
        tree.mark(emptyList(), isCorrect = true).outcomes shouldBe emptyMap()
    }

    test("a marked node loses its outcome when it gets a child") {
        val tree = SolutionTree().add(listOf(b1)).mark(listOf(b1), isCorrect = false).add(listOf(b1, w2))

        tree.outcome(listOf(b1)) shouldBe null
    }

    test("removing a node removes its subtree and outcomes") {
        val tree = SolutionTree()
            .add(listOf(b1, w2)).mark(listOf(b1, w2), isCorrect = true)
            .add(listOf(b1bis))
            .remove(listOf(b1))

        tree.paths shouldBe listOf(listOf(b1bis))
        tree.hasCorrectAnswer shouldBe false
    }

    test("sequences are the leaves, unmarked leaves are refutations") {
        val tree = SolutionTree()
            .add(listOf(b1, w2)).mark(listOf(b1, w2), isCorrect = true)
            .add(listOf(b1bis))

        tree.toSequences() shouldBe listOf(
            SolutionSequence(moves = listOf(b1, w2), isCorrect = true),
            SolutionSequence(moves = listOf(b1bis), isCorrect = false),
        )
        tree.leadsTo(listOf(b1), isCorrect = true) shouldBe true
        tree.leadsTo(listOf(b1bis), isCorrect = true) shouldBe false
    }
})
