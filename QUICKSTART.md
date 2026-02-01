# Quick Start Guide

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Gradle 8.2+

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/zaheerabbas7892034214-ai/PDF-Scanner-Clean-Document-Scan.git
cd PDF-Scanner-Clean-Document-Scan
```

### 2. Open in Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned repository
4. Click "OK"

### 3. Sync Gradle

Android Studio will automatically sync Gradle. If not:
1. Click "File" → "Sync Project with Gradle Files"
2. Wait for dependencies to download

### 4. Setup Google Play Billing (Required for In-App Purchases)

1. Create a product in Google Play Console:
   - Product ID: `scanner_pro_unlock`
   - Product Type: In-app product (one-time purchase)
   - Set your desired price

2. Update the app signing configuration if deploying to Play Store

### 5. Run the App

1. Connect an Android device or start an emulator (API 24+)
2. Click the "Run" button in Android Studio
3. Select your device

## Testing

### Testing Camera Features
- Grant camera permission when prompted
- Point camera at a document
- Tap the capture button (large circle at bottom)

### Testing Editing Features
- After capturing, adjust brightness and contrast sliders
- Tap "Crop" to enable crop overlay
- Drag to adjust crop area
- Add more pages with "Add Page" button

### Testing PDF Generation
- Tap "Generate PDF" after capturing pages
- PDF will be saved to Documents/PDFScanner folder
- Use "Share PDF" to share via other apps

### Testing Pro Features

Since billing requires a real Play Store environment, you can test Pro features by:

1. **Manual Pro Activation** (for testing only):
   - Use Android Studio's Device File Explorer
   - Navigate to `/data/data/com.zaheer.pdfscanner/shared_prefs/`
   - Edit `scanner_prefs.xml`
   - Set `<boolean name="is_pro" value="true" />`
   - Restart the app

2. **Testing Billing Flow**:
   - Use a test device added to your Google Play Console license testers
   - Configure test product in Play Console
   - Install app via Play Store internal testing track

## Build Variants

### Debug Build
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

Note: Release builds require signing configuration.

## Troubleshooting

### Issue: "Camera permission denied"
- Solution: Manually grant camera permission in device settings

### Issue: "Billing not working"
- Solution: Ensure app is signed and uploaded to Play Console internal testing
- Verify product ID matches: "scanner_pro_unlock"
- Check device is added as license tester

### Issue: "PDF not saving"
- Solution: Check Android version. On Android 9 and below, may need storage permissions

### Issue: "Gradle sync failed"
- Solution: Check internet connection
- Verify Gradle version compatibility
- Clear Gradle cache: `./gradlew clean`

## Key Files to Customize

### App Name
- `app/src/main/res/values/strings.xml` - Line 2: `<string name="app_name">`

### App Icon
- Replace files in `app/src/main/res/mipmap-*/` directories
- Use Android Asset Studio for icon generation

### Package Name
- Update in `app/build.gradle`: `applicationId`
- Update in `AndroidManifest.xml`: `package` attribute
- Refactor Kotlin package structure

### Colors/Theme
- `app/src/main/res/values/colors.xml` - Color definitions
- `app/src/main/java/com/zaheer/pdfscanner/ui/theme/Theme.kt` - Theme configuration

### Billing Product ID
- `app/src/main/java/com/zaheer/pdfscanner/billing/BillingManager.kt` - Line 19: `PRODUCT_ID_PRO`

## Development Workflow

### Adding New Features

1. **Update ViewModel State**
   - Modify `ScannerUiState.kt` if new state needed
   - Add functions to `ScannerViewModel.kt`

2. **Update UI**
   - Modify appropriate screen in `ui/screens/`
   - Collect state: `val uiState by viewModel.uiState.collectAsState()`

3. **Add Navigation**
   - Add new route in `ui/navigation/Screen.kt`
   - Add composable in `ui/navigation/NavigationGraph.kt`

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep functions small and focused

### Git Workflow

```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make changes and commit
git add .
git commit -m "Description of changes"

# Push to remote
git push origin feature/your-feature-name

# Create pull request on GitHub
```

## Production Checklist

Before releasing to Play Store:

- [ ] Replace app icons with professional designs
- [ ] Update app name and package name if needed
- [ ] Configure release signing in `app/build.gradle`
- [ ] Create keystore for app signing
- [ ] Test on multiple devices (different Android versions)
- [ ] Test billing with Play Console internal testing
- [ ] Update privacy policy if collecting user data
- [ ] Test ProGuard/R8 with release build
- [ ] Prepare Play Store listing (screenshots, description)
- [ ] Set up in Google Play Console
- [ ] Upload APK/AAB to internal testing track
- [ ] Test thoroughly before production release

## Support

For issues or questions:
- Check `IMPLEMENTATION.md` for detailed documentation
- Review code comments in source files
- Consult Android Developer documentation
- Check CameraX and Jetpack Compose guides

## License

Copyright © 2024
