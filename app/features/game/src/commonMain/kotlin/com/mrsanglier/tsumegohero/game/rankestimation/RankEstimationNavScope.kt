package com.mrsanglier.tsumegohero.game.rankestimation

data class RankEstimationNavScope(
    val navigateBack: () -> Unit,
    val navigateToReview: (tsumegoId: String) -> Unit,
)
