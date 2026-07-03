package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.game.model.Game

class RestartGameUseCase {
    operator fun invoke(
        game: Game,
    ): Game {
        return game.copy(
            board = game.tsumego.initialBoard,
            moveStack = emptyList(),
            reviewRoot = null,
            isGhostSubmitted = false,
        )
    }
}