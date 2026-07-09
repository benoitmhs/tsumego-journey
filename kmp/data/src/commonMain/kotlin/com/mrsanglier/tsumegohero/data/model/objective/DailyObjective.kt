package com.mrsanglier.tsumegohero.data.model.objective

import com.mrsanglier.tsumegohero.data.model.game.Attempt

/**
 * List<Attempt.Result> represente the list of problem results done. null → problem not started.
 */
data class DailyObjective(
    val flashProblemResults: List<Attempt?>,
    val classicalProblemResults: List<Attempt?>,
    val difficultProblemResults: List<Attempt?>,
)