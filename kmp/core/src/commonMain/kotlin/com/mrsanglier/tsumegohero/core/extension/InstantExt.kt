package com.mrsanglier.tsumegohero.core.extension

import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlin.time.Instant

fun Instant.daysUntil(
    other: Instant,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Int {
    return this.daysUntil(other, timeZone)
}
