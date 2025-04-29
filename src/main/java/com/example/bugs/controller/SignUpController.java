package com.example.bugs.controller;

import java.io.IOException;

import com.example.bugs.HelloApplication;
import com.example.bugs.dao.UserDAO;
import com.example.bugs.model.SignUp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signUpButton;
    @FXML private Button signInButton;
    
    private final UserDAO userDAO = new UserDAO();

    @FXML
    protected void onSignUpButtonClick() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match!");
            return;
        }
        
        SignUp user = new SignUp(firstName, lastName, email, password);
//        Temp fix, was causing error on first time account creation
//        if (!user.isValidUser()
//        {
//            showAlert("Please check your input!");
//            return;
//        }
        if (userDAO.emailExists(email)) {
            showAlert("Error", "Email already exists!");
            return;
        }
        
        if (userDAO.createUser(user)) {
            showAlert("Success", "Account created successfully!");
            navigateToLogin();
        } else {
            showAlert("Error", "Could not create account. Please try again.");
        }
    }

    @FXML
    protected void onSignInButtonClick() throws IOException {
        navigateToLogin();
    }
    
    private void navigateToLogin() {
        try {
            Stage stage = (Stage) signInButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Error", "Could not navigate to login page.");
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}