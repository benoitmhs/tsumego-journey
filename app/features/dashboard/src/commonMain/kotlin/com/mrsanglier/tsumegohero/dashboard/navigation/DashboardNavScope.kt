package com.mrsanglier.tsumegohero.dashboard.navigation

import com.mrsanglier.tsumegohero.data.model.game.GameContext

data class DashboardNavScope(
    val navigateToGame: (tsumegoId: String, gameContext: GameContext) -> Unit
)
