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
    val delegate = EstimateLevelDelegateImpl()

    test("no attempts falls back to the weakest rank on every tier") {
        delegate.estimateLevel(emptyList()) shouldBe Level(
            flashRank = Rank.entries.first(),
            classicalRank = Rank.entries.first(),
            difficultRank = Rank.entries.first(),
        )
    }

    test("fast and successful attempts validate all three tiers for that rank") {
        val attempts = block(Rank.`15K`, timeMs = 10_000)

        delegate.estimateLevel(attempts) shouldBe Level(
            flashRank = Rank.`15K`,
            classicalRank = Rank.`15K`,
            difficultRank = Rank.`15K`,
        )
    }

    test("slow but successful attempts only validate the difficult tier") {
        val attempts = block(Rank.`15K`, timeMs = 400_000)

        delegate.estimateLevel(attempts) shouldBe Level(
            flashRank = Rank.entries.first(),
            classicalRank = Rank.entries.first(),
            difficultRank = Rank.`15K`,
        )
    }

    test("a rank below the success threshold does not validate any tier") {
        val attempts = block(Rank.`15K`, timeMs = 10_000, successCount = 3)

        delegate.estimateLevel(attempts) shouldBe Level(
            flashRank = Rank.entries.first(),
            classicalRank = Rank.entries.first(),
            difficultRank = Rank.entries.first(),
        )
    }

    test("among several qualifying ranks the strongest one wins per tier") {
        val attempts = block(Rank.`15K`, timeMs = 10_000) + block(Rank.`10K`, timeMs = 10_000)

        delegate.estimateLevel(attempts) shouldBe Level(
            flashRank = Rank.`10K`,
            classicalRank = Rank.`10K`,
            difficultRank = Rank.`10K`,
        )
    }
})
