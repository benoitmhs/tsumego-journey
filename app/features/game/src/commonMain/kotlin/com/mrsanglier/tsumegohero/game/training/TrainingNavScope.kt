package com.mrsanglier.tsumegohero.game.training

data class TrainingNavScope(
    val navigateBack: () -> Unit,
    val navigateToReview: (tsumegoId: String) -> Unit,
)
