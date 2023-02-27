package paum.opencv.paumtravailopencv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.File;
import java.io.IOException;

public class TraitementImageApplication extends Application {
    public Scene scene;
    //https://medium.com/@aadimator/how-to-set-up-opencv-in-intellij-idea-6eb103c1d45c
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TraitementImageApplication.class.getResource("traitementImage-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1920, 1080);
        stage.setTitle("Paum traitement image");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
        System.out.println("OpenCV Mat: " + m);
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println("OpenCV Mat data:\n" + m.dump());
        launch();
    }
}