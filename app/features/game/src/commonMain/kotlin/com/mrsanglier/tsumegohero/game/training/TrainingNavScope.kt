package com.mrsanglier.tsumegohero.game.training

import com.mrsanglier.tsumegohero.game.model.BoardConfig

data class TrainingNavScope(
    val navigateBack: () -> Unit,
    val navigateToReview: (tsumegoId: String, boardConfig: BoardConfig) -> Unit,
)
