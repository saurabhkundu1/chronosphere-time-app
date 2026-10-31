# ChronoSphere

ChronoSphere is an offline world clock for saving cities, comparing local times, and keeping up to five clocks on a home-screen widget.

- Search and save cities from world time zones.
- See local time, date, and time-zone name.
- Find overlapping work hours and add a suggested meeting time to your calendar.
- Follow the device's 12- or 24-hour clock preference.

## Build

Open [`WorldClockApp`](WorldClockApp/) in Android Studio, or run:

```powershell
cd WorldClockApp
./gradlew assembleDebug
```

On Windows, use `gradlew.bat assembleDebug`. The app supports Android 7 (API 24) and newer. The current source version is `1.2.0` (`versionCode` 3).

## Releases and Droidify

Push a `vMAJOR.MINOR.PATCH` tag to build and sign the APK, publish it on [GitHub Releases](https://github.com/saurabhkundu1/chronosphere-time-app/releases), and send it to [Applify](https://saurabhkundu1.github.io/Applify/) for the Droidify-compatible repository. Release signing uses the existing app key; configure the repository's `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`, and `APPLIFY_DISPATCH_TOKEN` secrets before tagging.

## Privacy and license

ChronoSphere works offline and does not request internet access. It is licensed under [Apache License 2.0](WorldClockApp/LICENSE). Report problems in the [issue tracker](https://github.com/saurabhkundu1/chronosphere-time-app/issues).
