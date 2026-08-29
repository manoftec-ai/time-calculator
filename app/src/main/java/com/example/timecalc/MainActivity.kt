package com.example.timecalc

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
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
        inputField.hint = "e.g. 2.5 days to hours"
        historyContainer.visibility = View.GONE
        
        // Create converter UI inline
        operationButtons.removeAllViews()
        createConverterInputs()
    }

    private fun createConverterInputs() {
        // Create From/To unit selectors
        val fromSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                timeUnits
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                this.adapter = adapter
            }
        }
        
        val toSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                timeUnits
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                this.adapter = adapter
            }
        }
        
        val converterPanel = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 16, 0, 16)
            addView(fromSpinner, LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ))
            val arrow = TextView(this@MainActivity).apply {
                text = " → "
                gravity = Gravity.CENTER
            }
            addView(arrow, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ))
            addView(toSpinner, LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ))
        }
        
        operationButtons.addView(converterPanel)
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
            addToHistory(input, result)
        } catch (e: Exception) {
            resultDisplay.text = "= Error: ${e.message}"
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun parseConverterInput(input: String): String {
        // Parse format like: "2.5 days to hours" or "2y 3m to days"
        val parts = input.split(" to ", ignoreCase = true)
        if (parts.size != 2) {
            throw IllegalArgumentException("Use format: 'value unit to target_unit'")
        }
        
        val fromValue = parts[0].trim()
        val targetUnit = parts[1].trim()
        
        val fromTime = calculatorBrain.parseTimeExpression(fromValue)
        val baseSeconds = fromTime.toSeconds()
        
        return TimeCalculator.formatFromSeconds(baseSeconds, getTimeUnitChars(targetUnit))
    }

    private fun getTimeUnitChars(target: String): List<Char> {
        return when {
            target.contains("year", ignoreCase = true) -> listOf('y')
            target.contains("month", ignoreCase = true) -> listOf('mo')
            target.contains("week", ignoreCase = true) -> listOf('w')
            target.contains("day", ignoreCase = true) -> listOf('d')
            target.contains("hour", ignoreCase = true) -> listOf('h')
            target.contains("min", ignoreCase = true) -> listOf('m')
            target.contains("sec", ignoreCase = true) -> listOf('s')
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
