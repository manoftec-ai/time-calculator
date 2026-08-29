package com.example.timecalc

object TimeCalculator {
    private const val SECOND: Double = 1.0
    private const val MINUTE: Double = 60.0 * SECOND
    private const val HOUR: Double = 60.0 * MINUTE
    private const val DAY: Double = 24.0 * HOUR
    private const val WEEK: Double = 7.0 * DAY
    private const val MONTH: Double = 30.44 * DAY  // Average month length
    private const val YEAR: Double = 365.25 * DAY  // Average year length

    fun formatFromSeconds(totalSeconds: Double, unitChars: List<String>): String {
        if (unitChars.isEmpty()) return "${totalSeconds.format()}s"

        val unitOrder = listOf("y", "mo", "w", "d", "h", "m", "s")
        val filteredUnits = unitOrder.filter { unitChars.contains(it) }

        if (filteredUnits.isEmpty()) return "${totalSeconds.format()}s"

        val values = mutableListOf<String>()
        var remaining = totalSeconds

        filteredUnits.forEach { unit ->
            when (unit) {
                "y" -> {
                    val years = remaining.div(YEAR).toInt()
                    if (years > 0 || values.isNotEmpty()) {
                        values.add("${years}y")
                        remaining -= years * YEAR
                    }
                }
                "mo" -> {
                    val months = remaining.div(MONTH).toInt()
                    if (months > 0 || values.isNotEmpty()) {
                        values.add("${months}mo")
                        remaining -= months * MONTH
                    }
                }
                "w" -> {
                    val weeks = remaining.div(WEEK).toInt()
                    if (weeks > 0 || values.isNotEmpty()) {
                        values.add("${weeks}w")
                        remaining -= weeks * WEEK
                    }
                }
                "d" -> {
                    val days = remaining.div(DAY).toInt()
                    if (days > 0 || values.isNotEmpty()) {
                        values.add("${days}d")
                        remaining -= days * DAY
                    }
                }
                "h" -> {
                    val hours = remaining.div(HOUR).toInt()
                    if (hours > 0 || values.isNotEmpty()) {
                        values.add("${hours}h")
                        remaining -= hours * HOUR
                    }
                }
                "m" -> {
                    val minutes = remaining.div(MINUTE).toInt()
                    if (minutes > 0 || values.isNotEmpty()) {
                        values.add("${minutes}m")
                        remaining -= minutes * MINUTE
                    }
                }
                "s" -> {
                    val seconds = remaining.toInt()
                    values.add("${seconds}s")
                }
            }
        }

        return if (values.isEmpty()) "0s" else values.joinToString(" ")
    }

    fun convert(value: Double, fromUnit: String, toUnit: String): Double {
        val fromSeconds = value * when (fromUnit.lowercase()) {
            "second", "seconds", "s", "sec" -> SECOND
            "minute", "minutes", "min", "m" -> MINUTE
            "hour", "hours", "h" -> HOUR
            "day", "days", "d" -> DAY
            "week", "weeks", "w" -> WEEK
            "month", "months", "mo", "mon", "mons" -> MONTH
            "year", "years", "y", "yr", "yrs" -> YEAR
            else -> throw IllegalArgumentException("Unknown unit: $fromUnit")
        }

        return fromSeconds / when (toUnit.lowercase()) {
            "second", "seconds", "s", "sec" -> SECOND
            "minute", "minutes", "min", "m" -> MINUTE
            "hour", "hours", "h" -> HOUR
            "day", "days", "d" -> DAY
            "week", "weeks", "w" -> WEEK
            "month", "months", "mo", "mon", "mons" -> MONTH
            "year", "years", "y", "yr", "yrs" -> YEAR
            else -> throw IllegalArgumentException("Unknown unit: $toUnit")
        }
    }
}

data class TimeComponents(
    var years: Double = 0.0,
    var months: Double = 0.0,
    var weeks: Double = 0.0,
    var days: Double = 0.0,
    var hours: Double = 0.0,
    var minutes: Double = 0.0,
    var seconds: Double = 0.0
) {
    fun toSeconds(): Double {
        val yearSeconds = 365.25 * 24 * 3600
        val monthSeconds = 30.44 * 24 * 3600
        val weekSeconds = 7 * 24 * 3600
        val daySeconds = 24 * 3600
        val hourSeconds = 3600
        val minuteSeconds = 60

        return years * yearSeconds +
                months * monthSeconds +
                weeks * weekSeconds +
                days * daySeconds +
                hours * hourSeconds +
                minutes * minuteSeconds +
                seconds
    }

    fun multiply(factor: Double): String {
        val totalSec = toSeconds() * factor
        return TimeCalculator.formatFromSeconds(totalSec, listOf("y", "mo", "w", "d", "h", "m", "s"))
    }

    fun divide(divisor: Double): String {
        if (divisor == 0.0) throw ArithmeticException("Division by zero")
        val totalSec = toSeconds() / divisor
        return TimeCalculator.formatFromSeconds(totalSec, listOf("y", "mo", "w", "d", "h", "m", "s"))
    }
}

fun Double.format(): String = if (this == this.toLong().toDouble()) {
    this.toLong().toString()
} else {
    String.format("%.2f", this)
}
