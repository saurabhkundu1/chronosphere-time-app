# ChronoSphere

**An offline world clock for saving cities and checking their time from your home screen.**

![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)
![Version](https://img.shields.io/badge/version-1.0-orange.svg)

## Features

- 🌍 **Saved Cities**: Search and add cities from time zones around the world
- ⏱️ **Live Updates**: Time updates every second for accurate timekeeping
- 📱 **Home Screen Widget**: Widget shows up to five saved cities
- 🎨 **Modern Design**: Clean, card-based Material Design interface
- 🔋 **Efficient**: Optimized battery usage with smart update mechanisms
- 🔒 **Privacy-First**: No ads, no tracking, no internet permissions required
- 🌐 **Open Source**: Free and open source under Apache License 2.0

## Screenshots

The app features a clean interface with:
- Main activity showing 5 city cards with live clocks
- Home screen widget (2x4 size) displaying all city times
- Beautiful blue globe logo with clock hands and timezone markers

Screenshots will be available in the `fastlane/metadata/android/en-US/images/` directory.

## Download

### F-Droid
Coming soon to [F-Droid](https://f-droid.org/)

### GitHub Releases
Download the latest APK from the [Releases](https://github.com/saurabhkundu1/chronosphere-time-app/releases) page.

## Building from Source

### Requirements
- Android Studio Arctic Fox or later
- JDK 17 or later
- Android SDK 34

### Build Instructions

1. Clone the repository:
```bash
git clone https://github.com/saurabhkundu1/chronosphere-time-app.git
cd chronosphere-time-app/WorldClockApp
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build and run on your device or emulator

Or build via command line:
```bash
./gradlew assembleDebug
```

Install on your device:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Publishing to F-Droid

This project is configured for F-Droid submission using Fastlane metadata structure.

### Fastlane Structure

The app uses the standard F-Droid fastlane metadata structure:

```
fastlane/metadata/android/
├── en-US/
│   ├── title.txt
│   ├── short_description.txt
│   ├── full_description.txt
│   ├── changelogs/
│   │   └── 1.txt
│   └── images/
│       ├── icon.png
│       ├── featureGraphic.png
│       └── screenshots/
└── contact.txt
```

### Steps to Publish

1. **Prepare Metadata**: All metadata is already in place in the `fastlane/` directory

2. **Add Screenshots** (optional but recommended):
   - Take screenshots of your app
   - Place them in `fastlane/metadata/android/en-US/images/phoneScreenshots/`
   - Recommended size: 1080x1920 or similar

3. **Add Feature Graphic** (optional):
   - Create a 1024x500px feature graphic
   - Save as `fastlane/metadata/android/en-US/images/featureGraphic.png`

4. **Support**: Use the project's [GitHub issue tracker](https://github.com/saurabhkundu1/chronosphere-time-app/issues) for questions and bug reports.

5. **Build Release APK**:
```bash
./gradlew assembleRelease
```

6. **Submit to F-Droid**:
   - Fork the [F-Droid data repository](https://gitlab.com/fdroid/fdroiddata)
   - Add your app metadata following their guidelines
   - Submit a merge request

## Technology Stack

- **Language**: Kotlin
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with Coroutines
- **UI**: Material Design Components

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Roadmap

- [ ] Add more cities to choose from
- [ ] Customizable city selection
- [ ] Multiple widget sizes
- [ ] Dark/Light theme toggle
- [ ] Analog clock option
- [ ] Alarm support for different timezones

## Privacy Policy

ChronoSphere does not collect, store, or transmit any personal data. The app works entirely offline and does not require internet permissions.

## License

```
Copyright 2024 Applify

Licensed under the Apache License, Version 2.0. You may obtain a copy
of the License at https://www.apache.org/licenses/LICENSE-2.0.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

```

## Contact

Project Link: [https://github.com/saurabhkundu1/chronosphere-time-app](https://github.com/saurabhkundu1/chronosphere-time-app)

---

Made with ❤️ by Applify
