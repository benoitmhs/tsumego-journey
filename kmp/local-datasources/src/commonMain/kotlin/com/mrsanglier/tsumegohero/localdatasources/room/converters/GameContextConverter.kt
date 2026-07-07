package com.mrsanglier.tsumegohero.localdatasources.room.converters

import androidx.room.TypeConverter
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode

private const val RANK_ESTIMATION = "RANK_ESTIMATION"
private const val TRAINING = "TRAINING"

class GameContextConverter {

    @TypeConverter
    fun fromGameContext(context: GameContext): String {
        return when (context) {
            is GameContext.RankEstimation -> RANK_ESTIMATION
            is GameContext.Training -> "$TRAINING:${context.trainingMode.name}"
        }
    }

    @TypeConverter
    fun toGameContext(value: String): GameContext {
        return if (value == RANK_ESTIMATION) {
            GameContext.RankEstimation
        } else {
            val mode = value.substringAfter("$TRAINING:")
            GameContext.Training(TrainingMode.valueOf(mode))
        }
    }
}