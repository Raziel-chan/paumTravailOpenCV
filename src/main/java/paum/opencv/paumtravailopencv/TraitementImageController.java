package paum.opencv.paumtravailopencv;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class TraitementImageController {
    @FXML
    private Label welcomeText;

    //https://stackoverflow.com/questions/39463461/javafx-draw-image-inside-in-pane
    @FXML private ImageView imageOriginale;

    @FXML public ChoiceBox typeOperation;


    @FXML
    protected void menuItemOuvertureFichier() {
        File imageChoisis;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choix d'une image pour faire différent traitements");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        imageChoisis = fileChooser.showOpenDialog(new Stage());

        if (imageChoisis != null){
            //https://stackoverflow.com/questions/25643098/how-to-set-fileimage-on-imageview
            imageOriginale.setImage(new Image(imageChoisis.getAbsolutePath()));
        }
    }

    @FXML
    protected void initialisationChoix() {
        if (typeOperation.getItems().size() == 0) {
            typeOperation.getItems().addAll("Aucune","Convolution","Érosion","Dilatation","Ouverture","Fermeture","Filtre de Canny","Détection de contours","Détection de coins","Détection d'Objets");
        }
    }

    @FXML
    protected void choixTypeOperation(){
        String choixOperation = typeOperation.getSelectionModel().getSelectedItem().toString().toLowerCase();
        System.out.println(typeOperation.getSelectionModel().getSelectedItem().toString());
        switch (choixOperation){
            case "aucune":
            case "convolution":
            case "érosion":
            case "dilatation":
        }

    }
}