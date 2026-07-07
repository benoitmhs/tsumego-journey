package com.mrsanglier.tsumegohero.dashboard.screens.home

import com.mrsanglier.tsumegohero.data.model.game.TrainingMode

data class HomeNavScope(
    val navigateToTraining: (tsumegoId: String, trainingMode: TrainingMode) -> Unit,
    val navigateToRankEstimation: (tsumegoId: String) -> Unit,
)
