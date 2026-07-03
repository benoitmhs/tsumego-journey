package com.mrsanglier.tsumegohero.data.model.user

import com.mrsanglier.tsumegohero.data.model.game.Rank

data class Level(
    val flashRank: Rank,
    val classicalRank: Rank,
    val difficultRank: Rank,
)
