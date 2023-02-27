module paum.opencv.paumtravailopencv {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;


    opens paum.opencv.paumtravailopencv to javafx.fxml;
    exports paum.opencv.paumtravailopencv;
}