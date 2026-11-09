# Bolte Chai (বলতে চাই) — Architecture & Code Explanation

Bolte Chai is an offline-first Augmentative and Alternative Communication (AAC) Android app created in pure Java for non-verbal individuals, particularly children and adults on the autism spectrum or with speech impairments. The app enables users to tap picture tiles to build sentences, and the device vocalizes them aloud in natural Bangla (with full support for English as well).

**Developer Credit:** "Developed by Farhan Shariar" is permanently integrated into the app watermark across all screens, exported boards, and reports.

---

## 1. Project Overview & Architecture

- **Language:** Pure Java (JDK 17/21/23 compatible, no Kotlin, no Jetpack Compose).
- **UI Framework:** Android XML layouts, ViewBinding, and Material Components 3.
- **Minimum SDK:** API 24 (Android 7.0 Nougat).
- **Target SDK:** API 35 (Android 15).
- **Architecture:** Offline-First Model-View-Controller / Repository architecture backed by Room SQLite database and a background bound Text-to-Speech service.
- **Licensing & Attributions:** Default pictograms and online search use ARASAAC (Creative Commons BY-NC-SA 4.0 license).

---

## 2. Component-by-Component Walkthrough

### 2.1 Core Application & Constants

#### `AppConstants.java`
- **Purpose:** Centralized, immutable single source of truth for all application constants.
- **Key Fields:**
  - `DEVELOPER_CREDIT = "Developed by Farhan Shariar"`: Constant used in the persistent footer watermark and exported content.
  - Category keys: `CAT_FOOD`, `CAT_NEEDS`, `CAT_FEELINGS`, `CAT_FAMILY`, `CAT_SCHOOL`, `CAT_MARKET`, `CAT_MOSQUE`, `CAT_HOSPITAL`.
  - Intent extras: `EXTRA_CATEGORY_KEY`, `EXTRA_CATEGORY_NAME`.
  - SharedPreferences keys for language, grid dimensions, scanning parameters, and caregiver security PIN.
  - ARASAAC API endpoints for online pictogram search and caching.
  - Hospital phrase prefix (`"আমার"`) and suffix (`"ব্যথা করছে"`).

#### `BolteChaiApp.java`
- **Purpose:** Global `Application` subclass running from app launch.
- **Responsibilities:**
  - Manages global locale switching between Bangla (`"bn"`) and English (`"en"`).
  - Supplies locale-wrapped contexts via `getLocaleContext(Context)` to every Activity inside `attachBaseContext()`.
  - Stores and retrieves runtime configuration (grid size, scanning toggle, scanning speed, caregiver PIN) via `SharedPreferences`.
  - Initializes the Room database singleton upon startup.

---

### 2.2 Local Database Layer (Room)

#### `TileEntity.java`
- **Purpose:** SQLite table representation of an individual AAC communication tile.
- **Attributes:**
  - `id`: Auto-incrementing primary key.
  - `category`: Category identifier (e.g., `food`, `needs`).
  - `labelBn`: Bangla text displayed on tile and pronounced by TTS.
  - `labelEn`: English text counterpart.
  - `imagePath`: Either a drawable resource name (`ic_rice`) or a local filesystem path for user-captured photos.
  - `isCustom`: Boolean indicating if the tile was created by the user/caregiver.
  - `sortOrder`: Ordering index within the category grid.

#### `UsageEntity.java`
- **Purpose:** SQLite table tracking tile tap events for usage analytics and the "Most Used Words" screen.
- **Attributes:**
  - `tileId`: Primary key mapping to the used tile.
  - `useCount`: Integer tally of how many times the tile was selected.
  - `lastUsed`: Millisecond timestamp of the last tap.
  - `labelBn`, `labelEn`, `category`: Cached labels for instant display without requiring cross-table joins.

#### `TileDao.java`
- **Purpose:** Data Access Object defining Room queries for tiles.
- **Queries:**
  - `getTilesByCategory(String category)`: Fetches tiles belonging to a category ordered by `sort_order`.
  - `insertTile(TileEntity tile)`: Inserts a new tile into the database.
  - `deleteTile(TileEntity tile)`: Removes a custom tile.
  - `getTileCountForCategory(String category)`: Computes existing tile count to establish sort order.

#### `UsageDao.java`
- **Purpose:** Data Access Object for tracking and querying tile usage.
- **Queries:**
  - `getAllUsageSorted()`: Retrieves all usage records ordered by `use_count DESC`.
  - `getTopUsed(int limit)`: Retrieves the top $N$ most frequently used words.
  - `insertOrUpdate(UsageEntity entity)`: Inserts or updates tile usage.
  - `deleteAllUsage()`: Clears all usage telemetry when requested by caregiver.

#### `AppDatabase.java`
- **Purpose:** Room Database abstract class maintaining the SQLite database singleton instance `boltechai.db`.
- **Initialization:** Automatically seeds default tiles on initial database creation using `DatabaseSeeder`.

#### `DatabaseSeeder.java`
- **Purpose:** Seeds the database with over 70 initial communication tiles across 8 categories:
  1. **Food (খাবার):** Rice, Water, Milk, Bread, Lentils, Fish, Chicken, Fruit, Tea, Biscuit.
  2. **Needs (প্রয়োজন):** Toilet, Sleep, Help, Pain, Medicine, Clothes, Shoes, Glasses.
  3. **Feelings (অনুভূতি):** Happy, Sad, Angry, Fear, Tired, Good, Bad, Love.
  4. **Family (পরিবার):** Mother, Father, Brother, Sister, Grandfather, Grandmother, Maternal Grandfather, Maternal Grandmother.
  5. **School (স্কুল):** Teacher, Book, Pen, Study, Play, Friend, Holiday, Class.
  6. **Market (বাজার):** Buy, Price, Money, Vegetables, Fruit, Sweets, Clothes, Shoes.
  7. **Mosque (মসজিদ):** Prayer, Ablution, Call to Prayer, Quran, Prayer Mat, Cap, Friday, Supplication.
  8. **Hospital (হাসপাতাল):** Head, Eye, Ear, Nose, Teeth, Throat, Chest, Stomach, Back, Hand, Foot, Knee.

---

### 2.3 Custom UI Views & Adapters

#### `WatermarkView.java`
- **Purpose:** Reusable compound custom view displaying `"Developed by Farhan Shariar"`.
- **Design:** Rendered at 50% opacity in the footer of every screen. It is explicitly configured with `clickable=false`, `focusable=false`, and `importantForAccessibility=no` so it never interferes with touch interactions or TalkBack accessibility tree navigation.

#### `SentenceStripView.java`
- **Purpose:** Custom compound view placed at the top of communication screens.
- **Features:**
  - Displays selected tiles as chips in a horizontal scrolling container (`HorizontalScrollView`).
  - Automatically scrolls to the right as new tiles are appended.
  - Provides a "Delete" button to pop the last tile, a "Clear" button to wipe the strip, and a "Speak" button to synthesize the complete phrase.
  - Supports adding predefined hospital pain sentences directly.

#### `CategoryAdapter.java`
- **Purpose:** RecyclerView adapter for the home screen grid, rendering the 8 main categories with distinct vector icons and labels.

#### `TileAdapter.java`
- **Purpose:** RecyclerView adapter for the communication tile grid.
- **Features:**
  - Displays tile images loaded dynamically from resources or internal storage using Glide.
  - Implements visual highlighting (`scanning_highlight` border) for Accessibility Scanning Mode.
  - Provides TalkBack accessibility descriptions for every tile.

#### `UsageAdapter.java`
- **Purpose:** RecyclerView adapter for the usage analytics screen, displaying the tile label, raw count, and a horizontal progress bar demonstrating relative frequency.

#### `PictogramAdapter.java`
- **Purpose:** RecyclerView adapter for the online ARASAAC pictogram search results. Employs Glide with disk caching to ensure smooth scrolling.

---

### 2.4 User Interface Activities

#### `SplashActivity.java`
- **Purpose:** Application entry point.
- **Features:**
  - Logs all 6 Android Activity lifecycle callbacks (`onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`).
  - Displays the splash screen branding and watermark for 2 seconds.
  - Dispatches an explicit Intent to launch `MainActivity` and calls `finish()` to prevent back-navigation.

#### `MainActivity.java`
- **Purpose:** Main hub of the application.
- **Features:**
  - Displays `SentenceStripView` and the 8 communication categories in a customizable grid.
  - Connects to `SpeechService` via `bindService()` to voice sentences.
  - Verifies presence of Bangla TTS voice and alerts user if not installed.
  - Toolbar options menu provides quick access to Language Toggle (Bangla/English), Settings, Usage History, Online Pictogram Search, and About screen.

#### `BoardActivity.java`
- **Purpose:** Displays tiles for a chosen category.
- **Features:**
  - Loads tiles from Room on a background worker thread.
  - Tapping a tile appends it to `SentenceStripView` and records usage in Room.
  - Supports Accessibility Switch Scanning: automatically steps through tiles at user-defined intervals; hardware volume keys trigger selection.
  - Floating Action Button (FAB) enables caregivers to add custom tiles, guarded by a security PIN dialog.
  - Toolbar share action allows sharing the built sentence or an image of the board via the Android ShareSheet.

#### `TileEditorActivity.java`
- **Purpose:** Enables caregivers to capture custom photos and add personalized tiles.
- **Features:**
  - Dispatches an implicit camera Intent (`MediaStore.ACTION_IMAGE_CAPTURE`) with a temporary file URI generated via `FileProvider`.
  - Dispatches an implicit gallery Intent (`Intent.ACTION_PICK`) to select existing device photos.
  - Safely copies selected images to app-private internal storage.
  - Inserts new `TileEntity` into the Room database.

#### `HospitalBoardActivity.java`
- **Purpose:** Dedicated medical communication board for clinical or emergency settings.
- **Features:**
  - Displays a human body diagram silhouette and large touch-friendly body part buttons (Head, Eyes, Ears, Nose, Teeth, Throat, Chest, Stomach, Back, Hands, Knees, Feet).
  - Tapping an anatomical region constructs the sentence: `"আমার [অঙ্গ] ব্যথা করছে"` (e.g., `"আমার মাথা ব্যথা করছে"` / "My head hurts").
  - Bound to `SpeechService` to vocalize pain immediately.

#### `UsageHistoryActivity.java`
- **Purpose:** Displays communication statistics to caregivers and speech therapists.
- **Features:** Queries `UsageDao.getAllUsageSorted()` to display words ranked by popularity.

#### `SettingsActivity.java`
- **Purpose:** Configuration center for the application.
- **Features:**
  - Language switch (Bangla / English).
  - Grid size selector (2x2 up to 5x5).
  - Accessibility scanning mode toggle and speed slider (500ms to 5000ms).
  - Caregiver 4-digit PIN setup.
  - Firebase sync status indicator.
  - "Delete All Data" button to clear database, preferences, and cached images.

#### `AboutActivity.java`
- **Purpose:** Information, licensing, and compliance screen.
- **Features:**
  - Displays application description and version.
  - Prominently displays developer watermark: `"Developed by Farhan Shariar"`.
  - Details ARASAAC pictogram licensing (CC BY-NC-SA 4.0).
  - Presents privacy statement explaining that the app does not collect personal data.

#### `PictogramSearchActivity.java`
- **Purpose:** Search and download thousands of open-source pictograms online.
- **Features:**
  - Asynchronously queries the ARASAAC REST API using `OkHttp`.
  - Deserializes JSON responses using `Gson`.
  - Renders pictograms in a grid.
  - Tapping a pictogram saves it into the local Room database for offline use.

---

### 2.5 Services, Receivers, Providers & Helpers

#### `SpeechService.java`
- **Purpose:** Bound Android `Service` managing the Text-to-Speech (TTS) engine.
- **Features:**
  - Initializes `TextToSpeech` with the Bangla locale (`new Locale("bn", "BD")` / `"bn"`).
  - Queues utterances using `TextToSpeech.QUEUE_ADD`.
  - Automatically handles TTS initialization errors and checks voice availability.

#### `ScanningHelper.java`
- **Purpose:** Motor-accessibility helper for single-switch users.
- **Features:**
  - Runs a looping `Handler` that sequentially increments the highlighted tile position in `TileAdapter`.
  - Supports configurable cycle intervals (500ms - 5000ms).
  - Volume key presses or screen taps immediately select the highlighted tile.

#### `ShareHelper.java`
- **Purpose:** Utility for exporting sentences and communication boards.
- **Features:**
  - Dispatches `Intent.ACTION_SEND` to share plain text sentences with developer credit watermark.
  - Converts UI views to PNG bitmaps and shares them securely using `FileProvider`.

#### `FirebaseSyncManager.java`
- **Purpose:** Optional cloud backup manager.
- **Features:**
  - Provides anonymous Firebase authentication (`FirebaseAuth`).
  - Syncs only non-sensitive board metadata (categories, tile counts, timestamps) to Cloud Firestore.
  - Strictly preserves privacy: no child names, ages, or personal data are stored in the cloud.

#### `LowBatteryReceiver.java`
- **Purpose:** `BroadcastReceiver` that listens for `Intent.ACTION_BATTERY_LOW` to alert caregivers to charge the device so communication is not abruptly cut off.

#### `NetworkReceiver.java`
- **Purpose:** `BroadcastReceiver` monitoring network connectivity changes to trigger background cloud sync when online.

#### `BoardContentProvider.java`
- **Purpose:** Read-only Android `ContentProvider` (`content://com.farhan.boltechai.boards/categories`). Exposes category names to other assistive tools or widgets securely via `MatrixCursor`.

---

## 3. Accessibility & High Contrast Design

- **Touch Targets:** All clickable interactive elements have a minimum touch target size of 48dp x 48dp (and tiles are 100dp x 100dp).
- **Contrast Ratios:** Backgrounds and cards maintain high contrast (dark text `#212121` on light surfaces `#FFFFFF` and `#E3F2FD`).
- **Screen Reader (TalkBack):** Every image view, button, and card specifies explicit `contentDescription` in both Bangla and English. The watermark view is marked `importantForAccessibility="no"` to avoid cluttering TalkBack navigation.
