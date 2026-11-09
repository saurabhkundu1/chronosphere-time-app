package com.applify.chronosphere

import java.util.Calendar
import java.util.TimeZone

internal val DEFAULT_WORK_DAYS = setOf(
    Calendar.MONDAY,
    Calendar.TUESDAY,
    Calendar.WEDNESDAY,
    Calendar.THURSDAY,
    Calendar.FRIDAY,
)

internal fun isWorkingTime(
    timestamp: Long,
    zone: TimeZone,
    workStart: Int,
    workEnd: Int,
    duration: Int,
    workDays: Set<Int> = DEFAULT_WORK_DAYS,
): Boolean {
    val local = Calendar.getInstance(zone).apply { timeInMillis = timestamp }
    val minutes = local.get(Calendar.HOUR_OF_DAY) * 60 + local.get(Calendar.MINUTE)
    val overnight = workEnd < workStart
    val inWindow = if (overnight) minutes >= workStart || minutes + duration <= workEnd
    else minutes >= workStart && minutes + duration <= workEnd
    if (!inWindow) return false
    if (overnight && minutes + duration <= workEnd) local.add(Calendar.DAY_OF_MONTH, -1)
    return local.get(Calendar.DAY_OF_WEEK) in workDays
}
