package com.mrsanglier.tsumegohero.dashboard.screens.home

data class HomeNavScope(
    val navigateToTraining: (tsumegoId: String) -> Unit,
    val navigateToRankEstimation: (tsumegoId: String) -> Unit,
)
