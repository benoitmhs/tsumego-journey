package com.mrsanglier.tsumegohero.rankestimation.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.user.Level
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

private fun attempt(timeMs: Long, result: Attempt.Result) =
    Attempt(
        id = "id",
        userId = "user",
        tsumegoId = "tsumego",
        result = result,
        mode = GameMode.Standard,
        date = Instant.fromEpochMilliseconds(0),
        context = GameContext.RankEstimation,
        resolutionTimeMs = timeMs,
    )

private fun block(rank: Rank, timeMs: Long, successCount: Int = 5): List<Pair<Rank, Attempt>> =
    List(successCount) { rank to attempt(timeMs, Attempt.Result.Success) } +
        List(5 - successCount) { rank to attempt(timeMs, Attempt.Result.Failure) }

class ComputeFinalLevelDelegateTest : FunSpec({
    val compute = ComputeFinalLevelDelegate(EvaluateRankBlockDelegate())

    test("first rank failing the difficult threshold yields no level") {
        val attempts = block(Rank.`15K`, timeMs = 400_000, successCount = 3)

        compute(attempts) shouldBe null
    }

    test("tiers advance independently and stop at the last validated rank") {
        val attempts =
            block(Rank.`15K`, timeMs = 10_000) + // flash+classical+difficult
                block(Rank.`15Kplus`, timeMs = 90_000) + // classical+difficult only
                block(Rank.`14K`, timeMs = 400_000, successCount = 3) // fails difficult, stops here

        compute(attempts) shouldBe Level(
            flashRank = Rank.`15K`,
            classicalRank = Rank.`15Kplus`,
            difficultRank = Rank.`15Kplus`,
        )
    }

    test("tiers never validated fall back to the weakest rank") {
        val attempts =
            block(Rank.`15K`, timeMs = 400_000) + // difficult only
                block(Rank.`15Kplus`, timeMs = 400_000) + // difficult only
                block(Rank.`14K`, timeMs = 400_000, successCount = 3) // fails difficult, stops here

        compute(attempts) shouldBe Level(
            flashRank = Rank.entries.first(),
            classicalRank = Rank.entries.first(),
            difficultRank = Rank.`15Kplus`,
        )
    }

    test("an incomplete current block is ignored") {
        val attempts =
            block(Rank.`15K`, timeMs = 10_000) +
                List(3) { Rank.`15Kplus` to attempt(10_000, Attempt.Result.Success) }

        compute(attempts) shouldBe Level(
            flashRank = Rank.`15K`,
            classicalRank = Rank.`15K`,
            difficultRank = Rank.`15K`,
        )
    }
})
