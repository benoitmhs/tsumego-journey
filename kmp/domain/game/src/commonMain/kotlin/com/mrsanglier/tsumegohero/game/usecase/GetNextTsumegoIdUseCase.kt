package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.core.error.THAppError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first

class GetNextTsumegoIdUseCase(
    private val tsumegoRepository: TsumegoRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): THResult<String> = THResult.catchResult {
        val rank = userRepository.observeUser().first()?.level?.classicalRank
            ?: throw THAppError.Code.ObjectNotFound.toError(message = "User rank not found")

        val tsumego = tsumegoRepository.getNextNeverAttempted(rank)
            ?: tsumegoRepository.getNextNeverSucceeded(rank)
            ?: tsumegoRepository.getOldestAttempted(rank)
            ?: throw THAppError.Code.ObjectNotFound.toError(message = "No tsumego found")

        return@catchResult tsumego.id
    }
}