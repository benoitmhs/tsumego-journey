package com.mrsanglier.tsumegohero.rankestimation.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.Rank
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
            if (targetTier == null || consumed >= RankEstimationConfig.PROBLEM_CAP) {
                nextRankIndex = null
                break
            }

            val blockSize = brackets.getValue(targetTier).blockSize
            if (consumed >= attempts.size) {
                nextRankIndex = brackets.getValue(targetTier).nextRankIndex(seedRank)
                break
            }

            val blockRankIndex = attempts[consumed].rank.index
            val block = attempts
                .subList(consumed, min(consumed + blockSize, attempts.size))
                .takeWhile { it.rank.index == blockRankIndex }

            val isTrailingIncompleteBlock = block.size < blockSize
                && consumed + block.size == attempts.size
            if (isTrailingIncompleteBlock) {
                nextRankIndex = blockRankIndex
                break
            }

            brackets.values.forEach { bracket -> bracket.update(blockRankIndex, block) }
            consumed += block.size
        }

        return RankEstimationSearchState(
            brackets = brackets.mapValues { (_, bracket) -> bracket.toBracket() },
            nextRank = nextRankIndex?.let { Rank.entries[it] },
            problemsDone = attempts.size,
            problemsExpected = expectedProblems(attempts.size, brackets.values),
        )
    }

    private fun MutableBracket.update(rankIndex: Int, block: List<Attempt>) {
        val validated = block.count { it.isSuccessFor(tier) } >= RankEstimationConfig.BLOCK_SUCCESS_QUORUM
        if (validated) validate(rankIndex) else fail(rankIndex)
    }

    private fun expectedProblems(done: Int, brackets: Collection<MutableBracket>): Int {
        val remaining = brackets.sumOf { bracket ->
            if (bracket.isConverged) return@sumOf 0
            val isCadrage = bracket.lo == NO_LO || bracket.hi == NO_HI
            val width = if (isCadrage) RankEstimationConfig.CADRAGE_STEP else bracket.hi - bracket.lo
            val dichotomyBlocks = ceil(log2(width.toDouble())).toInt().coerceAtLeast(0)
            val cadrageBlocks = if (isCadrage) 2 else 0
            dichotomyBlocks * RankEstimationConfig.DICHOTOMY_BLOCK_SIZE +
                cadrageBlocks * RankEstimationConfig.CADRAGE_BLOCK_SIZE
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

    // cadrage while a bound is still unknown, then dichotomy
    val blockSize: Int
        get() = if (lo == NO_LO || hi == NO_HI) {
            RankEstimationConfig.CADRAGE_BLOCK_SIZE
        } else {
            RankEstimationConfig.DICHOTOMY_BLOCK_SIZE
        }

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

private fun Attempt.isSuccessFor(tier: Tier): Boolean =
    result == Attempt.Result.Success && resolutionTimeMs <= tier.timeLimit.inWholeMilliseconds

private val Tier.timeLimit: Duration
    get() = when (this) {
        Tier.Flash -> RankEstimationConfig.FLASH_TIME
        Tier.Classical -> RankEstimationConfig.CLASSICAL_TIME
        Tier.Difficult -> RankEstimationConfig.DIFFICULT_TIME
    }

private val Rank.index: Int get() = Rank.entries.indexOf(this)
