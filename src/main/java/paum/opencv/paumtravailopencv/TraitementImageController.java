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
    @FXML public Mat matriceImageEnCouleur = new Mat();

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

    @FXML protected void foldArccodion(){
        accordionParametreOperation.getPanes().forEach(pane -> pane.setExpanded(false));
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
            initialisationMenuConvolution();
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
   private RadioButton blurRadioButton = new RadioButton("Blur");
    private RadioButton gaussianBlurRadioButton = new RadioButton("Gaussian Blur");
    private RadioButton medianFilterRadioButton = new RadioButton("Median Filter");
    private RadioButton bilateralFilterRadioButton = new RadioButton("Bilateral Filter");
    private TitledPane titledPaneFilter = new TitledPane();

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

    @FXML private Slider forceMatriceTransformation;

    @FXML private Label labelForceMatrice;

    @FXML private Mat matriceDeTransformation;

    @FXML private Mat matriceDestination;
    int taille;

    @FXML protected void sliderEstEntrainDeBouger(){
        labelForceMatrice.setText(String.valueOf((int)Math.round(forceMatriceTransformation.getValue())));
        taille = Integer.parseInt(labelForceMatrice.getText());
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
    private void metAccordionAvecValeurParDefaut(){
        toggleGroup.selectToggle(null);
        accordionParametreOperation.getPanes().get(0).setExpanded(false);
        if (initialisationMenuConvolution){
            accordionParametreOperation.getPanes().get(1).setExpanded(false);
        }
        forceMatriceTransformation.setValue(0);
        labelForceMatrice.setText(String.valueOf((int)forceMatriceTransformation.getValue()));
    }

    private boolean initialisationMenuConvolution = false;
    private ToggleGroup toggleGroup = new ToggleGroup();

    private void nombreOddAEven(){
        if (taille % 2 == 0) {
            taille++; // make the kernel size odd
        }
    }

    private void operationAEffectuer() {
        if ( typeOperation.getSelectionModel().getSelectedItem() != null){
            choixOperation = typeOperation.getSelectionModel().getSelectedItem().toString().toLowerCase();
        }
        else {
            choixOperation = "";
        }
        switch (choixOperation) {
            case "convolution":
                    blurRadioButton.setOnAction(e -> {
                        timeline.stop();
                        timeline.play();
                    });

                    gaussianBlurRadioButton.setOnAction(e -> {
                        timeline.stop();
                        timeline.play();
                    });

                    medianFilterRadioButton.setOnAction(e -> {
                        timeline.stop();
                        timeline.play();
                    });

                    bilateralFilterRadioButton.setOnAction(e -> {
                        timeline.stop();
                        timeline.play();
                    });

                    accordionParametreOperation.getPanes().get(1).setVisible(true);
                    if (blurRadioButton.isSelected())
                    {
                        if (toggleButtonCouleur.isSelected()) {
                            Imgproc.blur(matriceImageEnGris, matriceDestination, new Size(taille, taille), new Point(-1, -1));
                        } else {
                            Imgproc.blur(matriceImageEnCouleur, matriceDestination, new Size(taille, taille), new Point(-1, -1));
                        }
                    }
                    else if (medianFilterRadioButton.isSelected())
                    {
                        nombreOddAEven();
                        if (toggleButtonCouleur.isSelected()) {
                            Imgproc.medianBlur(matriceImageEnGris, matriceDestination, taille);
                        } else {
                            Imgproc.medianBlur(matriceImageEnCouleur, matriceDestination, taille);
                        }
                    }
                    else if (bilateralFilterRadioButton.isSelected())
                    {
                        if (toggleButtonCouleur.isSelected()) {
                            Imgproc.bilateralFilter(matriceImageEnGris, matriceDestination, taille, taille * 2, (double)taille / 2);
                        } else {
                            Imgproc.bilateralFilter(matriceImageEnCouleur, matriceDestination, taille, taille * 2, (double)taille / 2);
                        }
                    }
                    else if (gaussianBlurRadioButton.isSelected())
                    {
                        nombreOddAEven();
                        if (toggleButtonCouleur.isSelected()) {
                            Imgproc.GaussianBlur(matriceImageEnGris, matriceDestination, new Size(taille, taille), 0, 0);
                        } else {
                            Imgproc.GaussianBlur(matriceImageEnCouleur, matriceDestination, new Size(taille, taille), 0, 0);
                        }
                    }
                    else {
                        if (toggleButtonCouleur.isSelected()) {
                            matriceDestination = matriceImageEnGris;
                        } else {
                            matriceDestination = matriceImageEnCouleur;
                        }
                    }
                break;
            case "érosion":
                //set les filtres en invisible
                Platform.runLater(() -> {
                    accordionParametreOperation.getPanes().get(1).setVisible(false);
                });
                if (toggleButtonCouleur.isSelected()) {
                    Imgproc.erode(matriceImageEnGris, matriceDestination, matriceDeTransformation);
                } else {
                    Imgproc.erode(matriceImageEnCouleur, matriceDestination, matriceDeTransformation);
                }
                break;
            case "dilatation":
                Platform.runLater(() -> {
                    accordionParametreOperation.getPanes().get(1).setVisible(false);
                });

                if (toggleButtonCouleur.isSelected()) {
                    Imgproc.dilate(matriceImageEnGris, matriceDestination, matriceDeTransformation);
                } else {
                    Imgproc.dilate(matriceImageEnCouleur, matriceDestination, matriceDeTransformation);
                }
                break;
            case "ouverture":
                Platform.runLater(() -> {
                    accordionParametreOperation.getPanes().get(1).setVisible(false);
                });

                if (toggleButtonCouleur.isSelected()) {
                    Imgproc.erode(matriceImageEnGris, matriceDestination, matriceDeTransformation);
                    Imgproc.dilate(matriceDestination, matriceDestination, matriceDeTransformation);
                } else {
                    Imgproc.erode(matriceImageEnCouleur, matriceDestination, matriceDeTransformation);
                    Imgproc.dilate(matriceDestination, matriceDestination, matriceDeTransformation);
                }
                break;
            case "fermeture":
                Platform.runLater(() -> {
                    accordionParametreOperation.getPanes().get(1).setVisible(false);
                });

                if (toggleButtonCouleur.isSelected()) {
                    Imgproc.dilate(matriceImageEnGris, matriceDestination, matriceDeTransformation);
                    Imgproc.erode(matriceDestination, matriceDestination, matriceDeTransformation);
                } else {
                    Imgproc.dilate(matriceImageEnCouleur, matriceDestination, matriceDeTransformation);
                    Imgproc.erode(matriceDestination, matriceDestination, matriceDeTransformation);
                }
                break;
            case "filtre de canny":
                toggleButtonCouleur.setVisible(false);
                final int RATIO = 3;
                final int KERNEL_SIZE = 3;
                Imgproc.blur(matriceImageEnGris, matriceDestination, new Size(taille, taille), new Point(-1, -1));
                Imgproc.Canny(matriceDestination, matriceDestination, (int)forceMatriceTransformation.getValue(), (int)forceMatriceTransformation.getValue() * RATIO, KERNEL_SIZE, false);

                Platform.runLater(() -> {
                    accordionParametreOperation.getPanes().get(1).setVisible(false);
                });
                break;
            case "détection de contours":
                Platform.runLater(() -> {
                    accordionParametreOperation.getPanes().get(1).setVisible(false);
                });
                break;
            case "détection de coins":
                Platform.runLater(() -> {
                    accordionParametreOperation.getPanes().get(1).setVisible(false);
                });
                break;
            case "détection d'objets":
                Platform.runLater(() -> {
                    accordionParametreOperation.getPanes().get(1).setVisible(false);
                });
                break;
            default:
                if (accordionParametreOperation.isVisible()){
                    accordionParametreOperation.setVisible(false);
                }

                if (toggleButtonCouleur.isSelected()) {
                    matriceDestination = matriceImageEnGris;
                } else {
                    matriceDestination = matriceImageEnCouleur;
                }
                break;
        }
    }
}