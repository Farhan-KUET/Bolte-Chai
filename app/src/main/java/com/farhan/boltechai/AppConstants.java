package com.farhan.boltechai;

/**
 * AppConstants — Central constants used throughout the app.
 * WATERMARK: The DEVELOPER_CREDIT constant is displayed on every screen
 * and must never be hidden by any setting.
 */
public final class AppConstants {

    private AppConstants() {} // Prevent instantiation

    // ===== WATERMARK (required) =====
    // This text appears on every screen at 50% opacity.
    // No setting may hide or remove it.
    public static final String DEVELOPER_CREDIT = "Developed by Farhan Shariar";

    // ===== Preferences Keys =====
    public static final String PREF_NAME = "boltechai_prefs";
    public static final String PREF_LANGUAGE = "pref_language";
    public static final String PREF_GRID_SIZE = "pref_grid_size";
    public static final String PREF_SCANNING_ENABLED = "pref_scanning_enabled";
    public static final String PREF_SCANNING_SPEED = "pref_scanning_speed";
    public static final String PREF_CAREGIVER_PIN = "pref_caregiver_pin";
    public static final String PREF_FIREBASE_UID = "pref_firebase_uid";
    public static final String PREF_FIRST_LAUNCH = "pref_first_launch";

    // ===== Language Codes =====
    public static final String LANG_BANGLA = "bn";
    public static final String LANG_ENGLISH = "en";

    // ===== Default Values =====
    public static final int DEFAULT_GRID_SIZE = 3;
    public static final int DEFAULT_SCANNING_SPEED_MS = 2000;
    public static final String DEFAULT_PIN = "1234";

    // ===== Category Keys =====
    public static final String CAT_FOOD = "food";
    public static final String CAT_NEEDS = "needs";
    public static final String CAT_FEELINGS = "feelings";
    public static final String CAT_FAMILY = "family";
    public static final String CAT_SCHOOL = "school";
    public static final String CAT_MARKET = "market";
    public static final String CAT_MOSQUE = "mosque";
    public static final String CAT_HOSPITAL = "hospital";

    // ===== Intent Extras =====
    public static final String EXTRA_CATEGORY_KEY = "extra_category_key";
    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";
    public static final String EXTRA_TILE_ID = "extra_tile_id";

    // ===== Request Codes =====
    public static final int RC_CAMERA = 1001;
    public static final int RC_GALLERY = 1002;
    public static final int RC_TTS_CHECK = 1003;

    // ===== File Paths =====
    public static final String TILE_IMAGES_DIR = "tile_images";
    public static final String SHARED_IMAGES_DIR = "shared_images";
    public static final String EXPORT_DIR = "export";

    // ===== ContentProvider =====
    public static final String AUTHORITY_BOARDS = "com.farhan.boltechai.boards";
    public static final String AUTHORITY_FILEPROVIDER = "com.farhan.boltechai.fileprovider";

    // ===== Firestore Collections =====
    public static final String FS_COLLECTION_BOARDS = "boards";
    public static final String FS_COLLECTION_TILES = "tiles";

    // ===== Pictogram API (configurable source) =====
    // Default: ARASAAC (open-licensed CC BY-NC-SA)
    public static final String PICTOGRAM_BASE_URL = "https://api.arasaac.org/v1/";
    public static final String PICTOGRAM_SEARCH_ENDPOINT = "pictograms/search/";
    public static final String PICTOGRAM_SEARCH_URL = "https://api.arasaac.org/v1/pictograms/search/";
    public static final String PICTOGRAM_IMAGE_URL = "https://static.arasaac.org/pictograms/";
    public static final String PICTOGRAM_ATTRIBUTION =
            "Pictograms by ARASAAC (https://arasaac.org), " +
            "created by Sergio Palao, licensed under CC BY-NC-SA 4.0. " +
            "Government of Aragon, Spain.";

    // ===== Hospital Board =====
    public static final String HOSPITAL_PHRASE_PREFIX = "আমার ";
    public static final String HOSPITAL_PHRASE_SUFFIX = " ব্যথা করছে";
}
