package com.mrsanglier.tsumegohero.rankestimation.usecase

import com.mrsanglier.tsumegohero.core.error.THAppError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import com.mrsanglier.tsumegohero.rankestimation.delegate.EstimateLevelDelegate
import com.mrsanglier.tsumegohero.rankestimation.delegate.EstimateLevelDelegateImpl
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SubmitRankEstimationAnswerUseCase(
    estimateLevelDelegateImpl: EstimateLevelDelegateImpl,
    private val userRepository: UserRepository,
    private val attemptRepository: AttemptRepository,
    private val tsumegoRepository: TsumegoRepository,
) : EstimateLevelDelegate by estimateLevelDelegateImpl {

    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        tsumegoId: String,
        resolutionTimeMs: Long,
        gameMode: GameMode,
        result: Attempt.Result,
    ): THResult<Level?> = THResult.catchResult {
        val user = userRepository.observeUser().first()
            ?: throw THAppError.Code.ObjectNotFound.toError("User not found")
        val tsumego = tsumegoRepository.observeGame(tsumegoId).first()

        attemptRepository.upsert(
            Attempt(
                id = Uuid.random().toString(),
                userId = user.userId,
                tsumegoId = tsumegoId,
                result = result,
                mode = gameMode,
                date = Clock.System.now(),
                context = GameContext.RankEstimation,
                resolutionTimeMs = resolutionTimeMs,
            ),
        )

        val attempts = attemptRepository.getRankEstimationAttempts()

        val rankAttempts = attempts.filter { it.first == tsumego.rank }.map { it.second }

        if (rankAttempts.size < RankEstimationConfig.BLOCK_SIZE) return@catchResult null

        val averageResTime = rankAttempts.map { it.resolutionTimeMs }.average()
        val successRate = rankAttempts.count { it.result == Attempt.Result.Success }
        val difficultPassed = averageResTime <= RankEstimationConfig.DIFFICULT_TIME.inWholeMilliseconds
            && successRate >= RankEstimationConfig.SUCCESS_THRESHOLD

        val isMaxRank = Rank.entries.indexOf(tsumego.rank) == Rank.entries.lastIndex

        if (!difficultPassed || isMaxRank) {
            val level = estimateLevel(attempts)
            userRepository.upsert(user.copy(level = level))
            attemptRepository.deleteRankEstimationAttempts()
            return@catchResult level
        }
        null
    }
}
