package com.example.timecalc

import java.util.regex.Pattern

class CalculatorBrain {
    fun evaluate(input: String, operation: String): String {
        return when (operation) {
            "+", "-" -> handleArithmetic(input, operation)
            "*", "/" -> handleScalar(input, operation)
            else -> throw IllegalArgumentException("Unknown operation: $operation")
        }
    }

    fun parseTimeExpression(expr: String): TimeComponents {
        val components = parseTimeComponents(expr)
        return TimeComponents(
            years = components["y"] ?: 0.0,
            months = components["mo"] ?: 0.0,
            weeks = components["w"] ?: 0.0,
            days = components["d"] ?: 0.0,
            hours = components["h"] ?: 0.0,
            minutes = components["m"] ?: 0.0,
            seconds = components["s"] ?: 0.0
        )
    }

    private fun handleArithmetic(input: String, op: String): String {
        val pattern = Pattern.compile("(.+?)\\s*([+\\-])\\s*(.+)")
        val matcher = pattern.matcher(input)
        
        if (!matcher.find()) throw IllegalArgumentException("Invalid expression format")

        val left = parseTimeExpression(matcher.group(1).trim())
        val right = parseTimeExpression(matcher.group(3).trim())

        val resultSeconds = if (op == "+") {
            left.toSeconds() + right.toSeconds()
        } else {
            val result = left.toSeconds() - right.toSeconds()
            if (result < 0) throw IllegalArgumentException("Negative result")
            result
        }

        return TimeCalculator.formatFromSeconds(resultSeconds, determineUnits(input))
    }

    private fun handleScalar(input: String, op: String): String {
        val pattern = Pattern.compile("(.+?)\\s*([*\\/])\\s*([\\d.]+)")
        val matcher = pattern.matcher(input)

        if (!matcher.find()) throw IllegalArgumentException("Invalid scalar expression")

        val timeExpr = matcher.group(1).trim()
        val scalar = matcher.group(3).toDouble()

        val time = parseTimeExpression(timeExpr)
        val resultSeconds = if (op == "*") {
            time.toSeconds() * scalar
        } else {
            if (scalar == 0.0) throw ArithmeticException("Division by zero")
            time.toSeconds() / scalar
        }

        return TimeCalculator.formatFromSeconds(resultSeconds, determineUnits(timeExpr))
    }

    fun determineUnits(expr: String): List<String> {
        val unitOrder = listOf("y", "mo", "w", "d", "h", "m", "s")
        val found = mutableSetOf<String>()

        val pattern = Pattern.compile("[\\d.]+\\s*([a-zA-Z]+)")
        val matcher = pattern.matcher(expr)

        while (matcher.find()) {
            val unitStr = matcher.group(1).lowercase()
            when {
                unitStr.startsWith("year") || unitStr == "y" -> found.add("y")
                unitStr.startsWith("month") || unitStr == "mo" -> found.add("mo")
                unitStr.startsWith("week") || unitStr == "w" -> found.add("w")
                unitStr.startsWith("day") || unitStr == "d" -> found.add("d")
                unitStr.startsWith("hour") || unitStr == "h" -> found.add("h")
                unitStr.startsWith("min") || unitStr == "m" -> found.add("m")
                unitStr.startsWith("sec") || unitStr == "s" -> found.add("s")
            }
        }

        return unitOrder.filter { found.contains(it) }
    }

    private fun parseTimeComponents(expr: String): Map<String, Double> {
        val result = mutableMapOf<String, Double>()
        val pattern = Pattern.compile("([\\d.]+)\\s*([a-zA-Z]+)")
        val matcher = pattern.matcher(expr)

        while (matcher.find()) {
            val value = matcher.group(1).toDouble()
            val unit = matcher.group(2).lowercase()

            val unitKey = when {
                unit.startsWith("year") || unit == "y" -> "y"
                unit.startsWith("month") || unit == "mo" -> "mo"
                unit.startsWith("week") || unit == "w" -> "w"
                unit.startsWith("day") || unit == "d" -> "d"
                unit.startsWith("hour") || unit == "h" -> "h"
                unit.startsWith("min") || unit == "m" -> "m"
                unit.startsWith("sec") || unit == "s" -> "s"
                else -> throw IllegalArgumentException("Unknown unit: $unit")
            }

            result[unitKey] = result.getOrDefault(unitKey, 0.0) + value
        }

        return result
    }
}
