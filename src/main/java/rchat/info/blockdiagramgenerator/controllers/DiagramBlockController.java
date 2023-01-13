package rchat.info.blockdiagramgenerator.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.controllers.bdelements.*;
import rchat.info.blockdiagramgenerator.elements.ResizableCanvas;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.SaveDialogModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.painter.CanvasPainter;
import rchat.info.blockdiagramgenerator.painter.ImagePainter;
import rchat.info.blockdiagramgenerator.painter.SVGPainter;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;

import java.io.File;
import java.util.List;

public class DiagramBlockController {
    public DiagramBlockModel model;
    public DiagramBlockView view;
    @FXML
    public ResizableCanvas canvas;
    @FXML
    public VBox elementProps;
    @FXML
    public ListView<HBox> elements;
    public History<DiagramBlockModel> applicationHistory;
    final KeyCombination undoCombintaion = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
    final KeyCombination redoCombination = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
    final KeyCombination saveCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
    final KeyCombination delete = new KeyCodeCombination(KeyCode.DELETE);
    AbstractPainter gc;

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

        model.root = new BDContainerController(
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
                new BDTerminatorController("Конец"));
        //model.root = new BDContainerController();
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
                model.root.recalculateSizes();
                SVGPainter painter = new SVGPainter(model.root.model.getSize().getWidth(),
                        model.root.model.getSize().getHeight());
                double oldCS = model.canvasScale;
                double oldPX = model.posX;
                double oldPY = model.posY;
                DiagramBlockModel.VIEWPORT_MODE = false;
                DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                        model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);
                model.canvasScale = 1;
                model.posX = 0;
                model.posY = 0;
                BDContainerController currentState = model.root;
                BDContainerController clone = model.root.clone();
                model.root = clone;
                SaveDialogController controller = new SaveDialogController(this, Main.stage);
                controller.showAndWait().ifPresent(el -> {
                            if (el != null) {
                                if (el.fileExtension == SaveDialogModel.FILE_EXTENSION_SVG) {
                                    SVGPainter p = new SVGPainter(el.widthPixels, el.heightPixels);
                                    model.root.update(p, new Pair<>(0.0, 0.0), 1.0);
                                    p.saveAsSVG(new File(el.file));
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

}