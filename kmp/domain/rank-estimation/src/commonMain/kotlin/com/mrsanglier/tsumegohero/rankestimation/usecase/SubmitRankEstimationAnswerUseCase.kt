package com.mrsanglier.tsumegohero.rankestimation.usecase

import com.mrsanglier.tsumegohero.core.error.THAppError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.rankestimation.RankEstimationConfig
import com.mrsanglier.tsumegohero.rankestimation.delegate.ComputeFinalLevelDelegate
import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SubmitRankEstimationAnswerUseCase(
    private val userRepository: UserRepository,
    private val attemptRepository: AttemptRepository,
    private val tsumegoRepository: TsumegoRepository,
    private val computeFinalLevelDelegate: ComputeFinalLevelDelegate,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        tsumegoId: String,
        resolutionTimeMs: Long,
        gameMode: GameMode,
        result: Attempt.Result,
    ): THResult<Unit> = THResult.catchResult {
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
        computeFinalLevelDelegate(attempts)

        val rankAttempts = attempts.filter { it.first == tsumego.rank }.map { it.second }

        if (rankAttempts.size < RankEstimationConfig.BLOCK_SIZE) return@catchResult

        val averageResTime = rankAttempts.map { it.resolutionTimeMs }.average()
        val successRate = rankAttempts.count { it.result == Attempt.Result.Success }
        val difficultPassed = averageResTime <= RankEstimationConfig.DIFFICULT_TIME.inWholeMilliseconds
            && successRate >= RankEstimationConfig.SUCCESS_THRESHOLD

        val isMaxRank = Rank.entries.indexOf(tsumego.rank) == Rank.entries.lastIndex

        if (!difficultPassed || isMaxRank) {
            val level = computeFinalLevelDelegate(attempts)
            userRepository.upsert(user.copy(level = level))
            attemptRepository.deleteRankEstimationAttempts()
        }
    }
}
