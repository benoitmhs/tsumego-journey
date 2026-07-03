package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.game.model.Game

/**
 * Ends the ghost sequence and reveals whether it succeeded. [Game.outcome] then checks whether
 * any move played so far matches a successful line in the solution tree: the player's sequence
 * may go deeper than the solution, but a shorter/diverging sequence fails.
 */
class SubmitGhostSequenceUseCase {
    operator fun invoke(game: Game): Game = game.copy(isGhostSubmitted = true)
}
