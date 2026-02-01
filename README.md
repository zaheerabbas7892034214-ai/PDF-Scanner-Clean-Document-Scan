# PDF Scanner – Clean Document Scan

An Android application for scanning documents and converting them to PDF format.

## Features

### Core Features
- **Camera Scanner**: Integrated CameraX for live preview and image capture
- **Document Editing**: Manual crop, brightness, and contrast adjustments with live preview
- **Multi-Page Support**: Add multiple scanned pages with thumbnails and reordering capability
- **PDF Generation**: Combine multiple pages into a single PDF using PdfDocument

### Free Version
- 5 scans per day limit
- Watermark applied to exported PDFs

### Pro Version (In-App Purchase)
- Unlimited scans
- No watermark on exported PDFs
- High-quality export mode
- Batch PDF merging support

## Technical Stack

- **Language**: Kotlin
- **Min SDK**: 24
- **Target SDK**: 34
- **Architecture**: MVVM (ViewModel + StateFlow)
- **UI**: Jetpack Compose + Material 3
- **Navigation**: Navigation Compose (Single Activity)
- **Camera**: CameraX
- **Billing**: Google Play Billing Library 7.0.0
- **Build System**: Groovy build.gradle

## Project Structure

```
app/
├── src/main/
│   ├── java/com/zaheer/pdfscanner/
│   │   ├── MainActivity.kt
│   │   ├── ui/
│   │   │   ├── screens/         # Camera, Preview, Result, Pro screens
│   │   │   ├── navigation/      # Navigation Compose setup
│   │   │   ├── viewmodel/       # ScannerViewModel and ScannerUiState
│   │   │   └── theme/           # Material 3 theme
│   │   ├── domain/              # PdfGenerator and business logic
│   │   ├── billing/             # BillingManager for in-app purchases
│   │   └── utils/               # FileUtils, PreferencesManager
│   ├── res/                     # Resources (layouts, strings, etc.)
│   └── AndroidManifest.xml
├── build.gradle                 # App-level build configuration
└── proguard-rules.pro          # ProGuard rules for CameraX and Compose
```

## Building

This is a standard Android project built with Gradle:

```bash
./gradlew build
```

## License

Copyright © 2024
