package com.mrsanglier.tsumegohero.dashboardgame.usecase

import com.mrsanglier.tsumegohero.domain.common.delegate.GetProgressDataDelegate
import com.mrsanglier.tsumegohero.domain.common.delegate.GetProgressDataDelegateImpl
import com.mrsanglier.tsumegohero.data.model.objective.ProgressData
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveProgressDataUseCase(
    private val userRepository: UserRepository,
    getProgressDataDelegateImpl: GetProgressDataDelegateImpl,
) : GetProgressDataDelegate by getProgressDataDelegateImpl {
    operator fun invoke(): Flow<ProgressData?> =
        userRepository.observeUser().map { user ->
            user?.let(::getProgressData)
        }
}

