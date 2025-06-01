package com.example.bugs.controller;

import com.example.bugs.FxTest; // Your corrected base class
import com.example.bugs.HelloApplication;
import com.example.bugs.dao.UserDAO;
import com.example.bugs.model.SignUp;
// import com.example.bugs.util.ThemeManager; // Not directly used here, but FxTest uses it

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatResponseModel;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatRequest; // Ensure this is imported for mock verification

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.Callable; // For use with the new waitForCondition

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.testfx.util.WaitForAsyncUtils;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NavigationAndChatbotTest extends FxTest { // Extends your FxTest

    private static final String TEST_EMAIL = "dom@test.com";
    private static final String TEST_PASSWORD = "test";

    @BeforeAll
    public static void setupClass() {
        // Create a test user if it doesn't exist for login-dependent tests
        // This runs once for all tests in this class
        UserDAO userDAO = new UserDAO();
        if (!userDAO.emailExists(TEST_EMAIL)) {
            userDAO.createUser(new SignUp("Test", "NavUser", TEST_EMAIL, TEST_PASSWORD));
        }
    }

    @BeforeEach
    void clearFieldsOnly() {
        WaitForAsyncUtils.waitForFxEvents();

        // Try to find #emailField. If @Start failed, this will fail, and that's okay for now.
        // This @BeforeEach is now more about clearing if the view IS present.
        try {
            // waitForNode will throw if the node isn't found and visible after timeout
            final TextField emailFieldNode = waitForNode("#emailField", 2); // Short timeout, @Start should make it available quickly
            final TextField passwordFieldNode = waitForNode("#passwordField", 2);

            Platform.runLater(() -> {
                ((TextField) emailFieldNode).clear();
                if (passwordFieldNode instanceof javafx.scene.control.PasswordField) {
                    ((javafx.scene.control.PasswordField) passwordFieldNode).clear();
                } else {
                    ((TextField) passwordFieldNode).clear();
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        } catch (AssertionError e) { // Catch if waitForNode times out
            System.out.println("WARN: @BeforeEach - Could not find/clear email/password fields. Scene might not be ready or error in @Start. Details: " + e.getMessage());
            // We can check if FxTest.stage is null here too, to confirm @Start status
            if (stage == null) {
                System.out.println("ERROR: @BeforeEach confirms FxTest.stage is still NULL!");
            } else {
                System.out.println("INFO: @BeforeEach: FxTest.stage is NOT null. Current scene: " + stage.getScene());
            }
        }
    }

    private void loginTestUser() {
        // Wait for the node and then cast it to TextField to use .clear()
        TextField emailField = waitForNode("#emailField", 5); // Returns Node, cast below
        TextField passwordField = waitForNode("#passwordField", 5); // Returns Node, cast below

        // It's safer to perform UI modifications on the JavaFX Application Thread
        Platform.runLater(() -> {
            ((TextField) emailField).clear(); // Cast to TextField
            ((TextField) passwordField).clear(); // Cast to TextField, or PasswordField if that's its fx:id target
        });
        WaitForAsyncUtils.waitForFxEvents(); // Wait for the clear operations to complete

        robot.clickOn(emailField).write(TEST_EMAIL);
        robot.clickOn(passwordField).write(TEST_PASSWORD);
        robot.clickOn("#signInButton");
        WaitForAsyncUtils.waitForFxEvents();
        waitForNode("#studyButton", 10);
        assertNotNull(find("#studyButton"), "Should navigate to BaseView after login");
    }
    @Test
    @Order(1)
    void testNavigateToSignUpAndBackToSignIn() {
        waitForNode("#signUpButton", 5);
        robot.clickOn("#signUpButton");
        WaitForAsyncUtils.waitForFxEvents();
        waitForNode("#firstNameField", 5);
        assertNotNull(find("#firstNameField"), "Should be on Sign Up page (firstNameField found)");

        // Assumes fx:id="signInButton" on the "Already have an account? Sign In" button in signup-view.fxml
        waitForNode("#signInButton", 5);
        robot.clickOn("#signInButton");
        WaitForAsyncUtils.waitForFxEvents();
        waitForNode("#emailField", 5);
        assertNotNull(find("#emailField"), "Should be back on Login page");
    }

    @Test
    @Order(2)
    void testNavigateFromBaseToSettingsAndBack() {
        loginTestUser();

        waitForNode("#settingsButton", 5);
        robot.clickOn("#settingsButton");
        WaitForAsyncUtils.waitForFxEvents();
        waitForNode("#darkModeToggle", 5);
        Label settingsHeader = find(".header-text"); // Assumes styleClass="header-text" on Settings title Label
        assertTrue(settingsHeader.getText().contains("Settings"), "Should be on Settings page");

        waitForNode("#backButton", 5); // Back button in settings-view.fxml
        robot.clickOn("#backButton");
        WaitForAsyncUtils.waitForFxEvents();
        waitForNode("#studyButton", 5);
        assertNotNull(find("#studyButton"), "Should be back on Base page");
    }

    @Test
    @Order(3)
    void testNavigateFromBaseToStudyAndBack() {
        loginTestUser();

        waitForNode("#studyButton", 5);
        robot.clickOn("#studyButton");
        WaitForAsyncUtils.waitForFxEvents();
        waitForNode("#userInputField", 5);
        assertEquals(800, stage.getScene().getWidth(), 0.1, "Stage width should be 800 for study view");
        assertEquals(600, stage.getScene().getHeight(), 0.1, "Stage height should be 600 for study view");
        assertNotNull(find("#mathsButton"), "Maths button should be visible on Chatbot page");

        waitForNode("#backButton", 5); // Back button in study-view.fxml (chatbot)
        robot.clickOn("#backButton");
        WaitForAsyncUtils.waitForFxEvents();
        waitForNode("#studyButton", 5);
        assertEquals(HelloApplication.WIDTH, stage.getScene().getWidth(), 0.1, "Stage width should be restored");
        assertEquals(HelloApplication.HEIGHT, stage.getScene().getHeight(), 0.1, "Stage height should be restored");
        assertNotNull(find("#studyButton"), "Should be back on Base page from Study");
    }

    @Test
    @Order(4)
    void testLogOutFromBaseView() {
        loginTestUser();

        waitForNode("#logOutButton", 5);
        robot.clickOn("#logOutButton");
        WaitForAsyncUtils.waitForFxEvents();
        waitForNode("#emailField", 5);
        assertNotNull(find("#emailField"), "Should be on Login page after logout");
    }

//    @Test
//    @Order(5)
//    void testChatbotSendMessageAndReceive() throws Exception {
//        OllamaAPI mockApi = mock(OllamaAPI.class);
//        OllamaChatResult mockResult = mock(OllamaChatResult.class);
//        OllamaChatResponseModel mockResponseModel = mock(OllamaChatResponseModel.class);
//        // OllamaChatMessage requires role and content in constructor
//        OllamaChatMessage mockResponseMessage = new OllamaChatMessage(OllamaChatMessageRole.ASSISTANT, "Mocked AI response here.");
//
//        when(mockResponseModel.getMessage()).thenReturn(mockResponseMessage);
//        when(mockResult.getResponseModel()).thenReturn(mockResponseModel);
//        // Ensure the argument matcher for OllamaChatRequest is correctly imported and used
//        when(mockApi.chat(any(OllamaChatRequest.class))).thenReturn(mockResult);
//
//        loginTestUser(); // Logs in, lands on base-view
//
//        // Navigate to study view and inject mock
//        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("study-view.fxml"));
//        Parent chatRoot = loader.load();
//        ChatbotController chatbotController = loader.getController();
//        assertNotNull(chatbotController, "ChatbotController should not be null after loading study-view.fxml");
//        chatbotController.setOllamaAPI(mockApi); // Inject the mock
//
//        // Set the scene on the JavaFX Application Thread
//        Platform.runLater(() -> {
//            Scene chatScene = new Scene(chatRoot, 800, 600);
//            applyThemeAndStyles(chatScene); // Helper from FxTest
//            stage.setScene(chatScene);
//            stage.setResizable(true); // As per BaseController's onStudyButtonClick
//        });
//        WaitForAsyncUtils.waitForFxEvents(); // Wait for scene change
//        waitForNode("#userInputField", 5); // Ensure chatbot UI is loaded
//
//        String userMessage = "Hello AI from test";
//        TextField userInputField = find("#userInputField");
//        Button sendButton = find("#sendButton"); // ToggleButton can be clicked like a Button
//        Label responseLabel = find("#responseLabel");
//        ProgressIndicator thinkingSpinner = find("#thinkingSpinner");
//
//        String initialResponseText = responseLabel.getText();
//
//        robot.clickOn(userInputField).write(userMessage);
//        robot.clickOn(sendButton);
//        WaitForAsyncUtils.waitForFxEvents(); // Allow UI to update immediately (e.g., spinner visibility change)
//
//        // Wait for spinner to become visible using the Callable<Boolean> version of waitForCondition
//        Callable<Boolean> spinnerVisibleCondition = () -> thinkingSpinner.isVisible();
//        waitForCondition(spinnerVisibleCondition, "Spinner did not become visible", 5);
//        assertTrue(thinkingSpinner.isVisible(), "Spinner should be visible while thinking.");
//
//        // Wait for spinner to become invisible (response received and processed)
//        Callable<Boolean> spinnerNotVisibleCondition = () -> !thinkingSpinner.isVisible();
//        waitForCondition(spinnerNotVisibleCondition, "Spinner did not disappear", 10);
//        assertFalse(thinkingSpinner.isVisible(), "Spinner should be hidden after response.");
//
//        assertEquals("", userInputField.getText(), "User input field should be cleared.");
//
//        String expectedText = initialResponseText +
//                "\n\nYou: " + userMessage +
//                "\n\nAI Tutor: Mocked AI response here.";
//        // Normalize line endings for robust comparison
//        assertEquals(expectedText.replace("\r\n", "\n"), responseLabel.getText().replace("\r\n", "\n"),
//                "Response label should contain user message and AI response.");
//
//        verify(mockApi).chat(any(OllamaChatRequest.class));
//    }
}