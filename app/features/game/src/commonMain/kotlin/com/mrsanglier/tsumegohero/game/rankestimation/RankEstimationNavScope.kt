package com.mrsanglier.tsumegohero.game.rankestimation

import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.game.model.BoardConfig

data class RankEstimationNavScope(
    val navigateBack: () -> Unit,
    val navigateToReview: (tsumegoId: String, boardConfig: BoardConfig) -> Unit,
    val navigateToResult: (level: Level) -> Unit,
)
