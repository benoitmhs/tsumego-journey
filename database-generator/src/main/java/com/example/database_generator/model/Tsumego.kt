package com.example.database_generator.model


data class Tsumego(
    val initialBoard: Board,
    val root: RootNode,
    val rank: Rank,
) {
    val playerStone: Stone
        get() = root.children.first().move.stone
}
