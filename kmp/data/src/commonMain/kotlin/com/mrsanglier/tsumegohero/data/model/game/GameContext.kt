package com.mrsanglier.tsumegohero.data.model.game

sealed interface GameContext {
    data object RankEstimation : GameContext
    data class Training(val trainingMode: TrainingMode) : GameContext
}