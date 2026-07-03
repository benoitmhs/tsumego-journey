package com.mrsanglier.tsumegohero.domain.common.delegate

import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.user.User
import com.mrsanglier.tsumegohero.domain.common.model.ProgressData

interface GetProgressDataDelegate {
    fun getProgressData(user: User): ProgressData
}

class GetProgressDataDelegateImpl : GetProgressDataDelegate {
    override fun getProgressData(user: User): ProgressData {
        val currentRank = user.level?.classicalRank ?: return ProgressData(
            rank = Rank.`15K`,
            rankStepCompleted = 0,
            rankStepRequired = getRankStepRequired(Rank.`15K`),
            problemStreak = 0,
            problemStreakRequired = ProblemStreakRequired,
        )

        return ProgressData(
            rank = currentRank,
            rankStepCompleted = user.nbRankStepCompleted,
            rankStepRequired = getRankStepRequired(currentRank),
            problemStreak = user.problemStreak,
            problemStreakRequired = ProblemStreakRequired,
        )
    }
}

private fun getRankStepRequired(rank: Rank): Int =
    (ProblemsByRankMap[rank] ?: 0) / ProblemStreakRequired

private const val ProblemStreakRequired: Int = 10
private val ProblemsByRankMap: Map<Rank, Int> = mapOf(
    Rank.`15K` to 10, Rank.`15Kplus` to 20,
    Rank.`14K` to 20, Rank.`14Kplus` to 20,
    Rank.`13K` to 30, Rank.`13Kplus` to 30,
    Rank.`12K` to 40, Rank.`12Kplus` to 40,
    Rank.`11K` to 50, Rank.`11Kplus` to 50,
    Rank.`10K` to 60, Rank.`10Kplus` to 80,
    Rank.`9K` to 90, Rank.`9Kplus` to 100,
    Rank.`8K` to 120, Rank.`8Kplus` to 140,
    Rank.`7K` to 160, Rank.`7Kplus` to 180,
    Rank.`6K` to 220, Rank.`6Kplus` to 240,
    Rank.`5K` to 290, Rank.`5Kplus` to 330,
    Rank.`4K` to 390, Rank.`4Kplus` to 440,
    Rank.`3K` to 520, Rank.`3Kplus` to 590,
    Rank.`2K` to 700, Rank.`2Kplus` to 790,
    Rank.`1K` to 950, Rank.`1Kplus` to 1050,
    Rank.`1D` to 1280, Rank.`1Dplus` to 1390,
    Rank.`2D` to 1680, Rank.`2Dplus` to 1900,
    Rank.`3D` to 2400, Rank.`3Dplus` to 2400,
    Rank.`4D` to 3220, Rank.`4Dplus` to 3220,
    Rank.`5D` to 4310, Rank.`5Dplus` to 4310,
)