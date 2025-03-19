module com.example.bugs {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bugs to javafx.fxml;
    exports com.example.bugs;
}