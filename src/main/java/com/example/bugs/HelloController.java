package com.example.bugs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class HelloController {
    @FXML
    private TextArea termsAndConditions;
    @FXML
    private Button nextButton;

    @FXML
    public void initialize() {
        termsAndConditions.setText("Your terms and conditions text here...");
    }

    // Fix this method signature - must take ActionEvent or no parameters
    @FXML
    private void onAgreeCheckBoxClick() {  // Changed from (CheckBox agreeCheckBox)
        boolean accepted = ((CheckBox) nextButton.getScene().lookup("#agreeCheckBox")).isSelected();
        nextButton.setDisable(!accepted);
    }

    @FXML
    private void onNextButtonClick() throws IOException {
        Stage stage = (Stage) nextButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    private void onCancelButtonClick() {
        Stage stage = (Stage) nextButton.getScene().getWindow();
        stage.close();
    }
}