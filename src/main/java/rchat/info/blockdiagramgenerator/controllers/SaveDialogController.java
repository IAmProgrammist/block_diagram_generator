package rchat.info.blockdiagramgenerator.controllers;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.StringConverter;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.SaveDialogModel;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class SaveDialogController extends Dialog<SaveDialogModel> {
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

    boolean shouldEditDensity = true;
    private ObjectProperty<SaveDialogModel> connection = new SimpleObjectProperty<>(null);
    public ResourceBundle rb;

    public SaveDialogController(DiagramBlockController main, Window window) {
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

            Pattern validEditingState = Pattern.compile("\\d+\\.\\d+");

            UnaryOperator<TextFormatter.Change> filter = c -> {
                String text = c.getControlNewText();
                if (validEditingState.matcher(text).matches()) {
                    return c;
                } else {
                    return null;
                }
            };

            StringConverter<Double> converter = new StringConverter<>() {

                @Override
                public Double fromString(String s) {
                    if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                        return 0.0;
                    } else {
                        return Double.valueOf(s);
                    }
                }


                @Override
                public String toString(Double d) {
                    return String.format("%.2f", d).replaceAll(",", ".");
                }
            };

            TextFormatter<Double> widthTextFormatter = new TextFormatter<>(converter, 0.0, filter);
            TextFormatter<Double> heightTextFormatter = new TextFormatter<>(converter, 0.0, filter);
            TextFormatter<Double> densityTextFormatter = new TextFormatter<>(converter, 0.0, filter);

            widthText.setTextFormatter(widthTextFormatter);
            heightText.setTextFormatter(heightTextFormatter);
            densityText.setTextFormatter(densityTextFormatter);

            connection.setValue(new SaveDialogModel(main.model.root.getModel().getSize().getWidth(),
                    main.model.root.getModel().getSize().getHeight(),
                    DiagramBlockModel.DEFAULT_PPI));
            widthTextFormatter.setValue(main.model.root.getModel().getSize().getWidth());

            widthTextFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == newValue) return;
                if (widthTextMeasurments.getValue().equals(rb.getString("measurments_pix"))) {
                    connection.getValue().setWidth(newValue, SaveDialogModel.PIXELS);
                    Dimension2D size = connection.getValue().getSize(SaveDialogModel.PIXELS);
                    heightText.setText(String.format("%.2f", size.getHeight()).replaceAll(",", "."));
                } else if (widthTextMeasurments.getValue().equals(rb.getString("measurments_cm"))) {
                    connection.getValue().setWidth(newValue, SaveDialogModel.CENTIMETERS);
                    Dimension2D size = connection.getValue().getSize(SaveDialogModel.CENTIMETERS);
                    heightText.setText(String.format("%.2f", size.getHeight()).replaceAll(",", "."));
                } else {
                    connection.getValue().setWidth(newValue, SaveDialogModel.INCHES);
                    Dimension2D size = connection.getValue().getSize(SaveDialogModel.INCHES);
                    heightText.setText(String.format("%.2f", size.getHeight()).replaceAll(",", "."));
                }
            });

            widthTextMeasurments.setItems(FXCollections.observableArrayList(
                    rb.getString("measurments_pix"),
                    rb.getString("measurments_cm"),
                    rb.getString("measurments_inch")));
            widthTextMeasurments.setValue(rb.getString("measurments_pix"));
            widthTextMeasurments.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue.equals(newValue)) return;
                heightTextMeasurments.setValue(newValue);
                Dimension2D size;
                if (newValue.equals(rb.getString("measurments_pix"))) {
                    size = connection.getValue().getSize(SaveDialogModel.PIXELS);
                } else if (newValue.equals(rb.getString("measurments_cm"))) {
                    size = connection.getValue().getSize(SaveDialogModel.CENTIMETERS);
                } else {
                    size = connection.getValue().getSize(SaveDialogModel.INCHES);
                }
                widthTextFormatter.setValue(size.getWidth());
                heightTextFormatter.setValue(size.getHeight());
            });
            heightTextFormatter.setValue(main.model.root.getModel().getSize().getHeight());

            heightTextFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == newValue) return;
                if (widthTextMeasurments.getValue().equals(rb.getString("measurments_pix"))) {
                    connection.getValue().setHeight(newValue, SaveDialogModel.PIXELS);
                    Dimension2D size = connection.getValue().getSize(SaveDialogModel.PIXELS);
                    widthText.setText(String.format("%.2f", size.getWidth()).replaceAll(",", "."));
                } else if (widthTextMeasurments.getValue().equals(rb.getString("measurments_cm"))) {
                    connection.getValue().setHeight(newValue, SaveDialogModel.CENTIMETERS);
                    Dimension2D size = connection.getValue().getSize(SaveDialogModel.CENTIMETERS);
                    widthText.setText(String.format("%.2f", size.getWidth()).replaceAll(",", "."));
                } else {
                    connection.getValue().setHeight(newValue, SaveDialogModel.INCHES);
                    Dimension2D size = connection.getValue().getSize(SaveDialogModel.INCHES);
                    widthText.setText(String.format("%.2f", size.getWidth()).replaceAll(",", "."));
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
                widthTextFormatter.setValue(size.getWidth());
                heightTextFormatter.setValue(size.getHeight());
            });
            densityTextFormatter.setValue(connection.getValue().density);
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
                widthTextFormatter.setValue(size.getWidth());
                heightTextFormatter.setValue(size.getHeight());
            });

            densityTextMeasurments.setItems(FXCollections.observableArrayList(
                    rb.getString("measurments_pix_perinch"),
                    rb.getString("measurments_pix_percm")));
            densityTextMeasurments.setValue(rb.getString("measurments_pix_perinch"));

            densityTextMeasurments.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == newValue) return;
                if (newValue.equals( rb.getString("measurments_pix_perinch"))) {
                    shouldEditDensity = false;
                    densityTextFormatter.setValue(connection.getValue().getDensity(false));
                } else {
                    shouldEditDensity = false;
                    densityTextFormatter.setValue(connection.getValue().getDensity(true));
                }
            });

            setDialogPane(dialogPane);
            setResultConverter(buttonType -> {
                if (!Objects.equals(ButtonBar.ButtonData.APPLY, buttonType.getButtonData())) {
                    return null;
                }

                return connection.getValue();
            });

            filePathTextSelect.setOnAction(action -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("SVG", "*.svg"),
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                        new FileChooser.ExtensionFilter("PNG", "*.png")
                );
                File file = fileChooser.showSaveDialog(window);
                if (file != null) {
                    filePathText.setText(file.getAbsolutePath());
                    connection.getValue().setPath(file.getAbsolutePath());
                }
            });

            setOnShowing(dialogEvent -> Platform.runLater(() -> filePathText.requestFocus()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
