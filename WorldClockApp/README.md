# ChronoSphere

**A beautiful, real-time world clock application for Android with home screen widget support.**

![License](https://img.shields.io/badge/license-GPL%20v3-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)
![Version](https://img.shields.io/badge/version-1.0-orange.svg)

## Features

- 🌍 **5 Global Cities**: Real-time clocks for New York, London, Tokyo, Sydney, and Dubai
- ⏱️ **Live Updates**: Time updates every second for accurate timekeeping
- 📱 **Home Screen Widget**: Beautiful widget showing all 5 city times on your home screen
- 🎨 **Modern Design**: Clean, card-based Material Design interface
- 🔋 **Efficient**: Optimized battery usage with smart update mechanisms
- 🔒 **Privacy-First**: No ads, no tracking, no internet permissions required
- 🌐 **Open Source**: Free and open source under GPL v3

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
Download the latest APK from the [Releases](https://github.com/Applify/ChronoSphere/releases) page.

## Building from Source

### Requirements
- Android Studio Arctic Fox or later
- JDK 17 or later
- Android SDK 34

### Build Instructions

1. Clone the repository:
```bash
git clone https://github.com/Applify/ChronoSphere.git
cd ChronoSphere
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

4. **Update Contact Info**:
   - Edit `fastlane/metadata/android/contact.txt` with your GitHub URL
   - Edit `fastlane/metadata/android/en-US/email.txt` with your email

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

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

## Contact

Project Link: [https://github.com/Applify/ChronoSphere](https://github.com/Applify/ChronoSphere)

---

Made with ❤️ by Applify
