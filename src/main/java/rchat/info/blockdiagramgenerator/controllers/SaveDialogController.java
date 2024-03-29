package rchat.info.blockdiagramgenerator.controllers;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.SaveDialogModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.painter.ImagePainter;
import rchat.info.blockdiagramgenerator.painter.SVGPainter;
import rchat.info.blockdiagramgenerator.painter.TikzPainter;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class SaveDialogController extends Dialog<SaveDialogModel> {
    class SaveDialogDiagramBlockController extends DiagramBlockController {
        Style style;

        public SaveDialogDiagramBlockController(DiagramBlockModel model, AbstractPainter gc, Style style) {
            super(null, null, gc);
            this.style = style;
            this.model = model.clone(this);
            this.view = new DiagramBlockView(this.model);
            this.model.posX = -1;
            this.model.posY = -1;
            this.setCanvasScale(1.0);
            this.model.selected = null;
        }

        public SaveDialogDiagramBlockController(DiagramBlockModel model, Style style) {
            super(null, null, null);
            this.style = style;
            this.model = model.clone(this);
            this.view = new DiagramBlockView(this.model);
            this.model.posX = -1;
            this.model.posY = -1;
            this.setCanvasScale(1.0);
            this.model.selected = null;
        }

        @Override
        public Style getCurrentStyle() {
            return style;
        }

        public void setStyle(Style style) {
            this.style = style;
            this.model.root = model.root.clone(this);
            setCanvasScale(this.model.canvasScale);
        }

        @Override
        public double canvasMousePosX() {
            return 0;
        }

        @Override
        public double canvasMousePosY() {
            return 0;
        }

        @Override
        public boolean isViewportMode() {
            return false;
        }

        @Override
        public boolean isDragMode() {
            return false;
        }
    }

    @FXML
    TextField widthText;
    @FXML
    ChoiceBox widthTextMeasurments;
    @FXML
    TextField heightText;
    @FXML
    ChoiceBox heightTextMeasurments;
    @FXML
    TextField densityText;
    @FXML
    ChoiceBox densityTextMeasurments;
    @FXML
    TextField filePathText;
    @FXML
    Button filePathTextSelect;
    @FXML
    Button styleButton;

    boolean shouldEditDensity = true;
    boolean processTextChangingEvents = true;
    private ObjectProperty<SaveDialogModel> connection = new SimpleObjectProperty<>(null);
    public ResourceBundle rb;

    public SaveDialogController(EditorController main, Window window) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/rchat/info/blockdiagramgenerator/layouts/save-dialog.fxml"));
            rb = ResourceBundle.getBundle("rchat/info/blockdiagramgenerator/bundles/languages");
            loader.setResources(rb);
            loader.setController(this);

            DialogPane dialogPane = loader.load();

            initOwner(window);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle(this.rb.getString("export_image_dialog_title"));


            TextFormatter<Integer> widthTextFormatter = TextareaUtils.uIntTextFormatter();
            TextFormatter<Integer> heightTextFormatter = TextareaUtils.uIntTextFormatter();
            TextFormatter<Integer> densityTextFormatter = TextareaUtils.uIntTextFormatter();

            widthText.setTextFormatter(widthTextFormatter);
            heightText.setTextFormatter(heightTextFormatter);
            densityText.setTextFormatter(densityTextFormatter);

            Style style = Style.getCurrentStyle();
            styleButton.setText(style.getStyleName());
            SaveDialogDiagramBlockController saveDialogController = new SaveDialogDiagramBlockController(main.model, style);

            connection.setValue(new SaveDialogModel(saveDialogController.model.root.getModel().getSize().getWidth(),
                    saveDialogController.model.root.getModel().getSize().getHeight(),
                    DiagramBlockModel.DEFAULT_PPI));

            if (main.getCurrentPath() != null) {
                String filePath = new File(main.getCurrentPath().getParent() + "/" + main.getCurrentPath().getName().replaceFirst("[.][^.]+$", "") + ".png").getAbsolutePath();
                filePathText.setText(filePath);
                connection.getValue().setPath(filePath);
            }

            widthTextFormatter.setValue((int) saveDialogController.model.root.getModel().getSize().getWidth());

            widthText.textProperty().addListener((observable, oldValue, newValue) -> {
                if (processTextChangingEvents) {
                    try {
                        Double.parseDouble(newValue);
                    } catch (NumberFormatException e) {
                        newValue = "0";
                    }

                    if (widthTextMeasurments.getValue().equals(rb.getString("measurments_pix"))) {
                        connection.getValue().setWidth(Double.parseDouble(newValue), SaveDialogModel.PIXELS);
                        updateHeight(SaveDialogModel.PIXELS);
                    } else if (widthTextMeasurments.getValue().equals(rb.getString("measurments_cm"))) {
                        connection.getValue().setWidth(Double.parseDouble(newValue), SaveDialogModel.CENTIMETERS);
                        updateHeight(SaveDialogModel.CENTIMETERS);
                    } else {
                        connection.getValue().setWidth(Double.parseDouble(newValue), SaveDialogModel.INCHES);
                        updateHeight(SaveDialogModel.INCHES);
                    }
                }
            });

            widthTextMeasurments.setItems(FXCollections.observableArrayList(
                    rb.getString("measurments_pix"),
                    rb.getString("measurments_cm"),
                    rb.getString("measurments_inch")));
            widthTextMeasurments.setValue(rb.getString("measurments_pix"));
            widthTextMeasurments.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue.equals(newValue)) return;
                processTextChangingEvents = false;
                Dimension2D size;
                if (newValue.equals(rb.getString("measurments_pix"))) {
                    size = connection.getValue().getSize(SaveDialogModel.PIXELS);
                } else if (newValue.equals(rb.getString("measurments_cm"))) {
                    size = connection.getValue().getSize(SaveDialogModel.CENTIMETERS);
                } else {
                    size = connection.getValue().getSize(SaveDialogModel.INCHES);
                }
                widthTextFormatter.setValue((int) size.getWidth());
                heightTextFormatter.setValue((int) size.getHeight());
                processTextChangingEvents = true;
            });
            heightTextFormatter.setValue((int) saveDialogController.model.root.getModel().getSize().getHeight());

            heightText.textProperty().addListener((observable, oldValue, newValue) -> {
                if (processTextChangingEvents) {
                    try {
                        Double.parseDouble(newValue);
                    } catch (NumberFormatException e) {
                        newValue = "0";
                    }

                    if (widthTextMeasurments.getValue().equals(rb.getString("measurments_pix"))) {
                        connection.getValue().setHeight(Double.parseDouble(newValue), SaveDialogModel.PIXELS);
                        updateWidth(SaveDialogModel.PIXELS);
                    } else if (widthTextMeasurments.getValue().equals(rb.getString("measurments_cm"))) {
                        connection.getValue().setHeight(Double.parseDouble(newValue), SaveDialogModel.CENTIMETERS);
                        updateWidth(SaveDialogModel.CENTIMETERS);
                    } else {
                        connection.getValue().setHeight(Double.parseDouble(newValue), SaveDialogModel.INCHES);
                        updateWidth(SaveDialogModel.INCHES);
                    }
                }
            });

            heightTextMeasurments.setItems(FXCollections.observableArrayList(
                    rb.getString("measurments_pix"),
                    rb.getString("measurments_cm"),
                    rb.getString("measurments_inch")));
            heightTextMeasurments.setValue(rb.getString("measurments_pix"));
            heightTextMeasurments.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue.equals(newValue)) return;
                widthTextMeasurments.setValue(newValue);
                Dimension2D size;
                if (newValue.equals(rb.getString("measurments_pix"))) {
                    size = connection.getValue().getSize(SaveDialogModel.PIXELS);
                } else if (newValue.equals(rb.getString("measurments_cm"))) {
                    size = connection.getValue().getSize(SaveDialogModel.CENTIMETERS);
                } else {
                    size = connection.getValue().getSize(SaveDialogModel.INCHES);
                }
                widthTextFormatter.setValue((int) size.getWidth());
                heightTextFormatter.setValue((int) size.getHeight());
            });
            densityTextFormatter.setValue((int) connection.getValue().density);
            densityTextFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (!shouldEditDensity) {
                    shouldEditDensity = true;
                    return;
                }
                if (oldValue.equals(newValue)) return;

                if (densityTextMeasurments.getValue().equals(rb.getString("measurments_pix_perinch"))) {
                    connection.getValue().setDensity(newValue, false);
                } else {
                    connection.getValue().setDensity(newValue, true);
                }

                Dimension2D size;
                if (widthTextMeasurments.getValue().equals(rb.getString("measurments_pix"))) {
                    size = connection.getValue().getSize(SaveDialogModel.PIXELS);
                } else if (widthTextMeasurments.getValue().equals(rb.getString("measurments_cm"))) {
                    size = connection.getValue().getSize(SaveDialogModel.CENTIMETERS);
                } else {
                    size = connection.getValue().getSize(SaveDialogModel.INCHES);
                }
                widthTextFormatter.setValue((int) size.getWidth());
                heightTextFormatter.setValue((int) size.getHeight());
            });

            densityTextMeasurments.setItems(FXCollections.observableArrayList(
                    rb.getString("measurments_pix_perinch"),
                    rb.getString("measurments_pix_percm")));
            densityTextMeasurments.setValue(rb.getString("measurments_pix_perinch"));

            densityTextMeasurments.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == newValue) return;
                if (newValue.equals(rb.getString("measurments_pix_perinch"))) {
                    shouldEditDensity = false;
                    densityTextFormatter.setValue((int) connection.getValue().getDensity(false));
                } else {
                    shouldEditDensity = false;
                    densityTextFormatter.setValue((int) connection.getValue().getDensity(true));
                }
            });

            setDialogPane(dialogPane);
            setResultConverter(buttonType -> {
                if (!Objects.equals(ButtonBar.ButtonData.APPLY, buttonType.getButtonData())) {
                    return null;
                }

                SaveDialogModel el = connection.getValue();
                saveDialogController.setCanvasScale(el.scale);

                if (el.fileExtension == SaveDialogModel.FILE_EXTENSION_SVG) {
                    saveDialogController.gc = new SVGPainter(el.widthPixels, el.heightPixels);
                    saveDialogController.repaint(new Dimension2D(el.widthPixels, el.heightPixels));
                    ((SVGPainter) saveDialogController.gc).saveAsSVG(new File(el.file));
                } else if (el.fileExtension == SaveDialogModel.FILE_EXTENSION_TEX) {
                    saveDialogController.gc = new TikzPainter();
                    saveDialogController.repaint(new Dimension2D(el.widthPixels, el.heightPixels));
                    ((TikzPainter) saveDialogController.gc).save(new File(el.file), el.widthCantimeters,
                            style.isDebugModeEnabled() && style.isDebugTikzIncludeComments());
                } else {
                    saveDialogController.gc = new ImagePainter(el.widthPixels, el.heightPixels, 1);
                    saveDialogController.repaint(new Dimension2D(el.widthPixels, el.heightPixels));
                    //if (el.fileExtension == SaveDialogModel.FILE_EXTENSION_JPG) {
                    //    ((ImagePainter) saveDialogController.gc).saveAsJPG(new File(el.file));
                    //} else {
                    ((ImagePainter) saveDialogController.gc).saveAsPNG(new File(el.file));
                    //}
                }

                return connection.getValue();
            });

            filePathTextSelect.setOnAction(action -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("SVG", "*.svg"),
                        //new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                        new FileChooser.ExtensionFilter("PNG", "*.png"),
                        new FileChooser.ExtensionFilter("TEX", "*.tex")
                );
                File file = fileChooser.showSaveDialog(window);
                if (file != null) {
                    filePathText.setText(file.getAbsolutePath());
                    connection.getValue().setPath(file.getAbsolutePath());
                }
            });

            styleButton.setOnAction(event -> {
                StyleDialogController stylePicker = new StyleDialogController(saveDialogController.model, Main.stage);
                Style style1;
                try {
                    style1 = stylePicker.showAndWait().get();
                } catch (NoSuchElementException e) {
                    style1 = Style.getCurrentStyle();
                }

                saveDialogController.setCanvasScale(1.0);
                shouldEditDensity = false;
                String kyle = connection.getValue().file;
                Short mike = connection.getValue().fileExtension;
                connection.setValue(new SaveDialogModel((int) saveDialogController.model.root.getModel().getSize().getWidth(),
                        (int) saveDialogController.model.root.getModel().getSize().getHeight(),
                        DiagramBlockModel.DEFAULT_PPI));

                connection.getValue().file = kyle;
                connection.getValue().fileExtension = mike;

                densityTextFormatter.setValue((int) connection.getValue().density);
                densityTextMeasurments.setValue(rb.getString("measurments_pix_perinch"));

                shouldEditDensity = true;
                styleButton.setText(style1.getStyleName());
                widthTextMeasurments.setValue(rb.getString("measurments_pix"));
                widthTextFormatter.setValue((int) saveDialogController.model.root.getModel().getSize().getWidth());
            });

            setOnShowing(dialogEvent -> Platform.runLater(() -> filePathText.requestFocus()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMeasurmentTexts(short sizeType) {
        Dimension2D size = connection.getValue().getSize(sizeType);
        processTextChangingEvents = false;
        heightText.setText(String.format("%d", (int) size.getHeight()));
        widthText.setText(String.format("%d", (int) size.getWidth()));
        processTextChangingEvents = true;
    }

    public void updateHeight(short sizeType) {
        Dimension2D size = connection.getValue().getSize(sizeType);
        processTextChangingEvents = false;
        heightText.setText(String.format("%d", (int) size.getHeight()));
        processTextChangingEvents = true;
    }

    public void updateWidth(short sizeType) {
        Dimension2D size = connection.getValue().getSize(sizeType);
        processTextChangingEvents = false;
        widthText.setText(String.format("%d", (int) size.getWidth()));
        processTextChangingEvents = true;
    }
}
