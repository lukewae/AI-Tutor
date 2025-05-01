package com.example.bugs.controller;

import com.example.bugs.HelloApplication;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatbotController {

    @FXML private TextField userInputField;
    @FXML private ToggleButton sendButton;
    @FXML private Label responseLabel;
    @FXML private ProgressIndicator thinkingSpinner;
    @FXML private VBox chatContainer;
    @FXML private Button backButton;

    private OllamaAPI ollamaAPI;
    private List<OllamaChatMessage> chatHistory = new ArrayList<>();
    private static final String MODEL_NAME = "gemma3:4b";

    @FXML
    protected void handleBackButton() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("base-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    public void initialize() {
        // Initialize Ollama API (using localhost by default)
        ollamaAPI = new OllamaAPI();

        // Set up spinner as initially invisible
        thinkingSpinner.setVisible(false);

        // Display a welcome message
        responseLabel.setText("Hello! I'm your AI tutor. How can I help you today?");

        // Set up the send button action
        sendButton.setOnAction(event -> sendMessage());

        // Set up to listen for Enter key on text field
        userInputField.setOnAction(event -> sendMessage());
    }

    private void sendMessage() {
        String userMessage = userInputField.getText().trim();

        // Don't process empty messages
        if (userMessage.isEmpty()) {
            return;
        }

        // Clear input field and show spinner
        userInputField.clear();
        thinkingSpinner.setVisible(true);

        // Add user message to chat history
        chatHistory.add(new OllamaChatMessage(OllamaChatMessageRole.USER, userMessage));

        // Update UI with user message
        String currentContent = responseLabel.getText();
        responseLabel.setText(currentContent + "\n\nYou: " + userMessage + "\n\nAI Tutor: ");

        // Create request for Ollama
        OllamaChatRequest request = new OllamaChatRequest(MODEL_NAME, chatHistory);

        // Start a background thread to handle the API call
        new Thread(() -> {
            try {
                // Make the API call
                OllamaChatResult result = ollamaAPI.chat(request);

                // Get the response content
                String responseContent = result.getResponseModel().getMessage().getContent();

                // Add assistant's response to chat history
                chatHistory.add(new OllamaChatMessage(
                        OllamaChatMessageRole.ASSISTANT,
                        responseContent));

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    String fullResponse = currentContent +
                            "\n\nYou: " + userMessage +
                            "\n\nAI Tutor: " + responseContent;
                    responseLabel.setText(fullResponse);
                    thinkingSpinner.setVisible(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    thinkingSpinner.setVisible(false);
                    responseLabel.setText(currentContent +
                            "\n\nYou: " + userMessage +
                            "\n\nAI Tutor: Sorry, I encountered an error. " +
                            "Please check if the Ollama server is running with the gemma3:4b model. You can ensure your Ollama server is open by entering http://localhost:11434 in your browser. If you don't see \"Ollama is running\", check your firewall settings.");
                    e.printStackTrace();
                });
            }
        }).start();
    }
}