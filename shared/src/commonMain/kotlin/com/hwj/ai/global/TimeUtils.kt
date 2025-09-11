package com.hwj.ai.global

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

object TimeUtils {

    //（UTC时间戳，精确到纳秒）
    fun getCurrentInstant(): Instant = Clock.System.now()

    //"2024-10-01 14:30:00"
    fun getCurrentLocalDateTime(): LocalDateTime = getCurrentInstant().toLocalDateTime(
        TimeZone.currentSystemDefault()
    )

    //"2024-10-01"
    fun getCurrentLocalDate(): LocalDate = Clock.System.todayIn(
        TimeZone.currentSystemDefault()
    )

    //"14:30:00"
    fun getCurrentLocalTime(): LocalTime = getCurrentLocalDateTime().time

    fun getTimeMills(instant: Instant = getCurrentInstant()): Long {
        return instant.toEpochMilliseconds()
    }
}