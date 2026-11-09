# ChronoSphere

An offline Android world clock for saved cities, meeting times, and a home-screen widget.

## Features

- Search and save cities from a curated set of time zones.
- Compare local times and dates, including how many hours and minutes each saved city is ahead of or behind you.
- Add a suggested meeting time to the calendar with participating time zones in the event description.
- Choose a 15-, 30-, or 60-minute meeting length; suggestions and calendar events use the selected length.
- Share a suggested meeting time with each city's local date and time.
- Show up to five saved cities in the home-screen widget.
- See each city's current time difference from your device in the widget; widget clocks follow the device's 12- or 24-hour setting.
- Set the weekday work-hour window used by meeting suggestions; the default is 09:00–17:00 in each city's local time.
- Choose which local weekdays count as workdays; Monday–Friday is the default, and overnight windows use the shift-start day. Preferences and saved cities stay on device; the app does not request internet access.

## Build

- JDK 17
- Android SDK 37
- Minimum Android version: 7 (API 24)

Open this folder in Android Studio, or run `gradlew.bat assembleDebug` on Windows and `./gradlew assembleDebug` on macOS/Linux. The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

The current source candidate is `1.8.0` (`versionCode` 9), covering the local 1.3.0–1.8.0 improvements. Push a `vMAJOR.MINOR.PATCH` tag to build the signed APK, publish a GitHub release, and dispatch it to Applify for the Droidify-compatible repository. Configure the release signing and Applify dispatch secrets in the repository before tagging.

## License and support

ChronoSphere is licensed under [Apache License 2.0](LICENSE). Use the [GitHub issue tracker](https://github.com/saurabhkundu1/chronosphere-time-app/issues) for support.
