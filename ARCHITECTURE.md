# Time Calculator App Architecture

## Overview
A lightweight Android time calculator built with Kotlin (no external dependencies).
Target APK size: < 500KB. Google Play compliant (no dangerous permissions).

## Architecture
Single Activity architecture — everything in MainActivity.kt
No Fragments, no ViewModel, no external libraries.

## File Structure
```
/app/src/main/
  ├── java/com/example/timecalc/MainActivity.kt    # All logic
  ├── res/
  │   ├── layout/activity_main.xml                 # UI layout
  │   ├── values/strings.xml, colors.xml, themes   # Resources
  │   └── mipmap-*                                 # Icons
  └── AndroidManifest.xml
```

## Core Components
1. **Parser** — Regex-based natural input parser
2. **TimeUnit** — Enum for all time units
3. **TimeCalculator** — Arithmetic operations with unit-aware output
4. **UnitConverter** — Bidirectional conversion between units
5. **DisplayManager** — Format output to match input units

## UI Flow
- Single screen with:
  - Input field (EditText) supporting natural time syntax
  - Operation selector (+, -, ×, ÷)
  - Mode toggle (Calculator / Converter)
  - Output display
  - History (optional, lightweight RecyclerView)

## Build Configuration
- Min SDK: 21 (covers 95%+ devices)
- Target SDK: 34 (latest stable)
- No permissions required
- No network access
- No analytics/external libs
