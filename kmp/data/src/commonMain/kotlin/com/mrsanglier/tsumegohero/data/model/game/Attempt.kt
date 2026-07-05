package com.mrsanglier.tsumegohero.data.model.game

import kotlin.time.Instant

class Attempt(
    val id: String,
    val userId: String,
    val tsumegoId: String,
    val rank: Rank,
    val result: Result,
    val date: Instant,
    val context: GameContext,
    val resolutionTimeMs: Long,
) {
    enum class Result {
        Success, Failure, Skip;
    }
}