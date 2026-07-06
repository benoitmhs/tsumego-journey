package com.mrsanglier.tsumegohero.rankestimation.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.rankestimation.model.RankEstimationSearchState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

private fun attempt(rank: Rank, result: Attempt.Result, timeMs: Long, index: Int) =
    Attempt(
        id = "id$index",
        userId = "user",
        tsumegoId = "tsumego$index",
        rank = rank,
        result = result,
        date = Instant.fromEpochMilliseconds(index.toLong()),
        context = GameContext.RankEstimation,
        resolutionTimeMs = timeMs,
    )

// Simulates a full estimation: serves the rank asked by the search until it finishes
private fun ComputeSearchStateDelegateImpl.runEstimation(
    seedRank: Rank,
    answer: (Rank) -> Pair<Attempt.Result, Long>,
): Pair<List<Attempt>, RankEstimationSearchState> {
    val attempts = mutableListOf<Attempt>()
    var state = computeSearchState(attempts, seedRank)
    while (!state.isFinished) {
        val rank = state.nextRank ?: break
        val (result, timeMs) = answer(rank)
        attempts += attempt(rank, result, timeMs, attempts.size)
        state = computeSearchState(attempts, seedRank)
    }
    return attempts to state
}

// A consistent player: flash up to flashIndex, classical up to classicalIndex, difficult up to difficultIndex
private fun player(
    flashIndex: Int,
    classicalIndex: Int = flashIndex,
    difficultIndex: Int = classicalIndex,
): (Rank) -> Pair<Attempt.Result, Long> = { rank ->
    when (val index = Rank.entries.indexOf(rank)) {
        in 0..flashIndex -> Attempt.Result.Success to 10_000L
        in 0..classicalIndex -> Attempt.Result.Success to 60_000L
        in 0..difficultIndex -> Attempt.Result.Success to 300_000L
        else -> Attempt.Result.Failure to 60_000L
    }
}

private fun estimatedLevel(state: RankEstimationSearchState): Level =
    EstimateLevelDelegateImpl().estimateLevel(state)

class ComputeSearchStateDelegateTest : FunSpec({
    val delegate = ComputeSearchStateDelegateImpl()
    val rank5kIndex = Rank.entries.indexOf(Rank.`5K`)

    test("a consistent 5k player declared 10k converges in less than 20 problems, flash and difficult forced 1 kyu around classical") {
        val (attempts, state) = delegate.runEstimation(
            seedRank = Rank.`10K`,
            answer = player(flashIndex = rank5kIndex),
        )

        state.isFinished.shouldBeTrue()
        attempts.size shouldBeLessThanOrEqual 20
        estimatedLevel(state) shouldBe Level(
            flashRank = Rank.`6K`,
            classicalRank = Rank.`5K`,
            difficultRank = Rank.`4K`,
        )
    }

    test("a beginner failing everything converges quickly to the weakest rank") {
        val (attempts, state) = delegate.runEstimation(
            seedRank = Rank.`10K`,
            answer = { Attempt.Result.Failure to 60_000L },
        )

        state.isFinished.shouldBeTrue()
        attempts.size shouldBeLessThanOrEqual 8
        estimatedLevel(state) shouldBe Level(
            flashRank = Rank.entries.first(),
            classicalRank = Rank.entries.first(),
            difficultRank = Rank.`14K`,
        )
    }

    test("a player succeeding everything converges to the strongest rank") {
        val (attempts, state) = delegate.runEstimation(
            seedRank = Rank.`10K`,
            answer = player(flashIndex = Rank.entries.lastIndex),
        )

        state.isFinished.shouldBeTrue()
        attempts.size shouldBeLessThanOrEqual 20
        estimatedLevel(state) shouldBe Level(
            flashRank = Rank.`6Dplus`,
            classicalRank = Rank.entries.last(),
            difficultRank = Rank.entries.last(),
        )
    }

    test("slower tiers are estimated lower than faster ones from the same attempts") {
        val flashIndex = Rank.entries.indexOf(Rank.`10K`)
        val classicalIndex = Rank.entries.indexOf(Rank.`5K`)
        val difficultIndex = Rank.entries.indexOf(Rank.`3K`)

        val (_, state) = delegate.runEstimation(
            seedRank = Rank.`10K`,
            answer = player(
                flashIndex = flashIndex,
                classicalIndex = classicalIndex,
                difficultIndex = difficultIndex,
            ),
        )

        state.isFinished.shouldBeTrue()
        estimatedLevel(state) shouldBe Level(
            flashRank = Rank.`10K`,
            classicalRank = Rank.`5K`,
            difficultRank = Rank.`3K`,
        )
    }

    test("the problem cap forces the estimation to finish") {
        val attempts = List(26) { index ->
            val result = if (index % 2 == 0) Attempt.Result.Success else Attempt.Result.Failure
            attempt(Rank.`10K`, result, timeMs = 60_000L, index = index)
        }

        val state = delegate.computeSearchState(attempts, seedRank = Rank.`10K`)

        state.isFinished.shouldBeTrue()
    }

    test("two skips fail the block for the three tiers") {
        val attempts = listOf(
            attempt(Rank.`10K`, Attempt.Result.Skip, timeMs = 10_000L, index = 0),
            attempt(Rank.`10K`, Attempt.Result.Skip, timeMs = 10_000L, index = 1),
        )

        val state = delegate.computeSearchState(attempts, seedRank = Rank.`10K`)

        RankEstimationSearchState.Tier.entries.forEach { tier ->
            state.brackets.getValue(tier).firstFailed shouldBe Rank.`10K`
        }
    }

    test("a single failure does not decide the block, a 3rd problem breaks the tie") {
        val undecided = listOf(
            attempt(Rank.`10K`, Attempt.Result.Success, timeMs = 60_000L, index = 0),
            attempt(Rank.`10K`, Attempt.Result.Skip, timeMs = 60_000L, index = 1),
        )

        val undecidedState = delegate.computeSearchState(undecided, seedRank = Rank.`10K`)
        undecidedState.nextRank shouldBe Rank.`10K`

        val decided = undecided + attempt(Rank.`10K`, Attempt.Result.Success, timeMs = 60_000L, index = 2)

        val decidedState = delegate.computeSearchState(decided, seedRank = Rank.`10K`)
        decidedState.brackets
            .getValue(RankEstimationSearchState.Tier.Classical)
            .lastValidated shouldBe Rank.`10K`
    }

    test("tier thresholds apply to the average time of the solved problems of the block") {
        val attempts = listOf(
            attempt(Rank.`10K`, Attempt.Result.Success, timeMs = 15_000L, index = 0),
            attempt(Rank.`10K`, Attempt.Result.Success, timeMs = 22_000L, index = 1),
        )

        val state = delegate.computeSearchState(attempts, seedRank = Rank.`10K`)

        // one problem exceeds 20s but the block average (18.5s) validates the flash tier
        state.brackets
            .getValue(RankEstimationSearchState.Tier.Flash)
            .lastValidated shouldBe Rank.`10K`
    }

    test("an incomplete block keeps serving the same rank") {
        val attempts = listOf(
            attempt(Rank.`10K`, Attempt.Result.Success, timeMs = 60_000L, index = 0),
        )

        val state = delegate.computeSearchState(attempts, seedRank = Rank.`10K`)

        state.nextRank shouldBe Rank.`10K`
    }
})
