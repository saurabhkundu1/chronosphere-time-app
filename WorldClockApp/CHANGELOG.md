# Changelog

## [1.8.0] - Unreleased
- Choose which local weekdays count as workdays for meeting suggestions; keep Monday–Friday as the default and apply overnight windows to their shift-start day.

## [1.7.0] - Unreleased
- Allow the meeting planner work window to cross midnight and keep weekday checks tied to the shift start.

## [1.6.0] - Unreleased
- Choose a 15-, 30-, or 60-minute meeting length for suggestions, calendar events, and shared times.

## [1.5.0] - Unreleased
- Share a suggested meeting time with each city's local date and time.

## [1.4.0] - Unreleased
- Let people set the weekday work hours used by the offline meeting planner; keep 09:00–17:00 as the default.

## [1.3.0] - Unreleased
- Show each saved city's current time difference from the device in the app and widget, including half-hour and quarter-hour zones.
- Make widget clock formatting follow the device's 12- or 24-hour setting.
- Upgrade the Gradle wrapper to 9.8.2 to satisfy Android lint's toolchain compatibility check.
- Upgrade AGP to 9.5.0 to satisfy the current Android lint toolchain check.
- Pin Kotlin Gradle Plugin 2.4.21 to remove Gradle 9 deprecation calls.
- Install the AGP-matched NDK in CI to strip packaged native libraries cleanly.
- Set the CI runner default Git branch to main to keep checkout output clean.
- Serialize CI by branch and show Gradle deprecations with call stacks.
- Use the AndroidX SharedPreferences KTX editor to persist selected cities.
- Enable resource shrinking alongside release minification.

## 1.2.0 - 2026-10-31
- Update Android Gradle, Kotlin, Android SDK, and library versions to current stable releases.
- Migrate to AGP built-in Kotlin and target Android 17 (API 37).
- Add push-triggered CI for lint, unit tests, and debug builds.

## 1.1.0 - 2026-10-31
- Add recommended meeting times directly to the calendar with participating city time zones in the event description.
- Refine world clock screens, city cards, and widget presentation.
- Refresh theme colors and launcher icon resources.
- Update the Android Gradle plugin dependency.
