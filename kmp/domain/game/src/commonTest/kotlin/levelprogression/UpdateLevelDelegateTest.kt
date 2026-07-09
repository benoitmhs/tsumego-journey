package levelprogression

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.game.delegate.UpdateLevelDelegateImpl
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

private fun attempt(
    rank: Rank,
    result: Attempt.Result,
    timeMs: Long,
    index: Int,
    trainingMode: TrainingMode = TrainingMode.Classical,
) = Attempt(
    id = "id$index",
    userId = "user",
    tsumegoId = "tsumego$index",
    rank = rank,
    result = result,
    date = Instant.fromEpochMilliseconds(index.toLong()),
    context = GameContext.Training(trainingMode),
    resolutionTimeMs = timeMs,
)

// flash 7k, classical 5k, difficult 3k
private val level = Level(
    flashRank = Rank.`7K`,
    classicalRank = Rank.`5K`,
    difficultRank = Rank.`3K`,
)

private fun window(
    rank: Rank = Rank.`5K`,
    successes: Int,
    failures: Int = 0,
    slowSuccesses: Int = 0,
    successTimeMs: Long = 60_000L,
    slowTimeMs: Long = 300_000L,
): List<Attempt> {
    val attempts = List(successes) { attempt(rank, Attempt.Result.Success, successTimeMs, it) } +
        List(slowSuccesses) { attempt(rank, Attempt.Result.Success, slowTimeMs, successes + it) } +
        List(failures) { attempt(rank, Attempt.Result.Failure, successTimeMs, successes + slowSuccesses + it) }
    return attempts.reversed()
}

class UpdateLevelDelegateTest : FunSpec({
    val delegate = UpdateLevelDelegateImpl()

    test("18 fast successes out of 20 promote the tier by one rank") {
        val updated = delegate.updateLevel(level, TrainingMode.Classical, window(successes = 18, failures = 2))

        updated shouldBe level.copy(classicalRank = Rank.`5Kplus`)
    }

    test("a slow success is neutral, it does not count toward the promotion rate") {
        val updated = delegate.updateLevel(
            level,
            TrainingMode.Classical,
            window(successes = 17, slowSuccesses = 3),
        )

        updated shouldBe null
    }

    test("8 failures out of 20 demote the tier by one rank") {
        val updated = delegate.updateLevel(level, TrainingMode.Classical, window(successes = 12, failures = 8))

        updated shouldBe level.copy(classicalRank = Rank.`6Kplus`)
    }

    test("an incomplete window is not decisive") {
        val updated = delegate.updateLevel(
            level,
            TrainingMode.Classical,
            window(successes = 18, failures = 1),
        )

        updated shouldBe null
    }

    test("an attempt at another rank in the window blocks the decision") {
        val attempts = window(successes = 19, failures = 0) +
            attempt(Rank.`6K`, Attempt.Result.Success, timeMs = 60_000L, index = 99)

        val updated = delegate.updateLevel(level, TrainingMode.Classical, attempts)

        updated shouldBe null
    }

    test("in between rates the tier is stable") {
        val updated = delegate.updateLevel(level, TrainingMode.Classical, window(successes = 15, failures = 5))

        updated shouldBe null
    }

    test("a classical promotion pushes the neighbor tiers to keep the 1 kyu gaps") {
        val tightLevel = Level(
            flashRank = Rank.`6K`,
            classicalRank = Rank.`5K`,
            difficultRank = Rank.`4K`,
        )

        val updated = delegate.updateLevel(
            tightLevel,
            TrainingMode.Classical,
            window(successes = 20),
        )

        updated shouldBe Level(
            flashRank = Rank.`6K`,
            classicalRank = Rank.`5Kplus`,
            difficultRank = Rank.`4Kplus`,
        )
    }

    test("a flash promotion is clamped 1 kyu below classical") {
        val tightLevel = Level(
            flashRank = Rank.`6K`,
            classicalRank = Rank.`5K`,
            difficultRank = Rank.`4K`,
        )

        val updated = delegate.updateLevel(
            tightLevel,
            TrainingMode.Flash,
            window(rank = Rank.`6K`, successes = 20, successTimeMs = 10_000L),
        )

        updated shouldBe null
    }

    test("the flash time threshold is 20 seconds, a 60s success is neutral for flash") {
        val updated = delegate.updateLevel(
            level,
            TrainingMode.Flash,
            window(rank = Rank.`7K`, successes = 20, successTimeMs = 60_000L),
        )

        updated shouldBe null
    }
})
