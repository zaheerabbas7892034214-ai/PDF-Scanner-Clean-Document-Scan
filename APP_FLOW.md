# PDF Scanner App Flow

## User Journey Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            APP LAUNCH                                   │
│                         (MainActivity)                                  │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │ Initialize ViewModel  │
                    │  • Load Pro status    │
                    │  • Connect billing    │
                    │  • Load scan count    │
                    └───────────┬───────────┘
                                │
                                ▼
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                        SCREEN 1: CAMERA SCREEN                          ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │  TopBar: "Scan Document" | [Upgrade] or [Pro Active ✓]    │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │                                                             │        │
│  │              📷 LIVE CAMERA PREVIEW                         │        │
│  │                  (CameraX Preview)                          │        │
│  │                                                             │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │          Scans remaining today: X/5 (if free)              │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│                           [  ⚪ ]                                       │
│                        Capture Button                                   │
│                                                                          │
└────────────────────────────────┬─────────────────────────────────────────┘
                                 │ Tap Capture
                                 ▼
                    ┌──────────────────────────┐
                    │  Check scan limit        │
                    │  (if free user)          │
                    └──────┬───────────────────┘
                           │
                ┌──────────┴──────────┐
                │                     │
                ▼ Limit OK            ▼ Limit Reached
    ┌───────────────────┐    ┌─────────────────────┐
    │ Capture Image     │    │ Show Alert Dialog   │
    │ Add to ViewModel  │    │ "Upgrade to Pro?"   │
    └─────┬─────────────┘    └──────┬──────────────┘
          │                          │ Tap "Upgrade"
          │                          ▼
          │              ┌──────────────────────────┐
          │              │  Navigate to Pro Screen  │
          │              └──────────────────────────┘
          │
          ▼
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                       SCREEN 2: PREVIEW SCREEN                          ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │  TopBar: [←] "Edit Document" | [🗑️ Delete]                │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │                                                             │        │
│  │         📄 CAPTURED IMAGE PREVIEW                           │        │
│  │      (with crop overlay if enabled)                         │        │
│  │                                                             │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │  [Crop] [Reset]                                             │        │
│  │                                                             │        │
│  │  Brightness: ━━━━●━━━━━━ (-1.0 to +1.0)                   │        │
│  │                                                             │        │
│  │  Contrast:   ━━━━━━●━━━━ (0.5 to 2.0)                     │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │  [Page 1] [Page 2] [Page 3] ...  (thumbnail gallery)       │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│  ┌─────────────────────┐  ┌──────────────────────┐                    │
│  │  [+ Add Page]       │  │ [Generate PDF]       │                    │
│  └─────────────────────┘  └──────────────────────┘                    │
│                                                                          │
└────────────┬───────────────────────────┬─────────────────────────────────┘
             │ Add Page                  │ Generate PDF
             ▼                           ▼
    ┌──────────────────┐    ┌────────────────────────┐
    │ Navigate back to │    │ Call PDF Generator     │
    │ Camera Screen    │    │  • Apply adjustments   │
    │ (keep existing   │    │  • Add watermark (if   │
    │  pages)          │    │    free user)          │
    └──────────────────┘    │  • Save to MediaStore  │
                            └────────┬───────────────┘
                                     │
                                     ▼
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                       SCREEN 3: RESULT SCREEN                           ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │  TopBar: "PDF Generated"                                    │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│                      ┌───────────────────┐                              │
│                      │                   │                              │
│                      │    ✅ Large       │                              │
│                      │   Check Icon      │                              │
│                      │                   │                              │
│                      └───────────────────┘                              │
│                                                                          │
│              "PDF saved successfully!"                                  │
│                                                                          │
│        Watermark applied to free version (if applicable)                │
│                                                                          │
│                    3 page(s) saved                                      │
│                                                                          │
│  ┌─────────────────────┐  ┌──────────────────────┐                    │
│  │  [📤 Share PDF]     │  │ [New Scan]           │                    │
│  └─────────────────────┘  └──────────────────────┘                    │
│                                                                          │
└────────────┬───────────────────────────┬─────────────────────────────────┘
             │ Share                     │ New Scan
             ▼                           ▼
    ┌──────────────────┐    ┌────────────────────────┐
    │ Open Share Sheet │    │ Reset ViewModel        │
    │ (via Intent)     │    │ Navigate to Camera     │
    └──────────────────┘    └────────────────────────┘


                      ┌─────────────────────────────┐
                      │ Accessible from Camera Top │
                      │ Bar or limit reached alert │
                      └──────────┬──────────────────┘
                                 │
                                 ▼
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                        SCREEN 4: PRO SCREEN                             ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │  TopBar: [←] "Upgrade to Pro"                              │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │             ✅ Pro Active ✓                                │        │
│  │        (if already purchased, or...)                        │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │  Pro Features:                                              │        │
│  │                                                             │        │
│  │  ✅ Unlimited scans                                         │        │
│  │  ✅ No watermark                                            │        │
│  │  ✅ High-quality export                                     │        │
│  │  ✅ Batch PDF merging                                       │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│              Free: 5 scans per day                                      │
│                                                                          │
│                    $X.XX                                                │
│                One-time payment                                         │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │              [💎 Unlock Pro]                                │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │              [↻ Restore Purchases]                          │        │
│  └────────────────────────────────────────────────────────────┘        │
│                                                                          │
└────────────┬───────────────────────────┬─────────────────────────────────┘
             │ Unlock Pro                │ Restore
             ▼                           ▼
    ┌──────────────────────┐    ┌────────────────────────┐
    │ Launch Billing Flow  │    │ Query existing         │
    │  • Show Play Store   │    │ purchases              │
    │    purchase dialog   │    │  • Restore if found    │
    │  • Process purchase  │    │  • Update Pro status   │
    │  • Acknowledge       │    └────────────────────────┘
    │  • Update Pro status │
    │  • Save to prefs     │
    └──────────────────────┘
```

## Data Flow

```
┌─────────────────────────────────────────────────────────────┐
│                      User Interactions                      │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    UI Screens (Compose)                     │
│  • CameraScreen                                             │
│  • PreviewScreen                                            │
│  • ResultScreen                                             │
│  • ProScreen                                                │
└────────────────────────┬────────────────────────────────────┘
                         │ collectAsState()
                         │ viewModel functions
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   ScannerViewModel                          │
│  ┌───────────────────────────────────────────────────┐     │
│  │  StateFlow<ScannerUiState>                        │     │
│  │   • capturedPages                                 │     │
│  │   • isPro                                         │     │
│  │   • scansRemainingToday                          │     │
│  │   • generatedPdfUri                              │     │
│  │   • isProcessing                                 │     │
│  │   • errorMessage                                 │     │
│  └───────────────────────────────────────────────────┘     │
│                                                             │
│  Functions:                                                 │
│   • addScannedPage()                                        │
│   • updatePageBrightness()                                  │
│   • updatePageContrast()                                    │
│   • updatePageCrop()                                        │
│   • generatePdf()                                           │
│   • resetScanner()                                          │
└────┬──────────────────┬────────────────┬───────────────────┘
     │                  │                │
     ▼                  ▼                ▼
┌─────────┐    ┌──────────────┐    ┌──────────────┐
│ Domain  │    │   Billing    │    │    Utils     │
│ Layer   │    │   Manager    │    │              │
│         │    │              │    │              │
│ • Pdf   │    │ • Connect    │    │ • FileUtils  │
│   Gen   │    │ • Query      │    │ • Prefs      │
│ • Image │    │ • Purchase   │    │   Manager    │
│   Proc  │    │ • Restore    │    │              │
└────┬────┘    └──────┬───────┘    └──────┬───────┘
     │                │                    │
     ▼                ▼                    ▼
┌────────────────────────────────────────────────┐
│          External Systems & Storage            │
│  • Android PdfDocument API                     │
│  • MediaStore (Documents)                      │
│  • FileProvider                                │
│  • Google Play Billing                         │
│  • SharedPreferences                           │
│  • CameraX                                     │
└────────────────────────────────────────────────┘
```

## Key User Flows

### Flow 1: First-time Free User Scanning
```
Open App → Grant Camera Permission → Capture Document → 
Edit (Brightness/Contrast/Crop) → Add More Pages (optional) → 
Generate PDF → PDF Saved with Watermark → Share or New Scan
```

### Flow 2: Free User Reaches Limit
```
Open App → Try to Capture (6th scan) → Alert: "Limit Reached" → 
Tap "Upgrade to Pro" → Pro Screen → "Unlock Pro" → 
Google Play Purchase → Pro Activated → Unlimited Scanning
```

### Flow 3: Pro User Scanning
```
Open App (shows "Pro Active ✓") → Capture Document → 
Edit → Generate PDF (No Watermark, High Quality) → Share
```

### Flow 4: Restore Purchase
```
Open App → Upgrade Button → Pro Screen → "Restore Purchases" → 
Query existing purchases → Pro Status Restored
```

## State Management Flow

```
User Action → UI Event → ViewModel Function → 
State Update (via _uiState.update {}) → 
StateFlow Emission → UI Recomposition
```

### Example: Capturing an Image
```
1. User taps Capture button
2. CameraScreen calls viewModel.addScannedPage(bitmap)
3. ViewModel checks scan limit (if free user)
4. ViewModel updates state: _uiState.update { 
     it.copy(capturedPages = newPages, scansRemainingToday = remaining) 
   }
5. StateFlow emits new state
6. UI recomposes with new data
7. Navigation to PreviewScreen triggered
```

## Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                  Presentation Layer                      │
│         (Jetpack Compose + Material 3)                   │
│                                                           │
│  • CameraScreen.kt                                       │
│  • PreviewScreen.kt                                      │
│  • ResultScreen.kt                                       │
│  • ProScreen.kt                                          │
│  • NavigationGraph.kt                                    │
│  • Theme.kt                                              │
└───────────────────────┬──────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────┐
│                  ViewModel Layer                         │
│                   (State Management)                     │
│                                                           │
│  • ScannerViewModel.kt                                   │
│  • ScannerUiState.kt                                     │
│                                                           │
│  Pattern: MVVM with StateFlow                            │
└───────────────────────┬──────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        ▼               ▼               ▼
┌───────────┐   ┌──────────────┐   ┌──────────┐
│  Domain   │   │   Billing    │   │  Utils   │
│   Layer   │   │    Layer     │   │  Layer   │
│           │   │              │   │          │
│ • Pdf     │   │ • Billing    │   │ • File   │
│   Gen     │   │   Manager    │   │   Utils  │
│           │   │              │   │ • Prefs  │
└───────────┘   └──────────────┘   └──────────┘
```

## Summary

This app follows a clean, modern architecture with:
- **Single Activity** + **Navigation Compose** for navigation
- **MVVM** pattern with **StateFlow** for reactive state management
- **Jetpack Compose** for declarative UI
- **Material 3** for modern design
- **Clean Architecture** with clear layer separation
- **Dependency Injection** through ViewModel constructor

All user interactions flow through the ViewModel, ensuring a single source of truth for UI state and making the app testable and maintainable.
