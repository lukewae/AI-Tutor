package com.example.bugs.controller;

import java.io.IOException;

import com.example.bugs.HelloApplication;
import com.example.bugs.util.ThemeManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class BaseController {
    @FXML
    private Button settingsButton;
    @FXML
    private Button logOutButton;
    @FXML
    private Button studyButton;

    @FXML
    protected void onSettingsButtonClick() throws IOException {
        Stage stage = (Stage) settingsButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("settings-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        
        // Apply current theme
        ThemeManager.applyCurrentTheme(scene);
        stage.setScene(scene);
    }

    @FXML
    protected void onLogOutButtonClick() throws IOException {
        Stage stage = (Stage) logOutButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        
        // Apply current theme
        ThemeManager.applyCurrentTheme(scene);
        stage.setScene(scene);
    }

    @FXML
    protected void onStudyButtonClick() throws IOException {
        Stage stage = (Stage) studyButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("study-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        
        // Apply current theme
        ThemeManager.applyCurrentTheme(scene);
        
        stage.setScene(scene);
        stage.setResizable(true);
    }
}