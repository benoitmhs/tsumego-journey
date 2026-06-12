package com.mrsanglier.tsumegohero.dashboardgame.usecase

import com.mrsanglier.tsumegohero.data.model.user.User
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class ObserveUserUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<User?> =
        userRepository.observeUser()
            .distinctUntilChanged()
}
