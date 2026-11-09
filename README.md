# Bolte Chai (বলতে চাই) — Bangla AAC Android App

An offline-first, picture-based Augmentative and Alternative Communication (AAC) Android application built with **pure Java** and **Material Components 3**. Designed specifically for non-verbal individuals, speech-impaired children, stroke survivors, and clinical hospital communication in Bengali.

---

## Developer Credit & Permanent Watermark
**"Developed by Farhan Shariar"** is displayed across every screen via a reusable custom view (`WatermarkView`) and stamped onto all exported board images and share messages.

---

## Key Features

1. **Sentence Builder Strip (`SentenceStripView`)**:
   - Tap picture tiles to sequence words into a complete sentence.
   - Speak button invokes native Android `TextToSpeech` in Bengali (`bn-BD`).
   - Single-tile deletion (backspace) and clear-all actions.

2. **8 Pre-loaded Boards & 70+ Default Tiles (`DatabaseSeeder`)**:
   - Categories: খাবার (Food), প্রয়োজন (Needs), অনুভূতি (Feelings), পরিবার (Family), স্কুল (School), বাজার (Market), মসজিদ (Mosque), হাসপাতাল (Hospital).
   - Automatically seeded into local Room database upon first launch.

3. **Clinical Hospital Pain Board (`HospitalBoardActivity`)**:
   - Interactive human body outline (`ic_body_outline`).
   - 12 touchable anatomical regions (head, eyes, throat, chest, stomach, knee, etc.).
   - Dynamically forms: *"আমার [অঙ্গ] ব্যথা করছে"* ("My [body part] hurts").

4. **Custom Tile Creator (`TileEditorActivity`)**:
   - Caregiver PIN-protected tile addition.
   - Implicit camera Intent (`FileProvider`) and gallery picker.
   - Locally stored images in app-private storage.

5. **Accessibility Scanning Mode (`ScanningHelper`)**:
   - Single-switch and motor-impaired accessible auto-scanning.
   - Highlights tiles sequentially with customizable interval speed (500ms - 5000ms).
   - Selection triggered via hardware Volume Key or tap.

6. **Online ARASAAC Pictogram Search (`PictogramSearchActivity`)**:
   - Direct search querying ARASAAC REST API via OkHttp + Gson.
   - Open-licensed (CC BY-NC-SA 4.0) symbols cached locally with Glide.

7. **Usage Analytics & Frequency Reports (`UsageHistoryActivity`)**:
   - Tracks most frequently expressed words and phrases with relative visual progress bars.

8. **Dual Locale Support (Bangla & English)**:
   - Bengali default with dynamic runtime language toggle.

---

## Technical Specifications & Architecture

- **Language:** Java (JDK 17/21/23 compatible)
- **Target SDK:** Android 15 (API 35) | **Compile SDK:** API 36 | **Min SDK:** Android 7.0 (API 24)
- **Architecture:** Clean Layered Architecture (UI / Service / Data / Database)
- **UI & Layouts:** XML Layouts with ViewBinding, Material Components 3, Large Touch Targets (min 48dp)
- **Local Persistence:** AndroidX Room (`AppDatabase`, `TileDao`, `UsageDao`)
- **Networking:** OkHttp 4.12, Gson 2.11, Glide 4.16
- **IPC & Architecture Components:**
  - Bound Service (`SpeechService` via `IBinder`)
  - ContentProvider (`BoardContentProvider`, `FileProvider`)
  - BroadcastReceivers (`NetworkReceiver`, `LowBatteryReceiver`)
  - Explicit & Implicit Intents

---

## Build & Run

### Prerequisites
- Android Studio Ladybug / Koala / Hedgehog or newer
- JDK 17+ installed

### Building via Gradle Wrapper
```bash
# Debug build (generates app-debug.apk)
.\gradlew.bat assembleDebug

# Release build with R8 optimization (generates app-release-unsigned.apk)
.\gradlew.bat assembleRelease

# Run unit tests
.\gradlew.bat test
```

Generated APKs:
- **Debug:** `app/build/outputs/apk/debug/app-debug.apk`
- **Release:** `app/build/outputs/apk/release/app-release-unsigned.apk`

---

## Documentation
See [`docs/EXPLAIN.md`](docs/EXPLAIN.md) for a comprehensive, class-by-class technical breakdown of all 18 Android lab topics and system components.
