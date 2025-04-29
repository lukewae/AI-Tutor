module com.example.addressbook {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ollama4j;
    requires java.net.http;
    requires org.slf4j;

    opens com.example.bugs to javafx.fxml;
    exports com.example.bugs;
    exports com.example.bugs.controller;
    opens com.example.bugs.controller to javafx.fxml;
    exports com.example.bugs.model;
    opens com.example.bugs.model to javafx.fxml;
}