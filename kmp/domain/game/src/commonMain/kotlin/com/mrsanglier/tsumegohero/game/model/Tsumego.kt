package com.mrsanglier.tsumegohero.game.model

import com.mrsanglier.tsumegohero.data.model.game.Rank

data class Tsumego(
    val initialBoard: Board,
    val root: RootNode,
    val rank: Rank,
) {
    val playerStone: Stone
        get() = root.children.first().move.stone
}
