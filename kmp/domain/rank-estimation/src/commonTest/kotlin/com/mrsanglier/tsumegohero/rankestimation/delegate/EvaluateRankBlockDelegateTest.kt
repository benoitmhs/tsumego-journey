package com.mrsanglier.tsumegohero.rankestimation.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

private fun attempt(timeMs: Long, result: Attempt.Result = Attempt.Result.Success) =
    Attempt(
        id = "id",
        userId = "user",
        tsumegoId = "tsumego",
        result = result,
        mode = GameMode.Standard,
        date = Instant.fromEpochMilliseconds(0),
        resolutionTimeMs = timeMs,
    )

class EvaluateRankBlockDelegateTest : FunSpec({
    val evaluate = EvaluateRankBlockDelegate()

    test("5/5 solved within flash time passes all three tiers") {
        val attempts = List(5) { attempt(10_000) }

        val result = evaluate(attempts)

        result shouldBe RankBlockEvaluation(flashPassed = true, classicalPassed = true, difficultPassed = true)
    }

    test("exactly 4/5 (80%) within flash time still passes flash") {
        val attempts = List(4) { attempt(15_000) } + attempt(15_000, result = Attempt.Result.Failure)

        val result = evaluate(attempts)

        result shouldBe RankBlockEvaluation(flashPassed = true, classicalPassed = true, difficultPassed = true)
    }

    test("3/5 (60%) fails all tiers even if all correct") {
        val attempts = List(3) { attempt(10_000) } + List(2) { attempt(10_000, result = Attempt.Result.Failure) }

        val result = evaluate(attempts)

        result shouldBe RankBlockEvaluation(flashPassed = false, classicalPassed = false, difficultPassed = false)
    }

    test("times spread across tiers only pass the matching cumulative tiers") {
        // 2 flash, 2 more classical-only, 1 more difficult-only => 2/5 flash, 4/5 classical, 5/5 difficult
        val attempts = listOf(
            attempt(10_000),
            attempt(15_000),
            attempt(90_000),
            attempt(110_000),
            attempt(300_000),
        )

        val result = evaluate(attempts)

        result shouldBe RankBlockEvaluation(flashPassed = false, classicalPassed = true, difficultPassed = true)
    }

    test("skip is treated as failure regardless of elapsed time") {
        val attempts = List(4) { attempt(10_000) } + attempt(timeMs = 5_000, result = Attempt.Result.Skip)

        val result = evaluate(attempts)

        result shouldBe RankBlockEvaluation(flashPassed = true, classicalPassed = true, difficultPassed = true)
    }

    test("only 3/5 solved within difficult time fails every tier") {
        val attempts = List(3) { attempt(400_000) } + List(2) { attempt(400_000, result = Attempt.Result.Failure) }

        val result = evaluate(attempts)

        result shouldBe RankBlockEvaluation(flashPassed = false, classicalPassed = false, difficultPassed = false)
    }
})
