package com.mrsanglier.tsumegohero.domain.authentication.usecase

import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.user.User
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class InitUserUseCase(
    private val userRepository: UserRepository,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(): THResult<User> = THResult.catchResult {
        val user = userRepository.observeUser().first()

        if (user != null) return@catchResult user

        val newUser = User(
            userId = Uuid.random().toString(),
            username = "",
            level = null,
            dailyStreak = 0,
            problemStreak = 0,
            nbRankStepCompleted = 0,
            bestDailyStreak = 0,
            nbTsumegoSolved = 0,
        )

        userRepository.upsert(newUser)
        return@catchResult newUser
    }
}