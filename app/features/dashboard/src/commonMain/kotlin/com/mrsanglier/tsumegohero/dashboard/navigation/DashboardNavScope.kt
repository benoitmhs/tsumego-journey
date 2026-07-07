package com.mrsanglier.tsumegohero.dashboard.navigation

import com.mrsanglier.tsumegohero.data.model.game.TrainingMode

data class DashboardNavScope(
    val navigateToTraining: (tsumegoId: String, trainingMode: TrainingMode) -> Unit,
    val navigateToRankEstimation: (tsumegoId: String) -> Unit,
)
