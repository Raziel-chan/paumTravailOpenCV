package paum.opencv.paumtravailopencv;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class TraitementImageController {

    //https://stackoverflow.com/questions/39463461/javafx-draw-image-inside-in-pane
    @FXML private ImageView imageOriginale = null;

    @FXML public ComboBox typeOperation;

    @FXML public ToggleButton toggleButtonCouleur;
    @FXML public Mat matriceImageEnCouleur = null;

    @FXML public String cheminImage;
    @FXML public Mat matriceImageEnGris = new Mat();

    //https://stackoverflow.com/questions/34771380/how-to-convert-a-javafx-image-to-a-opencv-matrix
    private static Image mat2Image(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        //https://jenkov.com/tutorials/java-io/bytearrayinputstream.html
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
    @FXML protected void metImageEnGrisOuCouleur(){
        if (cheminImage != null){
            if (toggleButtonCouleur.isSelected()){
                toggleButtonCouleur.setStyle("-fx-background-color:green");
                Imgproc.cvtColor(matriceImageEnCouleur, matriceImageEnGris, Imgproc.COLOR_RGB2GRAY);
            }
            else{
                toggleButtonCouleur.setStyle("-fx-background-color:red");
            }
            timeline.stop();
            timeline.play();
        }
    }


    @FXML
    protected void menuItemOuvertureFichier() {
        File imageChoisis;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choix d'une image pour faire différent traitements");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        imageChoisis = fileChooser.showOpenDialog(new Stage());

        if (imageChoisis != null){
            cheminImage = imageChoisis.getAbsolutePath();
            //https://stackoverflow.com/questions/25643098/how-to-set-fileimage-on-imageview
            imageOriginale.setImage(new Image(cheminImage));
            matriceImageEnCouleur = Imgcodecs.imread(cheminImage);

            if (toggleButtonCouleur.isSelected()){
                toggleButtonCouleur.setSelected(false);
                toggleButtonCouleur.setStyle("-fx-background-color:red");
            }

            forceMatriceTransformation.setValue(0);
            accordionParametreOperation.getPanes().forEach(pane -> pane.setExpanded(false));

        }
    }

    @FXML
    protected void initialisationChoix() {
        if (typeOperation.getItems().size() == 0) {
            typeOperation.getItems().addAll("Aucune","Convolution","Érosion","Dilatation","Ouverture","Fermeture","Filtre de Canny","Détection de contours","Détection de coins","Détection d'objets");
        }
    }

    String choixOperation = null;

    @FXML Accordion accordionParametreOperation;

    @FXML
    protected void choixTypeOperation(){
        if (imageOriginale.getImage() != null){
            accordionParametreOperation.setVisible(true);
            int kernelSize = (int)Math.round(forceMatriceTransformation.getValue());
            Size size = new Size(2 * kernelSize + 1, 2 * kernelSize + 1);
            matriceDeTransformation =  Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, size,new Point(kernelSize, kernelSize));
            matriceDestination = new Mat();
            timeline.stop();
            timeline.play();
        }
        else {
            typeOperation.getSelectionModel().clearSelection();
            typeOperation.setPromptText("Effectuer une opération");
        }

    }

    @FXML private Slider forceMatriceTransformation;

    @FXML private Label labelForceMatrice;

    @FXML private Mat matriceDeTransformation;

    @FXML private Mat matriceDestination;

    @FXML protected void sliderABouger(){
        labelForceMatrice.setText(String.valueOf((int)Math.round(forceMatriceTransformation.getValue())));
    }

    @FXML protected void faireOperationUneFoisSliderArreter(){
        labelForceMatrice.setText(String.valueOf((int)Math.round(forceMatriceTransformation.getValue())));
        timeline.stop();
        timeline.play();
    }

    private final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
        // Perform the erode operation on a background thread
        CompletableFuture.supplyAsync(() -> {
            int kernelSize = (int)Math.round(forceMatriceTransformation.getValue());
            Size size = new Size(2 * kernelSize + 1, 2 * kernelSize + 1);
            matriceDeTransformation =  Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, size,new Point(kernelSize, kernelSize));
            matriceDestination = new Mat();

            operationAEffectuer();

            return mat2Image(matriceDestination);
        }).thenAccept(image -> {
            // Update the UI with the result of the erode operation
            Platform.runLater(() -> {
                imageOriginale.setImage(image);
            });
        });
    }));


    private void operationAEffectuer() {
        if ( typeOperation.getSelectionModel().getSelectedItem() != null){
            choixOperation = typeOperation.getSelectionModel().getSelectedItem().toString().toLowerCase();
        }
        else {
            choixOperation = "";
        }

        switch (choixOperation) {
            case "convolution":
            case "érosion":
                if (toggleButtonCouleur.isSelected()){
                    Imgproc.erode(matriceImageEnGris, matriceDestination, matriceDeTransformation);
                }
                else {
                    Imgproc.erode(matriceImageEnCouleur, matriceDestination, matriceDeTransformation);
                }
                break;
            case "dilatation":
                if (toggleButtonCouleur.isSelected()){
                    Imgproc.dilate(matriceImageEnGris, matriceDestination, matriceDeTransformation);
                }
                else {
                    Imgproc.dilate(matriceImageEnCouleur, matriceDestination, matriceDeTransformation);
                }
                break;
            case "ouverture":
            case "fermeture":
            case "filtre de canny":
            case "détection de contours":
            case "détection de coins":
            case "détection d'objets":
            default:
                accordionParametreOperation.setVisible(false);
                if (toggleButtonCouleur.isSelected()){
                    imageOriginale.setImage(mat2Image(matriceImageEnGris));
                }
                else{
                    imageOriginale.setImage(mat2Image(matriceImageEnCouleur));
                }
                break;
        }
    }
}