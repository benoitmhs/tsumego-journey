package com.mrsanglier.tsumegohero.game.delegate

import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import com.mrsanglier.tsumegohero.data.model.user.Level
import com.mrsanglier.tsumegohero.domain.common.Config
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

interface UpdateLevelDelegate {
    fun updateLevel(level: Level, trainingMode: TrainingMode, lastAttempts: List<Attempt>): Level?
}

class UpdateLevelDelegateImpl : UpdateLevelDelegate {

    /**
     * @param lastAttempts the most recent Training attempts of the mode, newest first
     * @return the new level, null if the window is not decisive
     */
    override fun updateLevel(
        level: Level,
        trainingMode: TrainingMode,
        lastAttempts: List<Attempt>,
    ): Level? {
        val currentRank = level.rankFor(trainingMode)
        val window = lastAttempts.take(Config.LevelProgression.WINDOW_SIZE)

        // A full window at the current rank only: any attempt at another rank means the tier
        // moved recently, the performance measurement restarts from there
        val isWindowDecisive = window.size == Config.LevelProgression.WINDOW_SIZE
            && window.all { it.rank == currentRank }
        if (!isWindowDecisive) return null

        val timeLimitMs = trainingMode.timeLimit.inWholeMilliseconds
        val successes = window.count {
            it.result == Attempt.Result.Success && it.resolutionTimeMs <= timeLimitMs
        }
        val failures = window.count { it.result != Attempt.Result.Success }

        val successRate = successes.toFloat() / window.size
        val failureRate = failures.toFloat() / window.size
        val targetRankIndex = when {
            successRate >= Config.LevelProgression.PROMOTION_SUCCESS_RATE -> currentRank.index + 1
            failureRate >= Config.LevelProgression.DEMOTION_FAILURE_RATE -> currentRank.index - 1
            else -> return null
        }

        return level.withRankFor(trainingMode, targetRankIndex).takeIf { it != level }
    }
}

private fun Level.rankFor(trainingMode: TrainingMode): Rank = when (trainingMode) {
    TrainingMode.Flash -> flashRank
    TrainingMode.Classical -> classicalRank
    TrainingMode.Difficult -> difficultRank
}

// The estimation tier gaps are preserved: flash stays at least 1 kyu below classical,
// difficult at least 1 kyu above, classical pushes its neighbors when it moves
private fun Level.withRankFor(trainingMode: TrainingMode, targetRankIndex: Int): Level {
    val lastIndex = Rank.entries.lastIndex
    return when (trainingMode) {
        TrainingMode.Flash -> {
            val flashMax = max(0, classicalRank.index - Config.LevelProgression.TIER_MIN_GAP)
            copy(flashRank = rankAt(targetRankIndex.coerceIn(0, flashMax)))
        }

        TrainingMode.Classical -> {
            val classicalIndex = targetRankIndex.coerceIn(0, lastIndex)
            val flashMax = max(0, classicalIndex - Config.LevelProgression.TIER_MIN_GAP)
            val difficultMin = min(lastIndex, classicalIndex + Config.LevelProgression.TIER_MIN_GAP)
            copy(
                classicalRank = rankAt(classicalIndex),
                flashRank = rankAt(min(flashRank.index, flashMax)),
                difficultRank = rankAt(max(difficultRank.index, difficultMin)),
            )
        }

        TrainingMode.Difficult -> {
            val difficultMin = min(lastIndex, classicalRank.index + Config.LevelProgression.TIER_MIN_GAP)
            copy(difficultRank = rankAt(targetRankIndex.coerceIn(difficultMin, lastIndex)))
        }
    }
}

private val TrainingMode.timeLimit: Duration
    get() = when (this) {
        TrainingMode.Flash -> Config.Game.FLASH_TIME
        TrainingMode.Classical -> Config.Game.CLASSICAL_TIME
        TrainingMode.Difficult -> Config.Game.DIFFICULT_TIME
    }

private fun rankAt(index: Int): Rank = Rank.entries[index.coerceIn(0, Rank.entries.lastIndex)]

private val Rank.index: Int get() = Rank.entries.indexOf(this)
