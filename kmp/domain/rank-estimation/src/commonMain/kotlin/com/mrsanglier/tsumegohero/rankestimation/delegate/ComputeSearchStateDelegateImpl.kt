package com.mrsanglier.tsumegohero.rankestimation.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.domain.common.Config
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import com.mrsanglier.tsumegohero.rankestimation.model.RankEstimationSearchState
import com.mrsanglier.tsumegohero.rankestimation.model.RankEstimationSearchState.Bracket
import com.mrsanglier.tsumegohero.rankestimation.model.RankEstimationSearchState.Tier
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

interface ComputeSearchStateDelegate {
    fun computeSearchState(attempts: List<Attempt>, seedRank: Rank): RankEstimationSearchState
}

class ComputeSearchStateDelegateImpl : ComputeSearchStateDelegate {

    // attempts must be in chronological order (the search is replayed deterministically)
    override fun computeSearchState(attempts: List<Attempt>, seedRank: Rank): RankEstimationSearchState {
        val brackets = Tier.entries.associateWith { tier -> MutableBracket(tier) }
        var consumed = 0
        var nextRankIndex: Int?

        while (true) {
            val targetTier = searchPriority.firstOrNull { !brackets.getValue(it).isConverged }
            if (targetTier == null) {
                nextRankIndex = null
                break
            }

            if (consumed >= attempts.size) {
                nextRankIndex = nextRankToServe(attempts) { brackets.getValue(targetTier).nextRankIndex(seedRank) }
                break
            }

            val blockRankIndex = attempts[consumed].rank.index
            val run = attempts.drop(consumed).takeWhile { it.rank.index == blockRankIndex }
            val blockSize = decidedBlockSize(run)

            if (blockSize == null) {
                val isTrailingRun = consumed + run.size == attempts.size
                if (isTrailingRun) {
                    // undecided 1-1 (or single attempt): keep serving the same rank as tie-breaker
                    nextRankIndex = nextRankToServe(attempts) { blockRankIndex }
                    break
                }
                // undecided run in the middle of the history (legacy data): ignore it
                consumed += run.size
                continue
            }

            val block = run.take(blockSize)
            brackets.values.forEach { bracket -> bracket.update(blockRankIndex, block) }
            consumed += blockSize
        }

        return RankEstimationSearchState(
            brackets = brackets.mapValues { (_, bracket) -> bracket.toBracket() },
            nextRank = nextRankIndex?.let { Rank.entries[it] },
            problemsDone = attempts.size,
            problemsExpected = expectedProblems(attempts.size, brackets.values),
        )
    }

    private fun nextRankToServe(attempts: List<Attempt>, rankIndex: () -> Int): Int? =
        if (attempts.size >= RankEstimationConfig.PROBLEM_CAP) null else rankIndex()

    // number of attempts needed to decide the block on raw results, null if still undecided:
    // 2 failures fail it, 2 clean successes validate it, 3 successes after a failure validate it
    private fun decidedBlockSize(run: List<Attempt>): Int? {
        var successes = 0
        var failures = 0
        run.take(RankEstimationConfig.BLOCK_MAX_SIZE).forEachIndexed { index, attempt ->
            if (attempt.result == Attempt.Result.Success) successes++ else failures++

            val isFailed = failures >= RankEstimationConfig.BLOCK_FAILURE_LIMIT
            val successesToValidate = if (failures == 0) {
                RankEstimationConfig.BLOCK_CLEAN_SUCCESS_COUNT
            } else {
                RankEstimationConfig.BLOCK_RECOVERY_SUCCESS_COUNT
            }
            if (isFailed || successes >= successesToValidate) return index + 1
        }
        return null
    }

    // the tier time threshold applies to the average resolution time of the solved problems
    private fun MutableBracket.update(rankIndex: Int, block: List<Attempt>) {
        val solved = block.filter { it.result == Attempt.Result.Success }
        val failures = block.size - solved.size
        if (failures >= RankEstimationConfig.BLOCK_FAILURE_LIMIT) {
            fail(rankIndex)
            return
        }
        val averageTimeMs = solved.map { it.resolutionTimeMs }.average()
        if (averageTimeMs <= tier.timeLimit.inWholeMilliseconds) {
            validate(rankIndex)
        } else {
            fail(rankIndex)
        }
    }

    private fun expectedProblems(done: Int, brackets: Collection<MutableBracket>): Int {
        val remaining = brackets.sumOf { bracket ->
            if (bracket.isConverged) return@sumOf 0
            val isCadrage = bracket.lo == NO_LO || bracket.hi == NO_HI
            val width = if (isCadrage) RankEstimationConfig.CADRAGE_STEP else bracket.hi - bracket.lo
            val dichotomyBlocks = ceil(log2(width.toDouble())).toInt().coerceAtLeast(0)
            val cadrageBlocks = if (isCadrage) 2 else 0
            (dichotomyBlocks + cadrageBlocks) * RankEstimationConfig.BLOCK_CLEAN_SUCCESS_COUNT
        }
        return min(done + remaining, RankEstimationConfig.PROBLEM_CAP).coerceAtLeast(done)
    }
}

private val searchPriority = listOf(Tier.Classical, Tier.Difficult, Tier.Flash)

private const val NO_LO = -1
private val NO_HI = Rank.entries.size

private class MutableBracket(val tier: Tier) {
    var lo: Int = NO_LO
    var hi: Int = NO_HI
    private val validationCounts = mutableMapOf<Int, Int>()

    val isConverged: Boolean get() = hi - lo <= 1

    fun nextRankIndex(seedRank: Rank): Int = when {
        lo == NO_LO && hi == NO_HI -> seedRank.index
        lo == NO_LO -> max(hi - RankEstimationConfig.CADRAGE_STEP, 0)
        hi == NO_HI -> min(lo + RankEstimationConfig.CADRAGE_STEP, Rank.entries.lastIndex)
        else -> (lo + hi) / 2
    }

    fun validate(rankIndex: Int) {
        validationCounts[rankIndex] = (validationCounts[rankIndex] ?: 0) + 1
        lo = max(lo, rankIndex)
        if (hi <= lo) hi = lo + 1
    }

    fun fail(rankIndex: Int) {
        // a rank validated twice is a definitive floor, a later noisy failure cannot undo it
        val lockedFloor = validationCounts.filterValues { it >= 2 }.keys.maxOrNull() ?: NO_LO
        if (rankIndex <= lockedFloor) return
        hi = min(hi, rankIndex)
        lo = max(min(lo, hi - 1), lockedFloor)
    }

    fun toBracket(): Bracket = Bracket(
        lastValidated = Rank.entries.getOrNull(lo),
        firstFailed = Rank.entries.getOrNull(hi),
    )
}

private val Tier.timeLimit: Duration
    get() = when (this) {
        Tier.Flash -> Config.Game.FLASH_TIME
        Tier.Classical -> Config.Game.CLASSICAL_TIME
        Tier.Difficult -> Config.Game.DIFFICULT_TIME
    }

private val Rank.index: Int get() = Rank.entries.indexOf(this)
