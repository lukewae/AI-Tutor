package com.example.bugs;

import com.example.bugs.HelloApplication;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        // Enhanced headless configuration
        if (args != null && Arrays.asList(args).contains("--headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("headless.geometry", "1600x1200-32"); // Virtual display size
        }

        try {
            HelloApplication.main(args);
        } catch (UnsupportedOperationException e) {
            if (e.getMessage().contains("Unable to open DISPLAY")) {
                System.err.println("ERROR: Headless mode failed. Try these solutions:");
                System.err.println("1. Add '--headless' argument");
                System.err.println("2. Install Xvfb: 'sudo apt-get install xvfb'");
                System.err.println("3. Run with: 'xvfb-run java -jar your-app.jar'");
            }
            System.exit(1);
        }
    }
}