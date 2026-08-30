package com.example.timecalc

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import android.app.Activity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : Activity() {
    private lateinit var inputField: EditText
    private lateinit var resultDisplay: TextView
    private lateinit var modeToggle: TextView
    private lateinit var btnCalculate: Button
    private lateinit var operationButtons: LinearLayout
    private lateinit var historyContainer: LinearLayout
    private val calculatorBrain = CalculatorBrain()
    private var currentOperation: String = "+"
    private var isCalculatorMode = true
    private val historyList = ArrayList<String>()
    private val timeUnits = listOf("Seconds", "Minutes", "Hours", "Days", "Weeks", "Months", "Years")
    private var fromSpinner: Spinner? = null
    private var toSpinner: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        inputField = findViewById(R.id.inputField)
        resultDisplay = findViewById(R.id.resultDisplay)
        modeToggle = findViewById(R.id.modeToggleText)
        historyContainer = findViewById(R.id.historyContainer)
        operationButtons = findViewById(R.id.operationButtons)
        btnCalculate = findViewById(R.id.btnCalculate)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnSubtract = findViewById<Button>(R.id.btnSubtract)
        val btnMultiply = findViewById<Button>(R.id.btnMultiply)
        val btnDivide = findViewById<Button>(R.id.btnDivide)

        btnAdd.setOnClickListener { setOperation("+") }
        btnSubtract.setOnClickListener { setOperation("-") }
        btnMultiply.setOnClickListener { setOperation("*") }
        btnDivide.setOnClickListener { setOperation("/") }
        btnCalculate.setOnClickListener { calculate() }
        modeToggle.setOnClickListener { toggleMode() }
    }

    private fun setOperation(op: String) {
        currentOperation = op
        val hintText = when (op) {
            "+" -> "e.g. 2y 3m 9d + 5m 3d"
            "-" -> "e.g. 2y 3m 9d - 5m 3d"
            "*" -> "e.g. 2y 3m 9d * 3"
            "/" -> "e.g. 2y 3m 9d / 3"
            else -> "e.g. 2y 3m 9d + 5m 3d"
        }
        inputField.hint = hintText
    }

    private fun toggleMode() {
        isCalculatorMode = !isCalculatorMode
        if (isCalculatorMode) {
            modeToggle.text = "Calculator"
            setupCalculatorMode()
        } else {
            modeToggle.text = "Converter"
            setupConverterMode()
        }
    }

    private fun setupCalculatorMode() {
        // Remove converter panel if present (keep the 4 operation buttons)
        while (operationButtons.childCount > 4) {
            operationButtons.removeViewAt(operationButtons.childCount - 1)
        }
        fromSpinner = null
        toSpinner = null
        // Show operation buttons
        findViewById<Button>(R.id.btnAdd).visibility = View.VISIBLE
        findViewById<Button>(R.id.btnSubtract).visibility = View.VISIBLE
        findViewById<Button>(R.id.btnMultiply).visibility = View.VISIBLE
        findViewById<Button>(R.id.btnDivide).visibility = View.VISIBLE
        btnCalculate.text = "Calculate"
        inputField.hint = "e.g. 2y 3m 9d + 5m 3d"
        historyContainer.visibility = View.VISIBLE
    }

    private fun setupConverterMode() {
        // Hide operation buttons and show converter UI
        findViewById<Button>(R.id.btnAdd).visibility = View.GONE
        findViewById<Button>(R.id.btnSubtract).visibility = View.GONE
        findViewById<Button>(R.id.btnMultiply).visibility = View.GONE
        findViewById<Button>(R.id.btnDivide).visibility = View.GONE
        btnCalculate.text = "Convert"
        inputField.hint = "e.g. 2.5  (From/To set above)"
        historyContainer.visibility = View.GONE

        // Add converter UI alongside existing buttons (buttons are GONE so they stay attached)
        createConverterInputs()
    }

    private fun createConverterInputs() {
        // Create From/To unit selectors
        val from = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                timeUnits
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                this.adapter = adapter
            }
        }
        fromSpinner = from

        val to = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                timeUnits
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                this.adapter = adapter
            }
        }
        toSpinner = to

        val converterPanel = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 16, 0, 16)
            addView(from, LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ))
            val arrow = TextView(this@MainActivity).apply {
                text = " → "
                gravity = Gravity.CENTER
            }
            addView(arrow, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ))
            addView(to, LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ))
        }
        
        operationButtons.addView(converterPanel, operationButtons.childCount)
    }

    private fun calculate() {
        val input = inputField.text.toString().trim()
        if (input.isEmpty()) {
            Toast.makeText(this, "Enter an expression", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val result = if (isCalculatorMode) {
                calculatorBrain.evaluate(input, currentOperation)
            } else {
                parseConverterInput(input)
            }

            resultDisplay.text = "= $result"
            if (isCalculatorMode) addToHistory(input, result)
        } catch (e: Exception) {
            resultDisplay.text = "= Error: ${e.message}"
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun parseConverterInput(input: String): String {
        // If both spinners are set, use them directly and treat the input as a number.
        val from = fromSpinner?.selectedItem?.toString()
        val to = toSpinner?.selectedItem?.toString()
        if (from != null && to != null && !input.contains(" to ", ignoreCase = true)) {
            val value = input.replace(",", "").trim().toDoubleOrNull()
                ?: throw IllegalArgumentException("Enter a number to convert")
            val result = TimeCalculator.convert(value, from, to)
            return "${result.format()} $to"
        }

        // Fallback: parse format like "2.5 days to hours" or "2y 3m to days"
        val parts = input.split(" to ", ignoreCase = true)
        if (parts.size != 2) {
            throw IllegalArgumentException("Use format: 'value unit to target_unit'")
        }
        
        val fromValue = parts[0].trim()
        val targetUnit = parts[1].trim()
        
        val fromTime = calculatorBrain.parseTimeExpression(fromValue)
        val baseSeconds = fromTime.toSeconds()
        
        return TimeCalculator.formatFromSeconds(baseSeconds, getTimeUnitStrings(targetUnit))
    }

    private fun getTimeUnitStrings(target: String): List<String> {
        return when {
            target.contains("year", ignoreCase = true) -> listOf("y")
            target.contains("month", ignoreCase = true) -> listOf("mo")
            target.contains("week", ignoreCase = true) -> listOf("w")
            target.contains("day", ignoreCase = true) -> listOf("d")
            target.contains("hour", ignoreCase = true) -> listOf("h")
            target.contains("min", ignoreCase = true) -> listOf("m")
            target.contains("sec", ignoreCase = true) -> listOf("s")
            else -> throw IllegalArgumentException("Unknown target unit: $target")
        }
    }

    private fun addToHistory(input: String, result: String) {
        val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val entry = "$timeStamp | $input = $result"
        historyList.add(0, entry) // Add to top
        
        // Limit to 10 entries
        while (historyList.size > 10) {
            historyList.removeAt(historyList.size - 1)
        }
        
        renderHistory()
    }

    private fun renderHistory() {
        historyContainer.removeAllViews()
        
        historyList.forEach { entry ->
            val historyItem = TextView(this).apply {
                text = entry
                textSize = 14f
                setPadding(8, 12, 8, 12)
            }
            historyContainer.addView(historyItem)
        }
    }
}
