module com.example.javamail {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.javamail to javafx.fxml;
    exports com.example.javamail;
}