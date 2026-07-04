package com.mrsanglier.tsumegohero.dashboard.navigation

data class DashboardNavScope(
    val navigateToTraining: (tsumegoId: String) -> Unit,
    val navigateToRankEstimation: (tsumegoId: String) -> Unit,
)
