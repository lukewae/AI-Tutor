package com.example.bugs;

import java.io.IOException;

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

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database
        DatabaseUtil.initDatabase();
        
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());  // Remove fixed width to let HBox determine size
        
        // Add the CSS stylesheet to the scene
        String cssPath = HelloApplication.class.getResource("/com/example/bugs/styles/themes.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        
        // Apply theme based on stored preferences
        ThemeManager.applyCurrentTheme(scene);
        
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setResizable(false);  // Optional: prevent window resizing
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}