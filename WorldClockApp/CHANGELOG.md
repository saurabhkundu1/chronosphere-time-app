# Changelog

## Unreleased
- Upgrade the Gradle wrapper to 9.8.2 to satisfy Android lint's toolchain compatibility check.
- Upgrade AGP to 9.5.0 to satisfy the current Android lint toolchain check.
- Install the AGP-matched NDK in CI to strip packaged native libraries cleanly.
- Set the CI runner default Git branch to main to keep checkout output clean.
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
