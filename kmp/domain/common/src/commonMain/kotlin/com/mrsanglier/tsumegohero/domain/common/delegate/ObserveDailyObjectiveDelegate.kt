package com.mrsanglier.tsumegohero.domain.common.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import com.mrsanglier.tsumegohero.data.model.objective.DailyObjective
import com.mrsanglier.tsumegohero.domain.common.Config
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.collections.plus

interface ObserveDailyObjectiveDelegate {
    fun observeDailyObjective(daysAgo: Int = 0): Flow<DailyObjective>
}

class ObserveDailyObjectiveDelegateImpl(
    private val attemptRepository: AttemptRepository,
) : ObserveDailyObjectiveDelegate {
    override fun observeDailyObjective(daysAgo: Int): Flow<DailyObjective> =
        attemptRepository.observeTrainingAttemptsOfDay(daysAgo).map { attempts ->
            val flashs = attempts.filterByTrainingMode(TrainingMode.Flash)
                .take(Config.DailyObjective.FLASH_TOTAL_OBJECTIVE)
            val classicals = attempts.filterByTrainingMode(TrainingMode.Classical)
                .take(Config.DailyObjective.CLASSICAL_TOTAL_OBJECTIVE)
            val difficults = attempts.filterByTrainingMode(TrainingMode.Difficult)
                .take(Config.DailyObjective.DIFFICULT_TOTAL_OBJECTIVE)

            val remainingFlash = Config.DailyObjective.FLASH_TOTAL_OBJECTIVE - flashs.count()
            val remainingClassical = Config.DailyObjective.CLASSICAL_TOTAL_OBJECTIVE - classicals.count()
            val remainingDifficult = Config.DailyObjective.DIFFICULT_TOTAL_OBJECTIVE - difficults.count()

            DailyObjective(
                flashProblemResults = flashs + List(remainingFlash) { null },
                classicalProblemResults = classicals + List(remainingClassical) { null },
                difficultProblemResults = difficults + List(remainingDifficult) { null },
            )
        }
            .distinctUntilChanged { old, new ->
                old.flashProblemResults.filterNotNull().count() == new.flashProblemResults.filterNotNull().count() &&
                    old.classicalProblemResults.filterNotNull().count() == new.classicalProblemResults.filterNotNull().count() &&
                    old.difficultProblemResults.filterNotNull().count() == new.difficultProblemResults.filterNotNull().count()
            }

    private fun List<Attempt>.filterByTrainingMode(trainingMode: TrainingMode): List<Attempt> =
        filter { (it.context as? GameContext.Training)?.trainingMode == trainingMode }
}