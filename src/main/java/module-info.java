module com.dlsc.scrolling {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.dlsc.scrolling to javafx.fxml;
    exports com.dlsc.scrolling;
    exports com.dlsc.scrolling.util;
    opens com.dlsc.scrolling.util to javafx.fxml;
}