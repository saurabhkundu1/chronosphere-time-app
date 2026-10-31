# ChronoSphere

An offline Android world clock for saved cities, meeting times, and a home-screen widget.

## Features

- Search and save cities from a curated set of time zones.
- Compare local times and dates, including recommended meeting windows.
- Add a suggested meeting time to the calendar with participating time zones in the event description.
- Show up to five saved cities in the home-screen widget.
- Keep the selected cities on device; the app does not request internet access.

## Build

- JDK 17
- Android SDK 37
- Minimum Android version: 7 (API 24)

Open this folder in Android Studio, or run `gradlew.bat assembleDebug` on Windows and `./gradlew assembleDebug` on macOS/Linux. The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

The current source version is `1.2.0` (`versionCode` 3). Push a `vMAJOR.MINOR.PATCH` tag to build the signed APK, publish a GitHub release, and dispatch it to Applify for the Droidify-compatible repository. Configure the release signing and Applify dispatch secrets in the repository before tagging.

## License and support

ChronoSphere is licensed under [Apache License 2.0](LICENSE). Use the [GitHub issue tracker](https://github.com/saurabhkundu1/chronosphere-time-app/issues) for support.
