package sort

import com.mrsanglier.tsumegohero.domain.collection.utils.NaturalOrderComparator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NaturalOrderComparatorTest : FunSpec({

    fun sort(vararg names: String) = names.sortedWith(NaturalOrderComparator)

    test("numbers are compared by value") {
        sort("p12", "p8", "p1", "p100") shouldBe listOf("p1", "p8", "p12", "p100")
    }

    test("case is ignored") {
        sort("p10", "P2", "a") shouldBe listOf("a", "P2", "p10")
    }

    test("text before numbers and shorter names first") {
        sort("b2", "a", "a1", "a10b", "a10") shouldBe listOf("a", "a1", "a10", "a10b", "b2")
    }

    test("leading zeros keep a stable order") {
        sort("p8", "p08", "p9") shouldBe listOf("p08", "p8", "p9")
    }

    test("folders are sorted like the files inside") {
        sort("level 10/p1.sgf", "level 2/p1.sgf") shouldBe listOf("level 2/p1.sgf", "level 10/p1.sgf")
    }

    test("very long numbers") {
        sort("p123456789012345678901", "p99") shouldBe listOf("p99", "p123456789012345678901")
    }
})
