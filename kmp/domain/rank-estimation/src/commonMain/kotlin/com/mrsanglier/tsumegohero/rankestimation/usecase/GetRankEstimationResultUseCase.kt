package com.mrsanglier.tsumegohero.rankestimation.usecase

import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first

class GetRankEstimationResultUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): THResult<Level?> = THResult.catchResult {
        userRepository.observeUser().first()?.level
    }
}
