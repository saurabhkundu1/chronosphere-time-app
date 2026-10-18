# ChronoSphere

ChronoSphere is an Android world clock for saving cities and comparing their local times. Add the **ChronoSphere** widget to your home screen to keep up to five saved clocks visible at a glance.

## Features

- Search and save cities from a curated list of world time zones.
- View each city's local time, date, and time-zone name.
- Remove saved cities at any time; the selection persists on the device.
- Use the home-screen widget to show the first five saved cities.
- Works offline and follows the device's 12-hour or 24-hour time preference.

## Build

Open [`WorldClockApp`](WorldClockApp/) in Android Studio, or run the Gradle wrapper:

```bash
cd WorldClockApp
./gradlew assembleDebug
```

On Windows, use `gradlew.bat assembleDebug`. The app requires Android 7.0 (API 24) or later.

## Release

The initial release is version `1.0.0` (`versionCode` 1). Release APK signing keys are kept outside this source repository.

## License and support

The app is licensed under [Apache License 2.0](WorldClockApp/LICENSE). For support, use the [GitHub issue tracker](https://github.com/saurabhkundu1/chronosphere-time-app/issues).
