package com.example.bugs;

import java.io.IOException;
import java.util.Arrays;

import com.example.bugs.util.DatabaseUtil;
import com.example.bugs.util.ThemeManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    public static final String TITLE = "AI Tutor";
    public static final int WIDTH = 640;  // Keep this width for other views
    public static final int HEIGHT = 400;
    
    // Flag for CI/CD testing
    private static boolean isTestMode = false;
    private static boolean isHeadless = false;

    @Override
    public void start(Stage stage) throws IOException {
        // Parse parameters from command line
        Parameters params = getParameters();
        if (params != null && params.getRaw().contains("--test-mode")) {
            isTestMode = true;
        }
        if (params != null && params.getRaw().contains("--headless")) {
            isHeadless = true;
        }

        // Initialize database
        DatabaseUtil.initDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/bugs/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());  // Remove fixed width to let HBox determine size
        
        // Add the CSS stylesheet to the scene.
        String cssPath = getClass().getResource("/com/example/bugs/styles/themes.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        
        // Apply theme based on stored preferences
        ThemeManager.applyCurrentTheme(scene);
        
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setResizable(false);  // Optional: prevent window resizing
        
        // Only show the window if not in headless mode
        if (!isHeadless) {
            stage.show();
        }
    }

    public static void main(String[] args) {
        // Check args for CI flags before launch
        isTestMode = Arrays.asList(args).contains("--test-mode");
        isHeadless = Arrays.asList(args).contains("--headless");
        
        launch(args);
    }
    
    // Getters for test purposes
    public static boolean isTestMode() {
        return isTestMode;
    }
    
    public static boolean isHeadless() {
        return isHeadless;
    }
}
