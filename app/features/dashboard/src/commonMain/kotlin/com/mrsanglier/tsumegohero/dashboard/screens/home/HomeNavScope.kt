package com.mrsanglier.tsumegohero.dashboard.screens.home

import com.mrsanglier.tsumegohero.data.model.game.GameContext

data class HomeNavScope(
    val navigateToGame: (tsumegoId: String, gameContext: GameContext) -> Unit,
)