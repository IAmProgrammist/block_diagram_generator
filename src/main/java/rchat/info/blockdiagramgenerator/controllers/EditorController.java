package rchat.info.blockdiagramgenerator.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDElementController;
import rchat.info.blockdiagramgenerator.elements.ResizableCanvas;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.EditorModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.painter.CanvasPainter;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

public class EditorController extends DiagramBlockController {
    @FXML
    public MenuItem pickStyle;
    EditorModel eModel;
    public File currentPath;
    @FXML
    public ResizableCanvas canvas;
    @FXML
    public VBox elementProps;
    @FXML
    public ListView<HBox> elements;
    public History<DiagramBlockModel> applicationHistory;
    @FXML
    public MenuItem menuNew;
    @FXML
    public MenuItem menuOpen;
    @FXML
    public MenuItem menuSave;
    @FXML
    public MenuItem menuSaveAs;
    @FXML
    public MenuItem menuExport;
    @FXML
    public MenuItem menuQuit;
    @FXML
    public MenuItem menuUndo;
    @FXML
    public MenuItem menuRedo;
    @FXML
    public MenuItem menuDelete;


    public boolean checkIfSaved() {
        if (applicationHistory.isSaved()) {
            return true;
        } else {
            ButtonType yes = new ButtonType(Main.rb.getString("should_save_yes"), ButtonBar.ButtonData.YES);
            ButtonType no = new ButtonType(Main.rb.getString("should_save_no"), ButtonBar.ButtonData.NO);
            ButtonType cancel = new ButtonType(Main.rb.getString("should_save_cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    Main.rb.getString("should_save_warning"),
                    yes,
                    no, cancel);

            alert.setTitle(Main.rb.getString("should_save_warning_title"));
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get().getButtonData() == ButtonType.YES.getButtonData()) {
                return save();
            } else return result.isPresent() && result.get().getButtonData() == ButtonType.NO.getButtonData();
        }
    }

    public void checkAndExit() {
        if (checkIfSaved()) {
            Platform.exit();
            System.exit(0);
        }
    }

    public void export() {
        SaveDialogController controller = new SaveDialogController(this, Main.stage);
        controller.showAndWait();
    }

    public void checkAndNew() {
        if (checkIfSaved()) {
            model.root = new BDContainerController(this);
            model.posX = 0;
            model.posY = 0;
            setCanvasScale(1);
            model.selected = null;
            model.root.update(gc, new Pair<>(eModel.canvasMousePosX,
                    eModel.canvasMousePosY), model.canvasScale);
            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));

            currentPath = null;

            applicationHistory.clear();
            applicationHistory.pushElement(this.model.clone());
            applicationHistory.save();

            updateTitle();
        }
    }

    public void select() {
        BDElementController newSelected = model.root.select(new Pair<>(model.posX, model.posY));
        if (newSelected != model.selected) {

            if (model.selected != null)
                model.selected.selected = false;
            model.selected = newSelected;

            updateBDContentsEditor();

            updateTitle();

            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        }
    }

    public void updateBDContentsEditor() {
        elementProps.getChildren().clear();
        if (this.model.selected != null) {
            List<Node> nodes = this.model.selected.getControls();
            nodes.forEach(el -> HBox.setMargin(el, new Insets(50, 50, 50, 50)));
            elementProps.getChildren().addAll(nodes);
        }
    }

    public EditorController() {
        super(null, null, null);
    }

    @FXML
    public void initialize() {
        eModel = new EditorModel();
        this.gc = new CanvasPainter(canvas.getGraphicsContext2D());
        this.model = DiagramBlockModel.initDefault();
        model.onDataUpdate = () -> {
            DiagramBlockModel clonedAppState = model.clone();
            applicationHistory.pushElement(clonedAppState);

            updateTitle();

            model.root.recalculateSizes();
            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        };
        this.view = new DiagramBlockView(model);
        setCanvasScale(1);

        elements.getItems().forEach(el -> {
            el.setOnDragDetected((event -> {
                Dragboard db = el.startDragAndDrop(TransferMode.COPY);
                ClipboardContent cbc = new ClipboardContent();
                cbc.putString(el.getProperties().get("datatype").toString());
                db.setContent(cbc);
                eModel.dragMode = true;

                event.consume();
            }));

            el.setOnDragDone(event -> {
                event.acceptTransferModes(TransferMode.COPY);
                eModel.dragMode = false;
                select();
                if (this.model.selected != null) {
                    BDElementController replacing = BDElementController.fromString(this, el.getProperties().get("datatype").toString());
                    this.model.selected.replace(replacing);
                    this.model.selected = null;
                    updateBDContentsEditor();
                }
            });
        });
        canvas.setOnDragOver(event -> {
                    eModel.mousePosX = event.getX();
                    eModel.mousePosY = event.getY();
                    eModel.canvasMousePosX = eModel.mousePosX / this.model.canvasScale;
                    eModel.canvasMousePosY = eModel.mousePosY / this.model.canvasScale;
                    repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
                }
        );
        model.root = new BDContainerController(this);
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            eModel.canvasWidth = newValue.doubleValue();
            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            eModel.canvasHeight = newValue.doubleValue();
            canvas.setHeight(eModel.canvasHeight);
            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        });
        canvas.setOnMouseMoved(event -> {
            eModel.mousePosX = event.getX();
            eModel.mousePosY = event.getY();
            eModel.canvasMousePosX = eModel.mousePosX / this.model.canvasScale;
            eModel.canvasMousePosY = eModel.mousePosY / this.model.canvasScale;
            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        });

        menuQuit.setOnAction(event -> checkAndExit());

        menuNew.setOnAction(event -> checkAndNew());

        menuSave.setOnAction(event -> save());

        menuSaveAs.setOnAction(event -> {
            if (pickFile(true))
                save();
        });

        menuExport.setOnAction(event -> export());

        menuUndo.setOnAction(event -> {
            this.model.root = applicationHistory.prev().root.clone(this);

            updateTitle();

            this.model.selected = model.root.getSelected();
            this.view.model = model;
            setCanvasScale(this.model.canvasScale);

            updateBDContentsEditor();

            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        });

        menuRedo.setOnAction(event -> {
            this.model.root = applicationHistory.next().root.clone(this);

            updateTitle();

            this.model.selected = model.root.getSelected();
            this.view.model = model;
            setCanvasScale(this.model.canvasScale);

            updateBDContentsEditor();
            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        });

        menuDelete.setOnAction(event -> {
            if (canvas.isFocused()) {

                if (model.selected != null) model.selected.remove();
                model.selected = null;
                model.root.recalculateSizes();
                DiagramBlockModel clonedAppState = model.clone();
                applicationHistory.pushElement(clonedAppState);

                updateTitle();

                updateBDContentsEditor();
                repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
            }
        });

        menuOpen.setOnAction(event -> {
            if (checkIfSaved() && load()) {
                model.posX = 0;
                model.posY = 0;
                setCanvasScale(1);
                model.selected = null;
                model.root.update(gc, new Pair<>(eModel.canvasMousePosX,
                        eModel.canvasMousePosY), model.canvasScale);
                repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
                updateBDContentsEditor();
            }
        });

        pickStyle.setOnAction(event -> {
            StyleDialogController stylePicker = new StyleDialogController(model.clone(), Main.stage);
            Style style;
            try {
                style = stylePicker.showAndWait().get();
            } catch (NoSuchElementException e) {
                style = null;
            }

            if (style != null)
                Style.setCurrentStyle(style);

            this.model.root = this.model.root.clone(this);
            setCanvasScale(this.model.canvasScale);

            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        });

        currentPath = null;

        updateTitle();

        applicationHistory = new History<>(model.clone());
        repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
    }

    public void onCanvasScrolled(ScrollEvent event) {
        double scrollTime = event.getDeltaY();
        double scale = Math.pow(DiagramBlockModel.CANVAS_RESCALE_FACTOR, scrollTime);
        setCanvasScale(model.canvasScale * scale);

        double newCanvasHeight = eModel.canvasHeight * scale;
        double newCanvasWidth = eModel.canvasWidth * scale;

        double oldRelMouseX = eModel.mousePosX / eModel.canvasWidth;
        double oldRelMouseY = eModel.mousePosY / eModel.canvasHeight;
        double newRelMouseX = eModel.mousePosX / newCanvasWidth;
        double newRelMouseY = eModel.mousePosY / newCanvasHeight;
        model.posX += newCanvasWidth * (newRelMouseX - oldRelMouseX) / model.canvasScale;
        model.posY += newCanvasHeight * (newRelMouseY - oldRelMouseY) / model.canvasScale;

        repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
    }

    public void onMousePressed(MouseEvent event) {
        canvas.requestFocus();
        if (event.isSecondaryButtonDown()) {
            eModel.startX = event.getX();
            eModel.startY = event.getY();
        } else if (event.isPrimaryButtonDown()) {
            select();
        }
    }

    public void onCanvasMouseDragged(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            model.posX += (event.getX() - eModel.startX) / model.canvasScale;
            model.posY += (event.getY() - eModel.startY) / model.canvasScale;
            eModel.startX = event.getX();
            eModel.startY = event.getY();
            repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        }
    }

    public boolean load() {
        if (!checkIfSaved() || !pickFile(false))
            return false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(currentPath),
                StandardCharsets.UTF_8))) {
            String res = "";
            while (reader.ready()) {
                res += reader.readLine() + "\n";
            }

            JSONObject origin = new JSONObject(res);

            BDElementController cnt = BDElementController.fromJSONObject(this, origin);

            this.model.root = cnt instanceof BDContainerController ? (BDContainerController) cnt : new BDContainerController(this, cnt);

            applicationHistory.clear(this.model.clone());
            applicationHistory.save();

            updateTitle();

            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean pickFile(boolean save) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("DGB", "*.dgb"),
                new FileChooser.ExtensionFilter("Any", "*")
        );
        File file;
        if (save)
            file = fileChooser.showSaveDialog(Main.stage);
        else
            file = fileChooser.showOpenDialog(Main.stage);

        if (file != null) {
            currentPath = file;
            return true;
        }

        return false;
    }

    public boolean save() {
        if (currentPath == null)
            pickFile(true);

        if (currentPath != null) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentPath),
                    StandardCharsets.UTF_8))) {
                writer.write(this.model.root.exportToJSON().toString());
                applicationHistory.save();

                updateTitle();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

        return false;
    }

    private void updateTitle() {
        if (currentPath == null)
            Main.stage.setTitle(Main.rb.getString("untitled_project"));
        else if (applicationHistory.isSaved())
            Main.stage.setTitle(currentPath.getName() + " [" + currentPath.getPath() + "]");
        else
            Main.stage.setTitle(currentPath.getName() + "* [" + currentPath.getPath() + "]");
    }

    @Override
    public Style getCurrentStyle() {
        return Style.getCurrentStyle();
    }

    @Override
    public double canvasMousePosX() {
        return eModel.canvasMousePosX;
    }

    @Override
    public double canvasMousePosY() {
        return eModel.canvasMousePosY;
    }

    @Override
    public boolean isViewportMode() {
        return true;
    }

    @Override
    public boolean isDragMode() {
        return eModel.dragMode;
    }

    public File getCurrentPath() {
        return currentPath;
    }
}