package com.example.bugs.controller;

import com.example.bugs.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private TextArea termsAndConditions;
    @FXML
    private CheckBox agreeCheckBox;
    @FXML
    private Button signUpButton;
    @FXML
    private Button signInButton;


    @FXML
    protected void signInButtonClicked() throws IOException {
        Stage stage = (Stage) signInButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("base-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }
    @FXML
    protected void signUpButtonClicked() throws IOException {
        Stage stage = (Stage) signUpButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }
//    @FXML
//    protected void onAgreeCheckBoxClick() {
//        boolean accepted = agreeCheckBox.isSelected();
//        nextButton.setDisable(!accepted);
//    }

//    @FXML
//    protected void onNextButtonClick() throws IOException {
//        Stage stage = (Stage) nextButton.getScene().getWindow();
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
//        stage.setScene(scene);
//    }
//
//    @FXML
//    protected void onCancelButtonClick() {
//        Stage stage = (Stage) nextButton.getScene().getWindow();
//        stage.close();
//    }
}