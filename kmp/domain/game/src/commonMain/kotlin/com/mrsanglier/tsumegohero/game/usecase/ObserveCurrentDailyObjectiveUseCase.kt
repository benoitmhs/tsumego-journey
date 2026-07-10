package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import com.mrsanglier.tsumegohero.data.model.objective.DailyObjective
import com.mrsanglier.tsumegohero.domain.common.delegate.ObserveDailyObjectiveDelegate
import com.mrsanglier.tsumegohero.domain.common.delegate.ObserveDailyObjectiveDelegateImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveCurrentDailyObjectiveUseCase(
    observeDailyObjectiveDelegateImpl: ObserveDailyObjectiveDelegateImpl,
) : ObserveDailyObjectiveDelegate by observeDailyObjectiveDelegateImpl {

    operator fun invoke(trainingMode: TrainingMode): Flow<List<Attempt.Result?>> =
        observeDailyObjective()
            .map { objective ->
                objective.attemptsFor(trainingMode).map { attempt -> attempt?.result }
            }
            .distinctUntilChanged { old, new ->
                old.count { it != null } == new.count { it != null }
                    && old.count() == new.count()
            }

    private fun DailyObjective.attemptsFor(trainingMode: TrainingMode): List<Attempt?> =
        when (trainingMode) {
            TrainingMode.Flash -> flashProblemResults
            TrainingMode.Classical -> classicalProblemResults
            TrainingMode.Difficult -> difficultProblemResults
        }

}