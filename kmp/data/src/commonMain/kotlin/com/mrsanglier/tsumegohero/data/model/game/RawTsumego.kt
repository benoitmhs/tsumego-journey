package com.mrsanglier.tsumegohero.data.model.game

import kotlin.time.Instant

data class RawTsumego(
    val id: String,
    val name: String,
    val data: String,
    val rank: Rank,
    val updatedAt: Instant,
)