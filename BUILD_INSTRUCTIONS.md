# Time Calculator - Build & Install Instructions

## Quick Setup (Any Machine)

### Option 1: Android Studio (Recommended)
1. Install Android Studio from https://developer.android.com/studio
2. Open project: `/root/work/Agent Task Prompt/departments/app-developer/working/time-calculator/`
3. Wait for Gradle sync to complete
4. Click "Run ▶" to build and install on connected device

### Option 2: Command Line Build
```bash
cd /path/to/time-calculator
./gradlew assembleDebug
# Install APK manually on your device
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Requirements
- Android SDK API 34 or newer
- Gradle 8.2+ (included via wrapper)
- Java 17+ (included with Android Studio)

## Debug APK Build Steps
1. Enable "Developer Options" on your phone
2. Enable "USB Debugging"
3. Connect phone via USB
4. Run `./gradlew assembleDebug` from project root
5. Install the APK: `adb install app/build/outputs/apk/debug/app-debug.apk`

## Release (Play Store) Build
1. Generate keystore:
   ```bash
   keytool -genkey -v -keystore my-key.keystore -alias timecalc -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Add signing config to `app/build.gradle`
3. Run: `./gradlew assembleRelease`

## Project Size
- Source: ~5 KB
- APK: ~300-400 KB (no external dependencies)

