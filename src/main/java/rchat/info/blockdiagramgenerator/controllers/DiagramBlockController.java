package rchat.info.blockdiagramgenerator.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.controllers.bdelements.*;
import rchat.info.blockdiagramgenerator.elements.ResizableCanvas;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.SaveDialogModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.painter.*;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class DiagramBlockController {
    public DiagramBlockModel model;
    public DiagramBlockView view;
    public File currentPath;
    @FXML
    public ResizableCanvas canvas;
    @FXML
    public VBox elementProps;
    @FXML
    public ListView<HBox> elements;
    public History<DiagramBlockModel> applicationHistory;
    final KeyCombination undoCombintaion = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
    final KeyCombination newFileCombination = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
    final KeyCombination redoCombination = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
    final KeyCombination saveCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
    final KeyCombination delete = new KeyCodeCombination(KeyCode.DELETE);
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
    AbstractPainter gc;

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
            } else if (result.isPresent() && result.get().getButtonData() == ButtonType.NO.getButtonData()) {
                return true;
            }
        }

        return false;
    }

    public void checkAndExit() {
        if (checkIfSaved()) {
            Platform.exit();
            System.exit(0);
        }
    }

    public void checkAndNew() {
        if (checkIfSaved()) {
            model.root = new BDContainerController();
            model.posX = 0;
            model.posY = 0;
            model.canvasScale = 1;
            model.selected = null;
            DiagramBlockModel.VIEWPORT_MODE = true;
            DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                    model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);
            model.root.update(gc, new Pair<>(DiagramBlockModel.canvasMousePosX,
                    DiagramBlockModel.canvasMousePosY), model.canvasScale);
            view.repaint(gc);
        }
    }

    public void select() {
        BDElementController newSelected = model.root.select(new Pair<>(model.posX, model.posY));
        if (newSelected != model.selected) {

            if (model.selected != null)
                model.selected.selected = false;
            model.selected = newSelected;

            updateBDContentsEditor();
            DiagramBlockModel clonedAppState = model.clone();
            applicationHistory.pushElement(clonedAppState);
            view.repaint(gc);
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

    @FXML
    public void initialize() {
        gc = new CanvasPainter(canvas.getGraphicsContext2D());
        model = DiagramBlockModel.initDefault();
        DiagramBlockModel.onDataUpdate = () -> {
            DiagramBlockModel clonedAppState = model.clone();
            applicationHistory.pushElement(clonedAppState);
            model.root.recalculateSizes();
            view.repaint(gc);
        };
        view = new DiagramBlockView(model);
        DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME, model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);

        elements.getItems().forEach(el -> {
            el.setOnDragDetected((event -> {
                Dragboard db = el.startDragAndDrop(TransferMode.COPY);
                ClipboardContent cbc = new ClipboardContent();
                cbc.putString(el.getProperties().get("datatype").toString());
                db.setContent(cbc);
                DiagramBlockModel.dragMode = true;

                event.consume();
            }));

            el.setOnDragDone(event -> {
                event.acceptTransferModes(TransferMode.COPY);
                DiagramBlockModel.dragMode = false;
                select();
                if (this.model.selected != null) {
                    BDElementController replacing = BDElementController.fromString(el.getProperties().get("datatype").toString());
                    this.model.selected.replace(replacing);
                    this.model.selected = null;
                    updateBDContentsEditor();
                }
            });
        });
        canvas.setOnDragOver(event -> {
                    DiagramBlockModel.mousePosX = event.getX();
                    DiagramBlockModel.mousePosY = event.getY();
                    DiagramBlockModel.canvasMousePosX = DiagramBlockModel.mousePosX / this.model.canvasScale;
                    DiagramBlockModel.canvasMousePosY = DiagramBlockModel.mousePosY / this.model.canvasScale;
                    view.repaint(gc);
                }
        );

        /*model.root = new BDContainerController(
                new BDTerminatorController("Начало"),
                new BDDataController("Ввод input"),
                new BDDecisionController(
                        new BDContainerController(new BDDataController("Вывод\n\"Последовательность\nпуста\"")), BDDecisionModel.Branch.LEFT,
                        new BDContainerController(
                                new BDProcessController("min := input"),
                                new BDDataController("Ввод input"),
                                new BDDecisionController(new BDContainerController(new BDDataController("Вывод\n\"Последовательность\nсодержит только один\nэлемент\"")), BDDecisionModel.Branch.LEFT,
                                        new BDContainerController(
                                                new BDProcessController("nextAfterMin := input"),
                                                new BDCycleNotFixedController(
                                                        new BDContainerController(
                                                                new BDProcessController("previousElement :=\ninput"),
                                                                new BDDataController("Ввод input"),
                                                                new BDDecisionController(
                                                                        new BDContainerController(
                                                                                new BDProcessController("min :=\npreviousElement"),
                                                                                new BDProcessController("nextAfterMin := input")
                                                                        ), BDDecisionModel.Branch.LEFT,
                                                                        new BDContainerController(),
                                                                        BDDecisionModel.Branch.RIGHT,
                                                                        "previousElement ≤ min"
                                                                ),
                                                                new BDDecisionController(new BDContainerController(new BDDataController("Вывод \"Последний\nэлемент\nпоследовательности\nминимальный\"")), BDDecisionModel.Branch.LEFT, new BDContainerController(new BDDataController("Вывод nextAfterMin")), BDDecisionModel.Branch.RIGHT, "nextAfterMin = 0")
                                                        )
                                                        , "input ≠ 0")
                                        ), BDDecisionModel.Branch.RIGHT, "input = 0")
                        ), BDDecisionModel.Branch.RIGHT, "input = 0"),
                new BDTerminatorController("Конец"));*/
        model.root = new BDContainerController();
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            DiagramBlockModel.canvasWidth = newValue.doubleValue();
            view.repaint(gc);
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            DiagramBlockModel.canvasHeight = newValue.doubleValue();
            canvas.setHeight(DiagramBlockModel.canvasHeight);
            view.repaint(gc);
        });
        canvas.setOnMouseMoved(event -> {
            DiagramBlockModel.mousePosX = event.getX();
            DiagramBlockModel.mousePosY = event.getY();
            DiagramBlockModel.canvasMousePosX = DiagramBlockModel.mousePosX / this.model.canvasScale;
            DiagramBlockModel.canvasMousePosY = DiagramBlockModel.mousePosY / this.model.canvasScale;
            view.repaint(gc);
        });

        menuQuit.setOnAction(event -> {
            checkAndExit();
        });

        menuNew.setOnAction(event -> {
            checkAndNew();
        });

        menuSave.setOnAction(event -> {
            save();
        });

        menuSaveAs.setOnAction(event -> {
            if (pickFile(true))
                save();
        });

        menuExport.setOnAction(event -> {
            model.root.recalculateSizes();
            double oldCS = model.canvasScale;
            double oldPX = model.posX;
            double oldPY = model.posY;
            DiagramBlockModel.VIEWPORT_MODE = false;
            model.canvasScale = 1;
            model.posX = 0;
            model.posY = 0;
            BDContainerController currentState = model.root;
            BDContainerController clone = model.root.clone();
            DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                    model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);
            model.root = clone;
            SaveDialogController controller = new SaveDialogController(this, Main.stage);
            controller.showAndWait().ifPresent(el -> {


                        if (el != null) {
                            if (el.fileExtension == SaveDialogModel.FILE_EXTENSION_SVG) {
                                SVGPainter p = new SVGPainter(el.widthPixels, el.heightPixels);
                                model.root.update(p, new Pair<>(0.0, 0.0), 1.0);
                                p.saveAsSVG(new File(el.file));
                            } else if (el.fileExtension == SaveDialogModel.FILE_EXTENSION_TEX) {
                                TikzPainter texPainter = new TikzPainter();
                                model.root.update(texPainter, new Pair<>(0.0, 0.0), 1.0);
                                texPainter.save(new File(el.file), el.widthCantimeters);
                            } else {
                                ImagePainter p = new ImagePainter(el.originalWidth, el.originalHeight, el.scale);
                                model.root.update(p, new Pair<>(0.0, 0.0), 1.0);
                                if (el.fileExtension == SaveDialogModel.FILE_EXTENSION_JPG) {
                                    p.saveAsJPG(new File(el.file));
                                } else {
                                    p.saveAsPNG(new File(el.file));
                                }
                            }
                        }
                    }
            );
            model.root = currentState;
            model.canvasScale = oldCS;
            model.posX = oldPX;
            model.posY = oldPY;
            DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                    model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);
            DiagramBlockModel.VIEWPORT_MODE = true;
            model.root.update(gc, new Pair<>(DiagramBlockModel.canvasMousePosX,
                    DiagramBlockModel.canvasMousePosY), model.canvasScale);
            view.repaint(gc);
        });

        menuOpen.setOnAction(event -> {
            if (checkIfSaved() && load()) {
                model.posX = 0;
                model.posY = 0;
                model.canvasScale = 1;
                model.selected = null;
                DiagramBlockModel.VIEWPORT_MODE = true;
                DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                        model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);
                model.root.update(gc, new Pair<>(DiagramBlockModel.canvasMousePosX,
                        DiagramBlockModel.canvasMousePosY), model.canvasScale);
                view.repaint(gc);
            }
        });

        Main.rewriteKeyPressedEvent = event -> {
            if (undoCombintaion.match(event)) {
                this.model = applicationHistory.prev();
                this.model.selected = model.root.getSelected();
                this.view.model = model;

                DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                        model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);
                updateBDContentsEditor();
                this.view.repaint(gc);
            } else if (redoCombination.match(event)) {
                this.model = applicationHistory.next();
                this.model.selected = model.root.getSelected();
                this.view.model = model;

                DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                        model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);
                updateBDContentsEditor();
                this.view.repaint(gc);
            } else if (delete.match(event)) {
                if (model.selected != null) model.selected.remove();
                model.selected = null;
                model.root.recalculateSizes();
                DiagramBlockModel clonedAppState = model.clone();
                applicationHistory.pushElement(clonedAppState);
                updateBDContentsEditor();
                view.repaint(gc);
            } else if (saveCombination.match(event)) {
                save();
            } else if (newFileCombination.match(event)) {
                checkAndNew();
            }
        };
        applicationHistory = new History<>(model.clone());
        view.repaint(gc);
    }

    public void onCanvasScrolled(ScrollEvent event) {
        double scrollTime = event.getDeltaY();
        double scale = Math.pow(DiagramBlockModel.CANVAS_RESCALE_FACTOR, scrollTime);
        model.canvasScale *= scale;

        double newCanvasHeight = DiagramBlockModel.canvasHeight * scale;
        double newCanvasWidth = DiagramBlockModel.canvasWidth * scale;

        double oldRelMouseX = DiagramBlockModel.mousePosX / DiagramBlockModel.canvasWidth;
        double oldRelMouseY = DiagramBlockModel.mousePosY / DiagramBlockModel.canvasHeight;
        double newRelMouseX = DiagramBlockModel.mousePosX / newCanvasWidth;
        double newRelMouseY = DiagramBlockModel.mousePosY / newCanvasHeight;
        model.posX += newCanvasWidth * (newRelMouseX - oldRelMouseX) / model.canvasScale;
        model.posY += newCanvasHeight * (newRelMouseY - oldRelMouseY) / model.canvasScale;

        DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);

        view.repaint(gc);
    }

    public void onMousePressed(MouseEvent event) {
        canvas.requestFocus();
        if (event.isSecondaryButtonDown()) {
            model.startX = event.getX();
            model.startY = event.getY();
        } else if (event.isPrimaryButtonDown()) {
            select();
        }
    }

    public void onCanvasMouseDragged(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            model.posX += (event.getX() - model.startX) / model.canvasScale;
            model.posY += (event.getY() - model.startY) / model.canvasScale;
            model.startX = event.getX();
            model.startY = event.getY();
            view.repaint(gc);
        }
    }

    public boolean load() {
        if (!pickFile(false))
            return false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(currentPath),
                StandardCharsets.UTF_8))) {
             String res = "";
            while (reader.ready()) {
                res += reader.readLine() + "\n";
            }

            JSONObject origin = new JSONObject(res);

            BDElementController cnt = BDElementController.fromJSONObject(origin);

            this.model.root = cnt instanceof BDContainerController ? (BDContainerController) cnt : new BDContainerController(cnt);

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
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

        return false;
    }
}