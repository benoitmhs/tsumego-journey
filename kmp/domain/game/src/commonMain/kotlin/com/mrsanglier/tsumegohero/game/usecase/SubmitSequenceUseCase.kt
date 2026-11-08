package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.game.model.Game

/**
 * Submits the sequence played in manual mode. An incomplete sequence (correct so far but not
 * finished) is not submitted: the game stays in play and is flagged so the UI can tell the player.
 */
class SubmitSequenceUseCase {
    operator fun invoke(game: Game): Game =
        if (game.isSequenceIncomplete) {
            game.copy(isSubmissionIncomplete = true)
        } else {
            game.copy(isSequenceSubmitted = true, isSubmissionIncomplete = false)
        }
}
