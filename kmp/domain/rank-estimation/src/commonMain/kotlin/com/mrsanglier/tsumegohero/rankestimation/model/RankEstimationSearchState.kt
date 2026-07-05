package com.mrsanglier.tsumegohero.rankestimation.model

import com.mrsanglier.tsumegohero.data.model.game.Rank

/**
 * State of the rank estimation search, fully derived by replaying the chronological
 * list of rank estimation attempts.
 *
 * @property brackets: search interval per tier
 * @property nextRank: rank of the next problem to serve, null when the search is finished
 * @property problemsDone: number of problems already attempted
 * @property problemsExpected: estimated total number of problems for the whole search
 */
data class RankEstimationSearchState(
    val brackets: Map<Tier, Bracket>,
    val nextRank: Rank?,
    val problemsDone: Int,
    val problemsExpected: Int,
) {
    val isFinished: Boolean get() = nextRank == null

    enum class Tier {
        Flash, Classical, Difficult;
    }

    /**
     * @property lastValidated: highest rank validated for the tier, null if none yet
     * @property firstFailed: lowest rank failed for the tier, null if none yet
     */
    data class Bracket(
        val lastValidated: Rank? = null,
        val firstFailed: Rank? = null,
    )
}
