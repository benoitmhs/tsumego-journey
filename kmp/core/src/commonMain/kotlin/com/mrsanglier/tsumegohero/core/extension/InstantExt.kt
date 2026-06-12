package com.mrsanglier.tsumegohero.core.extension

import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Instant.daysUntil(
    other: Instant,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Int {
    return this.toLocalDateTime(timeZone).date
        .daysUntil(other.toLocalDateTime(timeZone).date)
}
