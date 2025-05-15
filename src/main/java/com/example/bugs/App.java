import com.example.bugs.HelloApplication;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
        }
        HelloApplication.main(args);
    }
}