module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.model;
    exports com.example.demo.engine;

}