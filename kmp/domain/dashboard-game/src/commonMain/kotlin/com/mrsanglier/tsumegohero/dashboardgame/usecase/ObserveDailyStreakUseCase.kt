package com.mrsanglier.tsumegohero.dashboardgame.usecase

import com.mrsanglier.tsumegohero.core.extension.daysUntil
import com.mrsanglier.tsumegohero.core.extension.handleTHError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.user.DailyStreak
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.Instant

class ObserveDailyStreakUseCase(
    private val userRepository: UserRepository,
    private val attemptRepository: AttemptRepository,
) {
    operator fun invoke(): Flow<THResult<DailyStreak>> =
        combine(
            flow = userRepository.observeUser()
                .map { it?.dailyStreak }
                .distinctUntilChanged(),
            flow2 = attemptRepository.observeLastSucceedAttempt(),
        ) { dailyStreak, lastAttempt ->
            getDailyStreak(
                streakCount = dailyStreak,
                lastAttemptDate = lastAttempt?.date,
            )
        }
            .handleTHError()
            .distinctUntilChanged()

    private fun getDailyStreak(
        streakCount: Int?,
        lastAttemptDate: Instant?,
    ): DailyStreak {
        if (streakCount == null || lastAttemptDate == null || streakCount <= 0) {
            return DailyStreak(
                streakCount = 0,
                status = DailyStreak.Status.Unactive,
            )
        }

        val dayUntilLastAttempt = lastAttemptDate.daysUntil(Clock.System.now())

        val status = when (dayUntilLastAttempt) {
            0 -> DailyStreak.Status.Today
            1 -> DailyStreak.Status.Hot
            2 -> DailyStreak.Status.Cold
            else -> DailyStreak.Status.Unactive
        }

        return DailyStreak(
            streakCount = streakCount,
            status = status,
        )
    }
}
