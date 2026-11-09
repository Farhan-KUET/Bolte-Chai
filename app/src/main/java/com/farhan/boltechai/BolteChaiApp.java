package com.farhan.boltechai;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import java.util.Locale;

/**
 * BolteChaiApp — Custom Application class.
 * Initializes app-wide settings: locale (Bangla default), Room database,
 * and shared preferences.
 *
 * Lab Topic: Activity lifecycle (Application is the first component created)
 */
public class BolteChaiApp extends Application {

    private static final String TAG = "BolteChaiApp";
    private static BolteChaiApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d(TAG, "onCreate: Application created");

        // Apply saved language preference (default: Bangla)
        applyLanguage();
    }

    public static BolteChaiApp getInstance() {
        return instance;
    }

    /**
     * Applies the stored language preference.
     * Default language is Bangla (bn).
     */
    public void applyLanguage() {
        SharedPreferences prefs = getSharedPreferences(
                AppConstants.PREF_NAME, MODE_PRIVATE);
        String langCode = prefs.getString(
                AppConstants.PREF_LANGUAGE, AppConstants.LANG_BANGLA);

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());

        Log.d(TAG, "applyLanguage: Language set to " + langCode);
    }

    /**
     * Switches between Bangla and English, saves the preference,
     * and applies the new locale.
     */
    public void toggleLanguage() {
        SharedPreferences prefs = getSharedPreferences(
                AppConstants.PREF_NAME, MODE_PRIVATE);
        String current = prefs.getString(
                AppConstants.PREF_LANGUAGE, AppConstants.LANG_BANGLA);
        String newLang = current.equals(AppConstants.LANG_BANGLA)
                ? AppConstants.LANG_ENGLISH
                : AppConstants.LANG_BANGLA;

        prefs.edit().putString(AppConstants.PREF_LANGUAGE, newLang).apply();
        applyLanguage();
        Log.d(TAG, "toggleLanguage: Switched from " + current + " to " + newLang);
    }

    /**
     * Returns the current language code.
     */
    public String getCurrentLanguage() {
        SharedPreferences prefs = getSharedPreferences(
                AppConstants.PREF_NAME, MODE_PRIVATE);
        return prefs.getString(AppConstants.PREF_LANGUAGE, AppConstants.LANG_BANGLA);
    }

    /**
     * Returns the saved grid size for tile boards.
     */
    public int getGridSize() {
        SharedPreferences prefs = getSharedPreferences(
                AppConstants.PREF_NAME, MODE_PRIVATE);
        return prefs.getInt(AppConstants.PREF_GRID_SIZE, AppConstants.DEFAULT_GRID_SIZE);
    }

    /**
     * Returns whether scanning mode is enabled.
     */
    public boolean isScanningEnabled() {
        SharedPreferences prefs = getSharedPreferences(
                AppConstants.PREF_NAME, MODE_PRIVATE);
        return prefs.getBoolean(AppConstants.PREF_SCANNING_ENABLED, false);
    }

    /**
     * Returns the scanning speed in milliseconds.
     */
    public int getScanningSpeed() {
        SharedPreferences prefs = getSharedPreferences(
                AppConstants.PREF_NAME, MODE_PRIVATE);
        return prefs.getInt(AppConstants.PREF_SCANNING_SPEED,
                AppConstants.DEFAULT_SCANNING_SPEED_MS);
    }

    public android.content.Context getLocaleContext(android.content.Context context) {
        String lang = getCurrentLanguage();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }

    public void setGridSize(int size) {
        getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE)
                .edit().putInt(AppConstants.PREF_GRID_SIZE, size).apply();
    }

    public void setScanningEnabled(boolean enabled) {
        getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE)
                .edit().putBoolean(AppConstants.PREF_SCANNING_ENABLED, enabled).apply();
    }

    public void setScanningSpeed(int speedMs) {
        getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE)
                .edit().putInt(AppConstants.PREF_SCANNING_SPEED, speedMs).apply();
    }

    public String getCaregiverPin() {
        return getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE)
                .getString(AppConstants.PREF_CAREGIVER_PIN, "");
    }

    public void setCaregiverPin(String pin) {
        getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE)
                .edit().putString(AppConstants.PREF_CAREGIVER_PIN, pin).apply();
    }
}
