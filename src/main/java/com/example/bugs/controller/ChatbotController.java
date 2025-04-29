package com.example.bugs.controller;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ChatbotController {

    @FXML private TextField userInputField;
    @FXML private ToggleButton sendButton;
    @FXML private Label responseLabel;
    @FXML private ProgressIndicator thinkingSpinner;
    @FXML private VBox chatContainer;

    private OllamaAPI ollamaAPI;
    private List<OllamaChatMessage> chatHistory = new ArrayList<>();
    private static final String MODEL_NAME = "gemma3:4b";

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
                            "Please check if the Ollama server is running with the deepseek-r1:7b model.");
                    e.printStackTrace();
                });
            }
        }).start();
    }
}