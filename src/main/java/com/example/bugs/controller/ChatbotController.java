package com.example.bugs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.bugs.HelloApplication;
import com.example.bugs.util.ThemeManager;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ChatbotController {

    @FXML private TextField userInputField;
    @FXML private ToggleButton sendButton;
    @FXML private TextFlow responseTextFlow;
    @FXML private ProgressIndicator thinkingSpinner;
    @FXML private VBox chatContainer;
    @FXML private Button backButton;
    @FXML private Button mathsButton;
    @FXML private Button englishButton;
    @FXML private Button historyButton;
    @FXML private Button geographyButton;
    @FXML private Button clearChatButton;

    private OllamaAPI ollamaAPI;
    private List<OllamaChatMessage> chatHistory = new ArrayList<>();
    private static final String MODEL_NAME = "gemma3:4b";

    // Subject prompts
    private static final String MATHS_PROMPT = "I want to study mathematics. Can you help me with some key concepts?";
    private static final String ENGLISH_PROMPT = "I'm studying English. Can you help me improve my language skills?";
    private static final String HISTORY_PROMPT = "I want to learn about history. What are some important events I should know about?";
    private static final String GEOGRAPHY_PROMPT = "I'm interested in geography. Can you teach me about world geography?";

    @FXML
    protected void handleBackButton() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("base-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
        ThemeManager.applyCurrentTheme(scene);
        stage.setScene(scene);
    }

    @FXML
    public void initialize() {
        // Initialize Ollama API (using localhost by default)
        ollamaAPI = new OllamaAPI();

        // Set up spinner as initially invisible
        thinkingSpinner.setVisible(false);

        // Display a welcome message
        displayMessage("Hello! I'm your AI tutor. How can I help you today?");

        // Set up the send button action
        sendButton.setOnAction(event -> sendMessage());

        // Set up to listen for Enter key on text field
        userInputField.setOnAction(event -> sendMessage());
    }

    @FXML
    protected void handleMathsButton() {
        sendPredefinedMessage(MATHS_PROMPT);
    }

    @FXML
    protected void handleEnglishButton() {
        sendPredefinedMessage(ENGLISH_PROMPT);
    }

    @FXML
    protected void handleHistoryButton() {
        sendPredefinedMessage(HISTORY_PROMPT);
    }

    @FXML
    protected void handleGeographyButton() {
        sendPredefinedMessage(GEOGRAPHY_PROMPT);
    }

    @FXML
    protected void handleClearChat() {
        // Clear chat history
        chatHistory.clear();
        // Reset the display
        responseTextFlow.getChildren().clear();
        displayMessage("Chat cleared. How can I help you today?");
    }

    private void sendPredefinedMessage(String message) {
        // Set the message in the input field (optional - for user to see what was sent)
        userInputField.setText(message);
        // Send the message
        sendMessage();
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
        Text userPrompt = new Text("\n\nYou: ");
        userPrompt.setFont(Font.font("System", FontWeight.BOLD, 14));
        Text userText = new Text(userMessage + "\n\n");
        userText.setFont(Font.font("System", 14));

        Platform.runLater(() -> {
            responseTextFlow.getChildren().addAll(userPrompt, userText);

            Text aiPrompt = new Text("AI Tutor: ");
            aiPrompt.setFont(Font.font("System", FontWeight.BOLD, 14));
            responseTextFlow.getChildren().add(aiPrompt);
        });

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
                    formatAndDisplayMarkdown(responseContent);
                    thinkingSpinner.setVisible(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    thinkingSpinner.setVisible(false);
                    Text errorText = new Text("Sorry, I encountered an error. " +
                            "Please check if the Ollama server is running with the gemma3:4b model. You can ensure your Ollama server is open by entering http://localhost:11434 in your browser. If you don't see \"Ollama is running\", check your firewall settings.");
                    errorText.setFont(Font.font("System", 14));
                    responseTextFlow.getChildren().add(errorText);
                    e.printStackTrace();
                });
            }
        }).start();
    }

    // Method to display regular text messages
    private void displayMessage(String message) {
        Text text = new Text(message);
        text.setFont(Font.font("System", 14));
        responseTextFlow.getChildren().add(text);
    }

    // Method to parse and format markdown in the AI's response
    private void formatAndDisplayMarkdown(String markdown) {
        // Pattern to match bold text (**text**)
        Pattern boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher boldMatcher = boldPattern.matcher(markdown);

        int lastEnd = 0;

        // Process the text
        while (boldMatcher.find()) {
            // Add regular text before the bold part
            if (boldMatcher.start() > lastEnd) {
                String regularText = markdown.substring(lastEnd, boldMatcher.start());
                Text regular = new Text(regularText);
                regular.setFont(Font.font("System", 14));
                responseTextFlow.getChildren().add(regular);
            }

            // Add bold text
            String boldText = boldMatcher.group(1);
            Text bold = new Text(boldText);
            bold.setFont(Font.font("System", FontWeight.BOLD, 14));
            responseTextFlow.getChildren().add(bold);

            lastEnd = boldMatcher.end();
        }

        // Add any remaining text after the last bold part
        if (lastEnd < markdown.length()) {
            String remainingText = markdown.substring(lastEnd);
            Text remaining = new Text(remainingText);
            remaining.setFont(Font.font("System", 14));
            responseTextFlow.getChildren().add(remaining);
        }
    }
}