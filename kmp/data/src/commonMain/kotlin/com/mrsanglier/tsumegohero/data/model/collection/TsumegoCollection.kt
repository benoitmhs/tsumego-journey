package com.mrsanglier.tsumegohero.data.model.collection

import kotlin.time.Instant

data class TsumegoCollection(
    val id: String,
    val name: String,
    val createdAt: Instant,
)
