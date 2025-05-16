module pl.gornik.sudokufx {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.gornik.sudokufx to javafx.fxml;
    exports pl.gornik.sudokufx;
}