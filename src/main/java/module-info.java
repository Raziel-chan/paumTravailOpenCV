module paum.opencv.paumtravailopencv {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;
    requires org.jetbrains.annotations;


    opens paum.opencv.paumtravailopencv to javafx.fxml;
    exports paum.opencv.paumtravailopencv;
}