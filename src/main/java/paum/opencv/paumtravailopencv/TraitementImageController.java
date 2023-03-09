package paum.opencv.paumtravailopencv;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static org.opencv.imgproc.Imgproc.LINE_8;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class TraitementImageController {
    //https://stackoverflow.com/questions/39463461/javafx-draw-image-inside-in-pane
    @FXML private ImageView imageOriginale = null;
    @FXML public ComboBox typeOperation;
    @FXML public ToggleButton toggleButtonCouleur;
    @FXML Accordion accordionParametreOperation;
    @FXML private Slider forceMatriceTransformation;
    @FXML private Label labelForceMatrice;
    public Mat matriceImageEnCouleur = new Mat();
    public String cheminImage;
    public Mat matriceImageEnGris = new Mat();
    private Mat matriceDeTransformation;
    private Mat matriceDestination;
    String choixOperation = null;
    private boolean imageEstEnGris = false;
    int taille;
    private boolean initialisationMenuConvolution = false;
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final RadioButton blurRadioButton = new RadioButton("Blur");
    private final RadioButton gaussianBlurRadioButton = new RadioButton("Gaussian Blur");
    private final RadioButton medianFilterRadioButton = new RadioButton("Median Filter");
    private final RadioButton bilateralFilterRadioButton = new RadioButton("Bilateral Filter");
    private final TitledPane titledPaneFilter = new TitledPane();
    private Random rng = new Random(12345);

    @FXML protected void metImageEnGrisOuCouleur(){
        if (cheminImage != null){
            if (toggleButtonCouleur.isSelected()){
                toggleButtonCouleur.setStyle("-fx-background-color:green");
                cvtColor(matriceImageEnCouleur, matriceImageEnGris, Imgproc.COLOR_RGB2GRAY);
                imageEstEnGris = true;
            }
            else{
                toggleButtonCouleur.setStyle("-fx-background-color:red");
                imageEstEnGris = false;
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
        imageEstEnGris = false;

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
            labelForceMatrice.setText(String.valueOf((int)forceMatriceTransformation.getValue()));
            accordionParametreOperation.getPanes().forEach(pane -> pane.setExpanded(false));
            accordionParametreOperation.setVisible(false);
            typeOperation.setValue(null);
        }
    }
    @FXML
    protected void initialisationChoix() {
        if (typeOperation.getItems().size() == 0) {
            typeOperation.getItems().addAll("Aucune","Convolution","Érosion","Dilatation","Ouverture","Fermeture","Filtre de Canny","Détection de contours","Détection de coins","Détection d'objets");
        }
    }
    @FXML
    protected void choixTypeOperation(){
        if (imageOriginale.getImage() != null){
            accordionParametreOperation.setVisible(true);
            int kernelSize = (int)Math.round(forceMatriceTransformation.getValue());
            Size size = new Size(2 * kernelSize + 1, 2 * kernelSize + 1);
            matriceDeTransformation =  Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, size,new Point(kernelSize, kernelSize));
            matriceDestination = new Mat();
            metAccordionAvecValeurParDefaut();
            if(typeOperation.getSelectionModel().getSelectedItem() != null){
                if(typeOperation.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("convolution")){
                    forceMatriceTransformation.minProperty().setValue(1);
                    forceMatriceTransformation.setValue(1);
                    labelForceMatrice.setText(String.valueOf((int)forceMatriceTransformation.getValue()));
                    taille = Integer.parseInt(labelForceMatrice.getText());
                }
                else {
                    forceMatriceTransformation.minProperty().setValue(0);
                    forceMatriceTransformation.setValue(0);
                    labelForceMatrice.setText(String.valueOf((int)forceMatriceTransformation.getValue()));
                    taille = Integer.parseInt(labelForceMatrice.getText());
                    toggleButtonCouleur.setVisible(true);
                }
            }
            timeline.stop();
            timeline.play();
        }
        else {
            typeOperation.getSelectionModel().clearSelection();
            typeOperation.setPromptText("Effectuer une opération");
        }
    }
    @FXML
    protected void sliderEstEntrainDeBouger(){
        labelForceMatrice.setText(String.valueOf((int)Math.round(forceMatriceTransformation.getValue())));
        taille = Integer.parseInt(labelForceMatrice.getText());
        timeline.stop();
        timeline.play();
    }
    //https://stackoverflow.com/questions/34771380/how-to-convert-a-javafx-image-to-a-opencv-matrix
    private static @NotNull Image mat2Image(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        //https://jenkov.com/tutorials/java-io/bytearrayinputstream.html
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
    protected void foldArccodion(){
        accordionParametreOperation.getPanes().forEach(pane -> pane.setExpanded(false));
    }
    private void initialisationMenuConvolution(){
        if (!initialisationMenuConvolution) {
            initialisationMenuConvolution = true;
            // Create a new titled pane
            titledPaneFilter.setText("Filtres de floues");
            // Create an anchor pane to hold the radio buttons
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefSize(209, 25);
            
            // Set the position of each button
            AnchorPane.setTopAnchor(blurRadioButton, 10.0);
            AnchorPane.setLeftAnchor(blurRadioButton, 10.0);

            AnchorPane.setTopAnchor(gaussianBlurRadioButton, 40.0);
            AnchorPane.setLeftAnchor(gaussianBlurRadioButton, 10.0);

            AnchorPane.setTopAnchor(medianFilterRadioButton, 70.0);
            AnchorPane.setLeftAnchor(medianFilterRadioButton, 10.0);

            AnchorPane.setTopAnchor(bilateralFilterRadioButton, 100.0);
            AnchorPane.setLeftAnchor(bilateralFilterRadioButton, 10.0);

            // Add the radio buttons to a toggle group

            blurRadioButton.setToggleGroup(toggleGroup);
            gaussianBlurRadioButton.setToggleGroup(toggleGroup);
            medianFilterRadioButton.setToggleGroup(toggleGroup);
            bilateralFilterRadioButton.setToggleGroup(toggleGroup);

            // Add the radio buttons to the anchor pane
            anchorPane.getChildren().addAll(blurRadioButton, gaussianBlurRadioButton, medianFilterRadioButton, bilateralFilterRadioButton);

            // Set the anchor pane as the content of the titled pane
            titledPaneFilter.setContent(anchorPane);
            // Add the titled pane to the accordion
            accordionParametreOperation.getPanes().add(titledPaneFilter);
        }
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
    private void metAccordionAvecValeurParDefaut(){
        toggleGroup.selectToggle(null);
        accordionParametreOperation.getPanes().get(0).setExpanded(false);
        if (initialisationMenuConvolution){
            accordionParametreOperation.getPanes().get(1).setExpanded(false);
        }
        forceMatriceTransformation.setValue(0);
        labelForceMatrice.setText(String.valueOf((int)forceMatriceTransformation.getValue()));
    }
    private void nombreOddAEven(){
        if (taille % 2 == 0) {
            taille++; // make the kernel size odd
        }
    }
    private Mat choixMatriceEntreCouleurEtGris() {
        if (toggleButtonCouleur.isSelected()){
            return matriceImageEnGris;
        }
        else {
            return matriceImageEnCouleur;
        }
    }

    private void resetAnimation(){
        timeline.stop();
        timeline.play();
    }

    private void operationAEffectuer() {
        final int RATIO = 3;
        final int KERNEL_SIZE = 3;
        if ( typeOperation.getSelectionModel().getSelectedItem() != null){
            choixOperation = typeOperation.getSelectionModel().getSelectedItem().toString().toLowerCase();
        }
        else {
            choixOperation = "";
        }

        Platform.runLater(() -> {
            initialisationMenuConvolution();
            accordionParametreOperation.getPanes().get(1).setVisible(choixOperation.equalsIgnoreCase("convolution"));

            if (choixOperation.equalsIgnoreCase("filtre de canny") || choixOperation.equalsIgnoreCase("détection de contours")){
                forceMatriceTransformation.minProperty().setValue(20);
                forceMatriceTransformation.maxProperty().setValue(175);
                labelForceMatrice.setText(String.valueOf((int)forceMatriceTransformation.getValue()));
                toggleButtonCouleur.setSelected(true);
                toggleButtonCouleur.setVisible(false);
                if (!imageEstEnGris){
                    metImageEnGrisOuCouleur();
                }
            }
            else {
                forceMatriceTransformation.setMax(20);
                toggleButtonCouleur.setVisible(true);
            }
        });

        Mat choixMatriceEntreCouleurEtGris = choixMatriceEntreCouleurEtGris();

        switch (choixOperation) {
            case "convolution":
                    blurRadioButton.setOnAction(e -> {
                        resetAnimation();
                    });

                    gaussianBlurRadioButton.setOnAction(e -> {
                        resetAnimation();
                    });

                    medianFilterRadioButton.setOnAction(e -> {
                        resetAnimation();
                    });

                    bilateralFilterRadioButton.setOnAction(e -> {
                        resetAnimation();
                    });

                    if (blurRadioButton.isSelected())
                    {
                        Imgproc.blur(choixMatriceEntreCouleurEtGris, matriceDestination, new Size(taille, taille), new Point(-1, -1));
                    }
                    else if (medianFilterRadioButton.isSelected())
                    {
                        nombreOddAEven();
                        Imgproc.medianBlur(choixMatriceEntreCouleurEtGris, matriceDestination, taille);
                    }
                    else if (bilateralFilterRadioButton.isSelected())
                    {
                        Imgproc.bilateralFilter(choixMatriceEntreCouleurEtGris, matriceDestination, taille, taille * 2, (double)taille / 2);
                    }
                    else if (gaussianBlurRadioButton.isSelected())
                    {
                        nombreOddAEven();
                        Imgproc.GaussianBlur(choixMatriceEntreCouleurEtGris, matriceDestination, new Size(taille, taille), 0, 0);
                    }
                    else {
                        matriceDestination = choixMatriceEntreCouleurEtGris;
                    }
                break;
            case "érosion":
                Imgproc.erode(choixMatriceEntreCouleurEtGris, matriceDestination, matriceDeTransformation);
                break;
            case "dilatation":
                Imgproc.dilate(choixMatriceEntreCouleurEtGris, matriceDestination, matriceDeTransformation);
                break;
            case "ouverture":
                Imgproc.erode(choixMatriceEntreCouleurEtGris, matriceDestination, matriceDeTransformation);
                Imgproc.dilate(matriceDestination, matriceDestination, matriceDeTransformation);
                break;
            case "fermeture":
                Imgproc.dilate(choixMatriceEntreCouleurEtGris, matriceDestination, matriceDeTransformation);
                Imgproc.erode(matriceDestination, matriceDestination, matriceDeTransformation);
                break;
            case "filtre de canny":
                Mat temp = new Mat();
                Imgproc.blur(choixMatriceEntreCouleurEtGris, temp, new Size(5, 5), new Point(-1, -1));
                Imgproc.Canny(temp, matriceDestination, (int)forceMatriceTransformation.getValue(), (int)forceMatriceTransformation.getValue() * RATIO, KERNEL_SIZE, false);
                break;
            case "détection de contours":
                Mat temp2 = new Mat();
                Imgproc.blur(choixMatriceEntreCouleurEtGris, temp2, new Size(5, 5), new Point(-1, -1));
                Imgproc.Canny(temp2, matriceDestination, (int)forceMatriceTransformation.getValue(), (int)forceMatriceTransformation.getValue() * RATIO, KERNEL_SIZE, false);
                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(matriceDestination, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
                Mat drawing = Mat.zeros(matriceDestination.size(), CvType.CV_8UC3);
                for (int i = 0; i < contours.size(); i++) {
                    Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
                    Imgproc.drawContours(drawing, contours, i, color, 2, LINE_8, hierarchy, 0, new Point());
                }
                matriceDestination = drawing;
                break;
            case "détection de coins":
                break;
            case "détection d'objets":
                break;
            default:
                if (accordionParametreOperation.isVisible()){
                    accordionParametreOperation.setVisible(false);
                }
                matriceDestination = choixMatriceEntreCouleurEtGris;
                break;
        }
    }
}