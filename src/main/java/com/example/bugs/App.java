package com.example.bugs;  // Make sure this matches your directory structure!

import com.example.bugs.HelloApplication;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        // Configure headless mode if requested
        if (args != null && Arrays.asList(args).contains("--headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("java.awt.headless", "true");
        }

        // Launch the JavaFX application
        HelloApplication.main(args);
    }
}