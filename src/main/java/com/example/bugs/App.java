package com.example.bugs;

import com.example.bugs.HelloApplication;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        // Force headless mode if not in a display environment
        if (isHeadless() || (args != null && Arrays.asList(args).contains("--headless"))) {
            configureHeadlessMode();
        }

        try {
            HelloApplication.main(args);
        } catch (UnsupportedOperationException e) {
            handleHeadlessFailure(e);
        }
    }

    private static boolean isHeadless() {
        return System.getProperty("java.awt.headless") != null
                || System.getenv("DISPLAY") == null
                || System.getProperty("testfx.headless") != null;
    }

    private static void configureHeadlessMode() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("java.awt.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.verbose", "true"); // For debugging
    }

    private static void handleHeadlessFailure(UnsupportedOperationException e) {
        System.err.println("\nCRITICAL: Headless initialization failed!");
        System.err.println("Required solutions:");
        System.err.println("1. For Linux servers:");
        System.err.println("   sudo apt-get install xvfb libgtk2.0-0 libxtst6");
        System.err.println("   xvfb-run java -jar your-app.jar");
        System.err.println("2. For Docker:");
        System.err.println("   Add these to your Dockerfile:");
        System.err.println("   RUN apt-get update && apt-get install -y xvfb libgtk2.0-0 libxtst6");
        System.err.println("3. For CI/CD (GitHub Actions):");
        System.err.println("   Add this step before running Java:");
        System.err.println("   - run: sudo apt-get install xvfb libgtk2.0-0 libxtst6");
        System.err.println("   - run: xvfb-run mvn test");
        System.exit(1);
    }
}