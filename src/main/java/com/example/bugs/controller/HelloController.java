package com.example.bugs.controller;

import java.io.IOException;

import com.example.bugs.HelloApplication;
import com.example.bugs.dao.UserDAO;
import com.example.bugs.util.ThemeManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HelloController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button signInButton;
    @FXML private Button signUpButton;
    
    private final UserDAO userDAO = new UserDAO();

    @FXML
    protected void signInButtonClicked() {
        String email = emailField.getText();
        String password = passwordField.getText();
        
        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both email, and password!");
            return;
        }
        
        // Check credentials against database
        if (userDAO.validateLogin(email, password)) {
            try {
                navigateToBase();
            } catch (IOException e) {
                showAlert("Error", "Could not navigate to main page.");
            }
        } else {
            showAlert("Error", "Invalid email or password!");
        }
    }

    @FXML
    protected void signUpButtonClicked() throws IOException {
        Stage stage = (Stage) signUpButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ThemeManager.applyCurrentTheme(scene);
        stage.setScene(scene);
    }
    
    private void navigateToBase() throws IOException {
        Stage stage = (Stage) signInButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("base-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        ThemeManager.applyCurrentTheme(scene);
        stage.setScene(scene);
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}