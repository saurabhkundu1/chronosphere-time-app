# Applify World Clock

A beautiful, real-time world clock application for Android with home screen widget support.

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)

## Features

- 🕐 Real-time clock display for 5 global cities (New York, London, Tokyo, Sydney, Dubai)
- 📱 Home screen widget support
- 🎨 Beautiful Material Design 3 UI
- ⚡ Updates every second
- 🔒 Privacy-focused: no ads, no tracking
- 🌍 Open source and free

## Screenshots

Screenshots will be available in the `fastlane/metadata/android/en-US/images/` directory.

## Building

### Requirements
- Android Studio Arctic Fox or later
- JDK 17 or later
- Android SDK 34

### Build Instructions

1. Clone the repository:
```bash
git clone https://github.com/yourusername/Applify.git
cd Applify
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build and run on your device or emulator

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

## License

```
Copyright 2024 Your Name

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
