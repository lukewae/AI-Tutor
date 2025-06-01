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
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest; // Import ApplicationTest
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// No @ExtendWith(ApplicationExtension.class) needed when extending ApplicationTest
public abstract class FxTest extends ApplicationTest { // Extend ApplicationTest

    // ApplicationTest provides a stage to its start() method.
    // We can assign it to this field if our helper methods need to refer to 'this.stage'.
    // Alternatively, helper methods could take Stage as a parameter if needed,
    // or rely on FxRobot interacting with the current primary stage managed by ApplicationTest.
    protected Stage stage;
    protected FxRobot robot = new FxRobot(); // We can instantiate our own or use one from ApplicationTest if available/preferred

    /**
     * This is the start method from javafx.application.Application,
     * which ApplicationTest extends. TestFX will call this to set up the UI.
     *
     * @param stage The primary stage for this application, onto which
     *              the application scene can be set.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("LOG: FxTest (ApplicationTest) start: Entered. Provided stage from TestFX: " + stage);
        if (stage == null) {
            System.err.println("LOG: FATAL ERROR in FxTest start: TestFX provided a NULL stage!");
            throw new IllegalStateException("TestFX ApplicationTest did not provide a valid Stage to start method.");
        }
        this.stage = stage; // Assign the TestFX-provided stage to our instance variable
        System.out.println("LOG: FxTest (ApplicationTest) start: this.stage (class field) assigned: " + this.stage);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root); // Let HBox in hello-view.fxml determine initial size
        System.out.println("LOG: FxTest (ApplicationTest) start: Initial scene created for hello-view.fxml.");

        applyThemeAndStyles(scene);

        this.stage.setTitle(HelloApplication.TITLE);
        this.stage.setScene(scene);
        this.stage.setResizable(false); // As per your HelloApplication
        this.stage.show();
        this.stage.toFront(); // Ensure window is in front
        System.out.println("LOG: FxTest (ApplicationTest) start: Stage shown. Title: " + this.stage.getTitle());
        System.out.println("LOG: FxTest (ApplicationTest) start: Exiting start method successfully.");
    }

    @BeforeAll
    public static void setupSpec() {
        System.out.println("LOG: FxTest @BeforeAll setupSpec called.");
        // Optional: Headless mode for CI servers
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
            System.err.println("CRITICAL: CSS themes.css not found in applyThemeAndStyles!");
            // Consider throwing an exception here to fail tests fast if CSS is critical
            return;
        }
        String cssPath = cssResource.toExternalForm();
        scene.getStylesheets().add(cssPath);
        ThemeManager.applyCurrentTheme(scene);
    }

    protected <T extends Parent> T loadFxml(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
        return fxmlLoader.load();
    }

    @SuppressWarnings("unchecked")
    protected <C> C getControllerFromStage() {
        // Note: This assumes you store the controller in the root node's properties.
        // If not, this method won't work as expected.
        if (this.stage == null || this.stage.getScene() == null || this.stage.getScene().getRoot() == null) {
            System.err.println("ERROR in getControllerFromStage: Stage, scene, or root is null.");
            return null;
        }
        return (C) this.stage.getScene().getRoot().getProperties().get("controller");
    }

    protected <C> C loadFxmlAndGetController(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
        // Store the controller in the root node's properties if getControllerFromStage is to be used
        Parent root = fxmlLoader.load();
        C controller = fxmlLoader.getController();
        if (root != null) { // It's good practice to set properties on the root after loading
            root.getProperties().put("controller", controller);
        }
        return controller;
    }

    protected void setSceneFromFxml(String fxmlPath, int width, int height) throws IOException {
        if (this.stage == null) {
            System.err.println("ERROR in setSceneFromFxml(width,height): this.stage is null!");
            throw new IllegalStateException("Stage is null, cannot set scene.");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, width, height);
        applyThemeAndStyles(scene);
        this.stage.setScene(scene);
    }

    protected void setSceneFromFxml(String fxmlPath) throws IOException {
        if (this.stage == null) {
            System.err.println("ERROR in setSceneFromFxml(path): this.stage is null!");
            throw new IllegalStateException("Stage is null, cannot set scene.");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root); // Let FXML define size
        applyThemeAndStyles(scene);
        this.stage.setScene(scene);
    }

    @AfterEach
    public void tearDownEachTest() { // Renamed for clarity from potential ApplicationTest.afterEachTest()
        System.out.println("LOG: FxTest @AfterEach tearDownEachTest called.");
        WaitForAsyncUtils.waitForFxEvents();
    }

    public <T extends Node> T find(final String query) {
        // robot.lookup() operates on the current scene graph of the primary stage
        // ApplicationTest manages this primary stage context.
        return this.robot.lookup(query).query();
    }

    @SuppressWarnings("unchecked")
    protected <T extends Node> T waitForNode(final String query, long timeoutSeconds) {
        final Node[] foundNode = new Node[1];
        Callable<Boolean> nodeIsPresentAndVisible = () -> {
            try {
                Node node = this.robot.lookup(query).query();
                if (node != null && node.isVisible()) {
                    foundNode[0] = node;
                    return true;
                }
                return false;
            } catch (org.testfx.service.query.EmptyNodeQueryException e) {
                return false;
            } catch (Exception e) {
                System.err.println("waitForNode: Unexpected exception during lookup for query '" + query + "': " + e.getMessage());
                // e.printStackTrace(); // Potentially too verbose for every retry
                return false;
            }
        };

        waitForCondition(nodeIsPresentAndVisible,
                "Node with query '" + query + "' did not become present and visible within " + timeoutSeconds + " seconds.",
                timeoutSeconds);

        if (foundNode[0] == null) {
            throw new AssertionError("waitForNode: Node found was null after condition met (this should not happen if condition returned true) for query: " + query);
        }
        return (T) foundNode[0];
    }

    protected void waitForCondition(Callable<Boolean> condition, String message, long timeoutSeconds) {
        try {
            WaitForAsyncUtils.waitFor(timeoutSeconds, TimeUnit.SECONDS, condition);
        } catch (TimeoutException e) {
            throw new AssertionError(message, e);
        }
    }

    protected void waitForCondition(java.util.function.BooleanSupplier conditionSupplier, String message, long timeoutSeconds) {
        Callable<Boolean> conditionCallable = conditionSupplier::getAsBoolean;
        waitForCondition(conditionCallable, message, timeoutSeconds);
    }
}