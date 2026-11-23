# ChronoSphere

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B%20(API%2024%2B)-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile%20SDK-37-brightgreen.svg)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-37-brightgreen.svg)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-ViewModel%20%2B%20StateFlow-purple.svg)](https://developer.android.com/topic/architecture)
[![Deploy F-Droid Repo](https://github.com/saurabhkundu1/Chronosphere/actions/workflows/deploy-fdroid-pages.yml/badge.svg)](https://github.com/saurabhkundu1/Chronosphere/actions/workflows/deploy-fdroid-pages.yml)

> **Precision global timekeeping for distributed teams** — An elegant, offline Android world clock application for tracking international cities, planning overlap meetings across time zones, and keeping up to five live clocks on your home screen.

---

## 🌍 Overview

**ChronoSphere** is engineered for remote professionals, international travelers, and distributed engineering teams who need instantaneous clarity across global time zones:
- **World City Tracking:** Search and pin cities across all global time zones with live relative offsets.
- **Overlap Meeting Planner:** Calculate mutually viable working hours between distributed team members and export suggested meetings directly to the Android System Calendar.
- **Home-Screen Widget:** Display up to five saved clocks directly on your home screen with dynamic time-offset chips.
- **100% Offline & Private:** Zero internet permission required; all preferences and cities remain strictly on your device.

---

## ✨ Features

### 1. Multi-City World Clock & Real-Time Offsets
- Curated worldwide database of time zones with instant fuzzy search.
- Live relative time difference indicator (e.g., `+5h 30m ahead` or `-3h behind`).
- Dynamic local date and timezone abbreviation badges.
- Automatically respects system 12-hour (AM/PM) and 24-hour time preferences.

### 2. Smart Overlap Meeting Planner
- Algorithmically determines overlapping working hours across all saved cities (`WorkWindow.kt`).
- Configurable workday schedule (e.g., Monday–Friday or custom workdays) and work hours (default: `09:00–17:00` local time).
- Supports 15-, 30-, and 60-minute meeting durations.
- One-tap calendar event generation pre-populating event titles and local meeting times for each participant's city in the event description.
- Easy text sharing of proposed meeting schedules across chat and email.

### 3. Home-Screen AppWidget
- Glanceable Android AppWidget supporting up to 5 user-selected clocks (`WorldClockWidgetProvider.kt`).
- Updates on system time ticks, timezone transitions, and configuration changes.
- Responsive layout adapting to device launcher sizes and density configurations.

### 4. Reactive Architecture (ViewModel + StateFlow)
- Unidirectional data flow powered by AndroidX `ViewModel` and Kotlin Coroutines `StateFlow`.
- Lifecycle-aware ticker mechanism ensuring 0% background battery drain when the app is paused or in background.
- Clean separation of business logic (`TimeDifferenceFormatter`, `WorkWindow`) from UI presentation.

### 5. Absolute Privacy
- Zero network permissions (`android.permission.INTERNET` is neither requested nor required).
- Zero third-party trackers, analytics, or telemetry.
- On-device persistent storage.

---

## 🛠️ Architecture & Tech Stack

```
Chronosphere/
├── WorldClockApp/
│   ├── app/
│   │   ├── src/main/java/com/applify/chronosphere/
│   │   │   ├── MainActivity.kt               # Core UI, search, and clock management
│   │   │   ├── WorldClockViewModel.kt        # StateFlow reactive state holder
│   │   │   ├── WorldClockWidgetProvider.kt   # Home-screen widget provider
│   │   │   ├── WorkWindow.kt                 # Overlap calculation & meeting window engine
│   │   │   └── TimeDifferenceFormatter.kt    # Relative timezone delta formatting
│   │   ├── src/test/java/com/applify/chronosphere/
│   │   │   ├── TimeDifferenceFormatterTest.kt# Unit tests for relative time offsets
│   │   │   ├── WorkWindowTest.kt             # Overlap meeting calculation test suite
│   │   │   └── WorldClockStateTest.kt        # Reactive state & reducer test suite
│   │   └── build.gradle.kts                  # Module build configuration (SDK 37)
│   └── build.gradle.kts                      # Root Gradle configuration
├── LICENSE                                   # MIT License
└── README.md
```

### Key Technologies
- **Language:** Kotlin 2.x
- **Target SDK:** 37 (Android 15+)
- **Compile SDK:** 37
- **Minimum SDK:** 24 (Android 7.0 Nougat)
- **Architecture:** Android Architecture Components (ViewModel, StateFlow, Coroutines)
- **UI:** Material Design Components with responsive XML layouts and AppWidget framework
- **Code Quality:** Enforced `warningsAsErrors = true` across lint and Kotlin compilation

---

## 🚀 Building & Testing

### Prerequisites
- **JDK:** 17 or higher
- **Android SDK:** API 37 with Build Tools installed

### Command-Line Build

```powershell
# Navigate to the Android project root
cd WorldClockApp

# Assemble debug APK
.\gradlew.bat assembleDebug

# Run unit test suite
.\gradlew.bat testDebugUnitTest

# Run strict Android lint checks
.\gradlew.bat lintDebug
```

On Linux or macOS:
```bash
cd WorldClockApp
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

The compiled APK will be generated at:
`WorldClockApp/app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 Distribution & Releases

- **Applify Portal:** Hosted on the [Applify Open Source App Hub](https://saurabhkundu1.github.io/Applify/) (`com.applify.chronosphere`).
- **Droidify / F-Droid:** Compatible with Droidify, Neo Store, and F-Droid client apps by adding the repository:
  ```
  https://saurabhkundu1.github.io/Applify/fdroid/repo
  ```
- **GitHub Releases:** Tagged releases (`vMAJOR.MINOR.PATCH`) automatically trigger the release pipeline to sign the APK, create GitHub releases, and update the Applify distribution repository.

---

## 📄 License

ChronoSphere is released under the **[MIT License](LICENSE)**.

```
Copyright (c) 2026 Saurabh Kundu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

See [LICENSE](LICENSE) for the full license text.
