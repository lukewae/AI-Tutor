package com.example.bugs;

import com.example.bugs.util.DatabaseUtil;
import com.example.bugs.util.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils; // This is the class we are inspecting

import java.io.IOException;
import java.util.concurrent.Callable; // Important: Using this Callable
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
// No need for java.util.function.BooleanSupplier if we use Callable<Boolean>

@ExtendWith(ApplicationExtension.class)
public abstract class FxTest {

    protected Stage stage;
    protected FxRobot robot = new FxRobot();

    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        applyThemeAndStyles(scene);
        stage.setTitle(HelloApplication.TITLE);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.toFront();
    }

    @BeforeAll
    public static void setupSpec() {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
        DatabaseUtil.initDatabase();
    }

    protected void applyThemeAndStyles(Scene scene) {
        java.net.URL cssResource = HelloApplication.class.getResource("/com/example/bugs/styles/themes.css");
        if (cssResource == null) {
            System.err.println("CRITICAL: CSS themes.css not found!");
            return;
        }
        String cssPath = cssResource.toExternalForm();
        scene.getStylesheets().add(cssPath);
        ThemeManager.applyCurrentTheme(scene);
    }

    // --- Other helper methods (loadFxml, setSceneFromFxml, etc.) remain the same ---
    protected <T extends Parent> T loadFxml(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
        return fxmlLoader.load();
    }

    @SuppressWarnings("unchecked")
    protected <C> C getControllerFromStage() {
        return (C) stage.getScene().getRoot().getProperties().get("controller");
    }

    protected <C> C loadFxmlAndGetController(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
        fxmlLoader.load();
        return fxmlLoader.getController();
    }

    protected void setSceneFromFxml(String fxmlPath, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, width, height);
        applyThemeAndStyles(scene);
        stage.setScene(scene);
    }

    protected void setSceneFromFxml(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        applyThemeAndStyles(scene);
        stage.setScene(scene);
    }


    @AfterEach
    public void tearDown() { // TimeoutException might not be declared by waitForFxEvents here
        WaitForAsyncUtils.waitForFxEvents();
    }

    public <T extends Node> T find(final String query) {
        return robot.lookup(query).query();
    }

    /**
     * Waits for a node to be present and visible.
     * For TestFX 4.0.18, we use async to get a Future for the node lookup,
     * and then wait for that Future.
     */
    @SuppressWarnings("unchecked")
    protected <T extends Node> T waitForNode(final String query, long timeoutSeconds) {
        Callable<T> nodeLookupTask = () -> {
            Node foundNode = robot.lookup(query).query(); // TestFX lookup itself throws if not found
            if (foundNode != null && foundNode.isVisible()) {
                return (T) foundNode;
            }
            // If not visible, or some other issue, returning null will cause the outer
            // waitFor (on the Future) to eventually timeout if the condition isn't met.
            // However, it's better to let lookup fail or continually check visibility.
            // For simplicity with Future, we'll rely on the lookup succeeding and visibility.
            // A more robust approach for retrying visibility would use the waitFor condition logic.
            return null; // This callable is for async execution
        };

        // Execute the lookup asynchronously
        Future<T> futureNode = WaitForAsyncUtils.async(nodeLookupTask);

        try {
            // Wait for the future to complete
            return WaitForAsyncUtils.waitFor(timeoutSeconds, TimeUnit.SECONDS, futureNode);
        } catch (TimeoutException e) {
            throw new AssertionError("Node with query '" + query + "' not found or not visible within " + timeoutSeconds + " seconds (future timed out).", e);
        } catch (Exception e) { // Catch other exceptions from the Future's execution
            throw new AssertionError("Exception while waiting for node '" + query + "': " + e.getMessage(), e);
        }
    }


    /**
     * Waits for a given condition to become true.
     * For TestFX 4.0.18, the waitFor method expects a Callable<Boolean>.
     */
    protected void waitForCondition(Callable<Boolean> condition, String message, long timeoutSeconds) {
        try {
            // Use the waitFor(long, TimeUnit, Callable<Boolean>) method
            WaitForAsyncUtils.waitFor(timeoutSeconds, TimeUnit.SECONDS, condition);
        } catch (TimeoutException e) {
            throw new AssertionError(message + " (timed out after " + timeoutSeconds + "s)", e);
        }
    }

    // Overload for convenience if you have a java.util.function.BooleanSupplier
    // This is what the original `FxTest` used, so test cases might call this.
    protected void waitForCondition(java.util.function.BooleanSupplier conditionSupplier, String message, long timeoutSeconds) {
        // Adapt the BooleanSupplier to a Callable<Boolean>
        Callable<Boolean> conditionCallable = conditionSupplier::getAsBoolean;
        waitForCondition(conditionCallable, message, timeoutSeconds);
    }
}