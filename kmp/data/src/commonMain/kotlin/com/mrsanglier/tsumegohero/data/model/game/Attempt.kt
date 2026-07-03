package com.mrsanglier.tsumegohero.data.model.game

import kotlin.time.Instant

class Attempt(
    val id: String,
    val userId: String,
    val tsumegoId: String,
    val success: Boolean,
    val mode: Mode,
    val date: Instant,
) {
    enum class Mode {
        Standard, Ghost;
    }
}