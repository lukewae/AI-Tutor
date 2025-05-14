package com.example.bugs;

import com.example.bugs.HelloApplication;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        if (args != null && Arrays.asList(args).contains("--headless")) {
            // Only the essential properties needed for headless mode
            System.setProperty("java.awt.headless", "true");
            System.setProperty("glass.platform", "Monocle");
            System.setProperty("prism.order", "sw");
        }
        HelloApplication.main(args);
    }
}