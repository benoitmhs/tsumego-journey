package com.mrsanglier.tsumegohero.rankestimation.usecase

import com.mrsanglier.tsumegohero.core.error.THAppError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import com.mrsanglier.tsumegohero.rankestimation.delegate.ComputeSearchStateDelegate
import com.mrsanglier.tsumegohero.rankestimation.delegate.ComputeSearchStateDelegateImpl
import com.mrsanglier.tsumegohero.rankestimation.delegate.EstimateLevelDelegate
import com.mrsanglier.tsumegohero.rankestimation.delegate.EstimateLevelDelegateImpl
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SubmitRankEstimationAnswerUseCase(
    estimateLevelDelegateImpl: EstimateLevelDelegateImpl,
    computeSearchStateDelegateImpl: ComputeSearchStateDelegateImpl,
    private val userRepository: UserRepository,
    private val attemptRepository: AttemptRepository,
) : EstimateLevelDelegate by estimateLevelDelegateImpl,
    ComputeSearchStateDelegate by computeSearchStateDelegateImpl {

    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        tsumego: RawTsumego,
        resolutionTimeMs: Long,
        gameMode: GameMode,
        result: Attempt.Result,
    ): THResult<Level?> = THResult.catchResult {
        val user = userRepository.observeUser().first()
            ?: throw THAppError.Code.ObjectNotFound.toError("User not found")

        attemptRepository.upsert(
            Attempt(
                id = Uuid.random().toString(),
                userId = user.userId,
                tsumegoId = tsumego.id,
                rank = tsumego.rank,
                result = result,
                mode = gameMode,
                date = Clock.System.now(),
                context = GameContext.RankEstimation,
                resolutionTimeMs = resolutionTimeMs,
            ),
        )

        val attempts = attemptRepository.getRankEstimationAttempts()
        val seedRank = attempts.firstOrNull()?.rank ?: RankEstimationConfig.DEFAULT_SEED_RANK
        val searchState = computeSearchState(attempts, seedRank)

        if (!searchState.isFinished) return@catchResult null

        val level = estimateLevel(searchState)
        userRepository.upsert(user.copy(level = level))
        attemptRepository.deleteRankEstimationAttempts()
        level
    }
}
