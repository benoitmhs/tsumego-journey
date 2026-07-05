package com.mrsanglier.tsumegohero.rankestimation.delegate

import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import com.mrsanglier.tsumegohero.rankestimation.model.RankEstimationSearchState
import com.mrsanglier.tsumegohero.rankestimation.model.RankEstimationSearchState.Tier
import kotlin.math.max
import kotlin.math.min

interface EstimateLevelDelegate {
    fun estimateLevel(searchState: RankEstimationSearchState): Level
}

class EstimateLevelDelegateImpl : EstimateLevelDelegate {

    // flash is at least 1 kyu below classical, difficult at least 1 kyu above
    override fun estimateLevel(searchState: RankEstimationSearchState): Level {
        val classicalIndex = searchState.validatedRankIndex(Tier.Classical)
        val flashIndex = min(
            searchState.validatedRankIndex(Tier.Flash),
            classicalIndex - RankEstimationConfig.TIER_MIN_GAP,
        )
        val difficultIndex = max(
            searchState.validatedRankIndex(Tier.Difficult),
            classicalIndex + RankEstimationConfig.TIER_MIN_GAP,
        )

        return Level(
            flashRank = rankAt(flashIndex),
            classicalRank = rankAt(classicalIndex),
            difficultRank = rankAt(difficultIndex),
        )
    }

    private fun RankEstimationSearchState.validatedRankIndex(tier: Tier): Int =
        brackets[tier]?.lastValidated?.let { Rank.entries.indexOf(it) } ?: 0

    private fun rankAt(index: Int): Rank =
        Rank.entries[index.coerceIn(0, Rank.entries.lastIndex)]
}
