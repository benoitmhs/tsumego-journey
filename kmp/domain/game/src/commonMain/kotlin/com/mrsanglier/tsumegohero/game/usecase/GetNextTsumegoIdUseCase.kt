package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.core.error.THAppError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.domain.common.delegate.GetNextTsumegoFromRankDelegate
import com.mrsanglier.tsumegohero.domain.common.delegate.GetNextTsumegoFromRankDelegateImpl
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first

class GetNextTsumegoIdUseCase(
    getNextTsumegoFromRankDelegateImpl: GetNextTsumegoFromRankDelegateImpl,
    private val userRepository: UserRepository,
) : GetNextTsumegoFromRankDelegate by getNextTsumegoFromRankDelegateImpl {

    suspend operator fun invoke(
        trainingMode: TrainingMode,
    ): THResult<String> = THResult.catchResult {
        val level = userRepository.observeUser().first()?.level
            ?: throw THAppError.Code.ObjectNotFound.toError(message = "User level not found")

        val rank = when (trainingMode) {
            TrainingMode.Flash -> level.flashRank
            TrainingMode.Classical -> level.classicalRank
            TrainingMode.Difficult -> level.difficultRank
        }

        val tsumego = getNextTsumegoFromRank(rank)
            ?: throw THAppError.Code.ObjectNotFound.toError(message = "No tsumego found")

        return@catchResult tsumego.id
    }
}