package paum.opencv.paumtravailopencv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Core;

import java.io.IOException;

public class TraitementImageApplication extends Application {
    public Scene scene;
    //https://medium.com/@aadimator/how-to-set-up-opencv-in-intellij-idea-6eb103c1d45c
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    @Override
    public void start(@NotNull Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TraitementImageApplication.class.getResource("traitementImage-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1500, 700);
        stage.setTitle("Paum traitement image");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}