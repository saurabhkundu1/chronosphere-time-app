package com.applify.chronosphere

import java.util.TimeZone

object TimeDifferenceFormatter {
    fun minutesBetween(target: TimeZone, reference: TimeZone, timestamp: Long): Int =
        (target.getOffset(timestamp) - reference.getOffset(timestamp)) / 60_000
}
