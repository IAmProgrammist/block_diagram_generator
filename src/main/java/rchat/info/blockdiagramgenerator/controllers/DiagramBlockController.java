package rchat.info.blockdiagramgenerator.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.DebounceDecorator;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.controllers.bdelements.*;
import rchat.info.blockdiagramgenerator.elements.ResizableCanvas;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;

import java.util.List;

public class DiagramBlockController {
    public DiagramBlockModel model;
    public DiagramBlockView view;
    @FXML
    public ResizableCanvas canvas;
    @FXML
    public VBox elementProps;
    public History<DiagramBlockModel> applicationHistory;
    final KeyCombination undoCombintaion = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
    final KeyCombination redoCombination = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
    final KeyCombination delete = new KeyCodeCombination(KeyCode.DELETE);

    /*public Runnable saveState = new Runnable() {
        @Override
        public void run() {
            DiagramBlockModel clonedAppState = model.clone();
            applicationHistory.pushElement(clonedAppState);
        }
    };
    DebounceDecorator d = new DebounceDecorator(saveState, DiagramBlockModel.SAVE_STATE_DELAY);*/

    public void select() {
        //d.discardChanges();
        BDElementController newSelected = model.root.select(new Pair<>(model.posX, model.posY));
        if (newSelected != model.selected) {

            if (model.selected != null)
                model.selected.selected = false;
            model.selected = newSelected;

            updateBDContentsEditor();
            DiagramBlockModel clonedAppState = model.clone();
            applicationHistory.pushElement(clonedAppState);
            view.repaint(canvas.getGraphicsContext2D());
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
        model = DiagramBlockModel.initDefault();
        DiagramBlockModel.onDataUpdate = () -> {
            DiagramBlockModel clonedAppState = model.clone();
            applicationHistory.pushElement(clonedAppState);
            model.root.recalculateSizes();
            view.repaint(canvas.getGraphicsContext2D());
        };
        view = new DiagramBlockView(model);
        DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME, model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);

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
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            DiagramBlockModel.canvasWidth = newValue.doubleValue();
            view.repaint(canvas.getGraphicsContext2D());
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            DiagramBlockModel.canvasHeight = newValue.doubleValue();
            canvas.setHeight(DiagramBlockModel.canvasHeight);
            view.repaint(canvas.getGraphicsContext2D());
        });
        canvas.setOnMouseMoved(event -> {
            DiagramBlockModel.mousePosX = event.getX();
            DiagramBlockModel.mousePosY = event.getY();
            DiagramBlockModel.canvasMousePosX = DiagramBlockModel.mousePosX / this.model.canvasScale;
            DiagramBlockModel.canvasMousePosY = DiagramBlockModel.mousePosY / this.model.canvasScale;
            view.repaint(canvas.getGraphicsContext2D());
        });
        Main.rewriteKeyPressedEvent = event -> {
            if (undoCombintaion.match(event)) {
                this.model = applicationHistory.prev();
                this.model.selected = model.root.getSelected();
                this.view.model = model;

                DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                        model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);
                updateBDContentsEditor();
                this.view.repaint(canvas.getGraphicsContext2D());
            } else if (redoCombination.match(event)) {
                this.model = applicationHistory.next();
                this.model.selected = model.root.getSelected();
                this.view.model = model;

                DiagramBlockModel.basicFont = Font.font(DiagramBlockModel.FONT_BASIC_NAME,
                        model.canvasScale * DiagramBlockModel.FONT_BASIC_SIZE);
                updateBDContentsEditor();
                this.view.repaint(canvas.getGraphicsContext2D());
            } else if (delete.match(event)) {
                if (model.selected != null) model.selected.remove();
                model.selected = null;
                model.root.recalculateSizes();
                DiagramBlockModel clonedAppState = model.clone();
                applicationHistory.pushElement(clonedAppState);
                updateBDContentsEditor();
                view.repaint(canvas.getGraphicsContext2D());
            }
        };
        /*bdcontent.textProperty().addListener((observable, oldValue, newValue) -> {
            if (model.selected != null && model.selected instanceof TextEditable) {
                ((TextEditable) model.selected).setText(newValue);
                DiagramBlockModel clonedAppState = model.clone();
                applicationHistory.pushElement(clonedAppState);
                model.root.recalculateSizes();
                view.repaint(canvas.getGraphicsContext2D());
            }
        });*/
        applicationHistory = new History<>(model.clone());
        view.repaint(canvas.getGraphicsContext2D());
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

        view.repaint(canvas.getGraphicsContext2D());
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
            view.repaint(canvas.getGraphicsContext2D());
        }
    }

}