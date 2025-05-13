package com.example.bugs.controller;

import java.io.IOException;
import java.util.prefs.Preferences;

import com.example.bugs.HelloApplication;
import com.example.bugs.util.ThemeManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SettingsController {

    @FXML private Button backButton;
    @FXML private ToggleButton darkModeToggle;
    @FXML private ToggleButton highContrastToggle;
    @FXML private ToggleButton placeholderToggle;
    @FXML private AnchorPane rootPane;
    
    private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
    private boolean isDarkMode;
    private boolean isHighContrast;
    private boolean isPlaceholder;

    @FXML
    public void initialize() {
        // Load saved preferences
        isDarkMode = prefs.getBoolean("darkMode", false);
        isHighContrast = prefs.getBoolean("highContrast", false);
        isPlaceholder = prefs.getBoolean("placeholder", false);
        
        // Set toggle buttons state based on preferences
        darkModeToggle.setSelected(isDarkMode);
        highContrastToggle.setSelected(isHighContrast);
        placeholderToggle.setSelected(isPlaceholder);
        
        // Apply current theme
        applyTheme();
    }

    @FXML
    protected void handleBackButton() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("base-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        
        // Make sure to add the stylesheet before applying the theme
        String cssPath = getClass().getResource("/com/example/bugs/styles/themes.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        
        ThemeManager.applyCurrentTheme(scene);
        stage.setScene(scene);
    }
    
    @FXML
    protected void handleDarkModeToggle() {
        isDarkMode = darkModeToggle.isSelected();
        ThemeManager.saveThemePreferences(isDarkMode, isHighContrast);
        applyTheme();
    }
    
    @FXML
    protected void handleHighContrastToggle() {
        isHighContrast = highContrastToggle.isSelected();
        ThemeManager.saveThemePreferences(isDarkMode, isHighContrast);
        applyTheme();
    }
    
    @FXML
    protected void handlePlaceholderToggle() {
        isPlaceholder = placeholderToggle.isSelected();
        prefs.putBoolean("placeholder", isPlaceholder);
    }
    
    private void applyTheme() {
        rootPane.getStyleClass().removeAll("light-mode", "dark-mode", "high-contrast");
        
        if (isDarkMode) {
            rootPane.getStyleClass().add("dark-mode");
        } else {
            rootPane.getStyleClass().add("light-mode");
        }
        
        if (isHighContrast) {
            rootPane.getStyleClass().add("high-contrast");
        }
    }
}