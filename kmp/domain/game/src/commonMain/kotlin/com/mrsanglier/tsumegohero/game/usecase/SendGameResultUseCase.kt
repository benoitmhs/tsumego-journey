package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.core.error.THAppError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.extension.daysUntil
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import com.mrsanglier.tsumegohero.data.model.user.User
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val DailyStreakThreshold: Int = 2

class SendGameResultUseCase(
    private val userRepository: UserRepository,
    private val attemptRepository: AttemptRepository,
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        result: Attempt.Result,
        tsumego: RawTsumego,
        mode: GameMode,
        gameContext: GameContext,
        resolutionTimeMs: Long,
    ): THResult<User> = THResult.catchResult {
        val user = userRepository.observeUser().first()
            ?: throw THAppError.Code.ObjectNotFound.toError("User not found")
        val lastAttempt = attemptRepository.observeLastSucceedAttempt().first()

        val newAttempt = Attempt(
            id = Uuid.random().toString(),
            userId = user.userId,
            tsumegoId = tsumego.id,
            rank = tsumego.rank,
            result = result,
            mode = mode,
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
        userRepository.upsert(updatedUser)
        updatedUser
    }

    private fun calculateDailyStreak(lastAttempt: Attempt?, currentDailStreak: Int): Int {
        val dayUntilLastAttempt = lastAttempt?.date?.daysUntil(Clock.System.now())
        return when {
            (1..DailyStreakThreshold).contains(dayUntilLastAttempt) -> currentDailStreak + 1
            dayUntilLastAttempt == 0 -> currentDailStreak
            else -> 1
        }
    }
}