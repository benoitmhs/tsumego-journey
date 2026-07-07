package com.mrsanglier.tsumegohero.core.extension

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

fun Instant.daysUntil(
    other: Instant,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Int {
    return this.daysUntil(other, timeZone)
}

/** @return the current local day as an [Instant] interval, start inclusive and end exclusive */
fun todayInterval(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): OpenEndRange<Instant> {
    val today = Clock.System.todayIn(timeZone)
    val startOfDay = today.atStartOfDayIn(timeZone)
    val endOfDay = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
    return startOfDay..<endOfDay
}
