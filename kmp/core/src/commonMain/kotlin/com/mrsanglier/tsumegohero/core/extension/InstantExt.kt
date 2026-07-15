package com.mrsanglier.tsumegohero.core.extension

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
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
): OpenEndRange<Instant> = dayInterval(daysAgo = 0, timeZone = timeZone)

/** @return the local day [daysAgo] days back as an [Instant] interval, start inclusive and end exclusive */
fun dayInterval(
    daysAgo: Int,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): OpenEndRange<Instant> {
    val day = Clock.System.todayIn(timeZone).minus(daysAgo, DateTimeUnit.DAY)
    val startOfDay = day.atStartOfDayIn(timeZone)
    val endOfDay = day.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
    return startOfDay..<endOfDay
}
