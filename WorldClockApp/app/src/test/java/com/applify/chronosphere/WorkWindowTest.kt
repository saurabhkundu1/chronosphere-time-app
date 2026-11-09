package com.applify.chronosphere

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Calendar
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkWindowTest {
    private val zone = TimeZone.getTimeZone("UTC")
    private fun at(value: String) = Date.from(LocalDateTime.parse(value).atZone(ZoneId.of("UTC")).toInstant()).time

    @Test fun defaultsToMondayThroughFriday() {
        assertEquals(
            setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY),
            DEFAULT_WORK_DAYS,
        )
        assertTrue(isWorkingTime(at("2026-11-03T10:00:00"), zone, 9 * 60, 17 * 60, 30))
        assertFalse(isWorkingTime(at("2026-11-07T10:00:00"), zone, 9 * 60, 17 * 60, 30))
    }

    @Test fun defaultOvernightShiftUsesStartWeekdayAcrossWeekend() {
        assertTrue(isWorkingTime(at("2026-11-06T23:30:00"), zone, 22 * 60, 6 * 60, 30))
        assertTrue(isWorkingTime(at("2026-11-07T05:30:00"), zone, 22 * 60, 6 * 60, 30))
        assertFalse(isWorkingTime(at("2026-11-08T05:30:00"), zone, 22 * 60, 6 * 60, 30))
    }

    @Test fun alternateWeekendAppliesToOvernightShiftStartAndEndBoundaries() {
        val workDays = setOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY)
        assertTrue(isWorkingTime(at("2026-11-06T05:30:00"), zone, 22 * 60, 6 * 60, 30, workDays))
        assertFalse(isWorkingTime(at("2026-11-06T05:45:00"), zone, 22 * 60, 6 * 60, 30, workDays))
        assertFalse(isWorkingTime(at("2026-11-06T23:30:00"), zone, 22 * 60, 6 * 60, 30, workDays))
        assertFalse(isWorkingTime(at("2026-11-07T05:30:00"), zone, 22 * 60, 6 * 60, 30, workDays))
        assertTrue(isWorkingTime(at("2026-11-09T05:30:00"), zone, 22 * 60, 6 * 60, 30, workDays))
    }

    @Test fun durationMustFitInsideWindow() {
        assertTrue(isWorkingTime(at("2026-11-03T05:30:00"), zone, 22 * 60, 6 * 60, 30))
        assertFalse(isWorkingTime(at("2026-11-03T05:45:00"), zone, 22 * 60, 6 * 60, 30))
    }
}
