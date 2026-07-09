package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.core.error.THAppError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.extension.daysUntil
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import com.mrsanglier.tsumegohero.data.model.objective.DailyObjective
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.data.model.user.User
import com.mrsanglier.tsumegohero.domain.common.Config
import com.mrsanglier.tsumegohero.domain.common.delegate.ObserveDailyObjectiveDelegate
import com.mrsanglier.tsumegohero.domain.common.delegate.ObserveDailyObjectiveDelegateImpl
import com.mrsanglier.tsumegohero.game.delegate.UpdateLevelDelegate
import com.mrsanglier.tsumegohero.game.delegate.UpdateLevelDelegateImpl
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val DailyStreakThreshold: Int = 2

data class SendGameResultData(
    val classicalPromotion: Rank?,
    val dailyStreakNotif: Int?,
    val objectiveNotif: Pair<TrainingMode, List<Attempt.Result?>>?
)

class SendGameResultUseCase(
    updateLevelDelegateImpl: UpdateLevelDelegateImpl,
    observeDailyObjectiveDelegateImpl: ObserveDailyObjectiveDelegateImpl,
    private val userRepository: UserRepository,
    private val attemptRepository: AttemptRepository,
) : UpdateLevelDelegate by updateLevelDelegateImpl,
    ObserveDailyObjectiveDelegate by observeDailyObjectiveDelegateImpl {

    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        result: Attempt.Result,
        tsumego: RawTsumego,
        gameContext: GameContext,
        resolutionTimeMs: Long,
    ): THResult<SendGameResultData> = THResult.catchResult {
        val user = userRepository.observeUser().first()
            ?: throw THAppError.Code.ObjectNotFound.toError("User not found")
        val lastAttempt = attemptRepository.observeLastSucceedAttempt().first()

        val newAttempt = Attempt(
            id = Uuid.random().toString(),
            userId = user.userId,
            tsumegoId = tsumego.id,
            rank = tsumego.rank,
            result = result,
            date = Clock.System.now(),
            resolutionTimeMs = resolutionTimeMs,
            context = gameContext,
        )
        attemptRepository.upsert(newAttempt)

        val updatedUser = if (result == Attempt.Result.Success) {
            val newDailyStreak = calculateDailyStreak(lastAttempt, user.dailyStreak)
            user.copy(
                dailyStreak = newDailyStreak,
                bestDailyStreak = maxOf(newDailyStreak, user.bestDailyStreak),
                problemStreak = user.problemStreak + 1,
                nbTsumegoSolved = attemptRepository.getTsumegoSolvedCount(),
            )
        } else {
            // Reset problem streak
            user.copy(
                problemStreak = 0,
            )
        }
            .withUpdatedLevel(gameContext)

        userRepository.upsert(updatedUser)

        SendGameResultData(
            dailyStreakNotif = updatedUser.dailyStreak.takeIf { it > user.dailyStreak },
            classicalPromotion = updatedUser.level?.classicalRank
                ?.takeIf { it > (user.level?.classicalRank ?: Rank.entries.first()) },
            objectiveNotif = calculateDailyObjectiveNotif(
                gameContext = gameContext,
                dailyObjective = observeDailyObjective().first(),
                currentAttemptId = newAttempt.id,
            ),
        )
    }

    private suspend fun User.withUpdatedLevel(gameContext: GameContext): User {
        if (gameContext !is GameContext.Training) return this
        val currentLevel = level ?: return this

        val lastAttempts = attemptRepository.getLastAttempts(
            gameContext = gameContext,
            limit = Config.LevelProgression.WINDOW_SIZE,
        )
        val updatedLevel: Level = updateLevel(
            level = currentLevel,
            trainingMode = gameContext.trainingMode,
            lastAttempts = lastAttempts,
        ) ?: return this

        return copy(level = updatedLevel)
    }

    private fun calculateDailyStreak(lastAttempt: Attempt?, currentDailStreak: Int): Int {
        val dayUntilLastAttempt = lastAttempt?.date?.daysUntil(Clock.System.now())
        return when {
            (1..DailyStreakThreshold).contains(dayUntilLastAttempt) -> currentDailStreak + 1
            dayUntilLastAttempt == 0 -> currentDailStreak
            else -> 1
        }
    }

    private fun calculateDailyObjectiveNotif(
        gameContext: GameContext,
        dailyObjective: DailyObjective,
        currentAttemptId: String,
    ): Pair<TrainingMode, List<Attempt.Result?>>? {
        val trainingMode = (gameContext as? GameContext.Training)?.trainingMode ?: return null
        val attempts = when (trainingMode) {
            TrainingMode.Flash -> dailyObjective.flashProblemResults
            TrainingMode.Classical -> dailyObjective.classicalProblemResults
            TrainingMode.Difficult -> dailyObjective.difficultProblemResults
        }

        return if (attempts.any { it?.id == currentAttemptId }) {
            trainingMode to attempts.map { it?.result }
        } else {
            null
        }
    }
}