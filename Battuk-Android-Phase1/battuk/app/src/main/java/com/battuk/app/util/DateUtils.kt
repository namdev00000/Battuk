package com.battuk.app.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object DateUtils {

    private val zone: ZoneId = ZoneId.systemDefault()

    fun nowMillis(): Long = System.currentTimeMillis()

    fun toMillis(date: LocalDate): Long =
        date.atStartOfDay(zone).toInstant().toEpochMilli()

    fun endOfDayMillis(date: LocalDate): Long =
        date.atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli()

    fun toLocalDate(millis: Long): LocalDate =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()

    fun toLocalDateTime(millis: Long): LocalDateTime =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDateTime()

    fun startOfWeek(date: LocalDate): LocalDate =
        date.with(DayOfWeek.MONDAY)

    fun endOfWeek(date: LocalDate): LocalDate =
        startOfWeek(date).plusDays(6)

    fun startOfMonth(date: LocalDate): LocalDate = date.withDayOfMonth(1)

    fun endOfMonth(date: LocalDate): LocalDate = YearMonth.from(date).atEndOfMonth()

    fun startOfYear(date: LocalDate): LocalDate = date.withDayOfYear(1)

    fun endOfYear(date: LocalDate): LocalDate = date.withMonth(12).withDayOfMonth(31)

    fun formatDate(millis: Long, pattern: String = "d MMM yyyy"): String =
        toLocalDate(millis).format(DateTimeFormatter.ofPattern(pattern))

    fun formatDateMedium(millis: Long): String =
        toLocalDate(millis).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

    fun formatTime(millis: Long): String =
        toLocalDateTime(millis).format(DateTimeFormatter.ofPattern("h:mm a"))

    fun formatDayHeader(millis: Long): String =
        toLocalDate(millis).format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy"))
}
