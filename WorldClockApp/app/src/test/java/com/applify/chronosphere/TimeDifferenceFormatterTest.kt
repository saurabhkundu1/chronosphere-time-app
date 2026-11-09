package com.applify.chronosphere

import java.time.Instant
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeDifferenceFormatterTest {
    private fun at(date: String) = Instant.parse(date).toEpochMilli()

    @Test fun preservesQuarterAndHalfHourOffsets() {
        val utc = TimeZone.getTimeZone("UTC")
        assertEquals(345, TimeDifferenceFormatter.minutesBetween(TimeZone.getTimeZone("Asia/Kathmandu"), utc, at("2026-01-01T00:00:00Z")))
        assertEquals(330, TimeDifferenceFormatter.minutesBetween(TimeZone.getTimeZone("Asia/Kolkata"), utc, at("2026-01-01T00:00:00Z")))
    }

    @Test fun usesOffsetAtTheRequestedInstantForDaylightSaving() {
        val newYork = TimeZone.getTimeZone("America/New_York")
        val utc = TimeZone.getTimeZone("UTC")
        assertEquals(-300, TimeDifferenceFormatter.minutesBetween(newYork, utc, at("2026-01-15T12:00:00Z")))
        assertEquals(-240, TimeDifferenceFormatter.minutesBetween(newYork, utc, at("2026-07-15T12:00:00Z")))
    }
}
