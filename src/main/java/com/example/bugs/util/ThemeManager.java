package com.example.bugs.util;

import java.util.prefs.Preferences;

import javafx.scene.Scene;

public class ThemeManager {
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);

    /**
     * Apply theme settings to a scene
     *
     * @param scene The scene to apply the theme to
     * @param isDarkMode Whether dark mode is enabled
     * @param isHighContrast Whether high contrast mode is enabled
     */
    public static void applyTheme(Scene scene, boolean isDarkMode, boolean isHighContrast) {
        // Add CSS file if not already added
        String cssPath = ThemeManager.class.getResource("/com/example/bugs/styles/themes.css").toExternalForm();
        if (!scene.getStylesheets().contains(cssPath)) {
            scene.getStylesheets().add(cssPath);
        }

        scene.getRoot().getStyleClass().removeAll("light-mode", "dark-mode", "high-contrast");
        
        if (isDarkMode) {
            scene.getRoot().getStyleClass().add("dark-mode");
        } else {
            scene.getRoot().getStyleClass().add("light-mode");
        }
        
        if (isHighContrast) {
            scene.getRoot().getStyleClass().add("high-contrast");
        }
    }

    /**
     * Apply saved theme settings to a scene
     *
     * @param scene The scene to apply the theme to
     */
    public static void applyCurrentTheme(Scene scene) {
        boolean isDarkMode = prefs.getBoolean("darkMode", false);
        boolean isHighContrast = prefs.getBoolean("highContrast", false);
        applyTheme(scene, isDarkMode, isHighContrast);
    }

    /**
     * Save theme settings
     *
     * @param isDarkMode Whether dark mode is enabled
     * @param isHighContrast Whether high contrast mode is enabled
     */
    public static void saveThemePreferences(boolean isDarkMode, boolean isHighContrast) {
        prefs.putBoolean("darkMode", isDarkMode);
        prefs.putBoolean("highContrast", isHighContrast);
    }
}