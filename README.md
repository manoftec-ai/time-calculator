# Time Calculator - Android App

A lightweight, minimalist time calculator for Android that handles natural time expressions.

## Features
- Add, subtract, multiply, or divide time expressions
- Input format: natural syntax like "2y 3m 9d + 5m 3d"
- Preserves output units based on input context
- Works with: years (y), months (mo), weeks (w), days (d), hours (h), minutes (m), seconds (s)
- Decimal support for fractional time values

## Examples
- `2y 3m 9d + 5m 3d` → `2y 8m 12d`
- `10w + 90w 3d` → `100w 3d` (stays in weeks/days)
- `2h 30m * 3` → `7h 30m`

## Building
1. Open in Android Studio
2. Build with Gradle
3. APK size target: < 500KB

## Permissions
None required - no network access, no data collection.

## Publishing
Google Play compliant. Uses standard adaptive icon resources.

## Architecture
- Single Activity (MainActivity.kt)
- CalculatorBrain.kt - parsing and calculation logic
- TimeCalculator.kt - unit conversion and formatting utilities
- No external dependencies
