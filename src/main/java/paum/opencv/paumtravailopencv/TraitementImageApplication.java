package paum.opencv.paumtravailopencv;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        scene = new Scene(fxmlLoader.load(), 1500, 700);
        stage.setTitle("Paum traitement image");
        stage.setScene(scene);
        stage.show();

        alert.setTitle("Détection de contours");
        alert.setHeaderText("Information Alert");
        String s ="Pour la détection de contours, il se peux que cela prend quelques minutes dûe au temps de calcul. surtout si l'image est en haute résolution.";
        alert.setContentText(s);
        // Create a pause transition of 0.3 seconds
        PauseTransition delay = new PauseTransition(Duration.millis(300));
        delay.setOnFinished(event -> alert.show());

        // Start the delay
        delay.play();
    }

    public static void main(String[] args) {
        launch();
    }
}