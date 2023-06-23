package rchat.info.blockdiagramgenerator.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.controllers.bdelements.*;
import rchat.info.blockdiagramgenerator.elements.ResizableCanvas;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.EditorModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.painter.CanvasPainter;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StyleDialogController extends Dialog<Style> {

    public CheckBox toggleEditMode;
    public CheckBox toggleDragMode;
    public Button resetDiagramBlock;

    public void resetDiagramBlock(ActionEvent mouseEvent) {
        dbController.setModel(new DiagramBlockModel(new BDContainerController(dbController,
                new BDTerminatorController(dbController, "Начало"),
                new BDDataController(dbController, "Ввод input"),
                new BDDecisionController(dbController,
                        new BDContainerController(dbController, new BDDataController(dbController, "Вывод\n\"Последовательность\nпуста\"")), BDDecisionModel.Branch.LEFT,
                        new BDContainerController(dbController,
                                new BDProcessController(dbController, "min := input"),
                                new BDDataController(dbController, "Ввод input"),
                                new BDDecisionController(dbController, new BDContainerController(dbController, new BDDataController(dbController, "Вывод\n\"Последовательность\nсодержит только один\nэлемент\"")), BDDecisionModel.Branch.LEFT,
                                        new BDContainerController(dbController,
                                                new BDProcessController(dbController, "nextAfterMin := input"),
                                                new BDCycleNotFixedController(dbController,
                                                        new BDContainerController(dbController,
                                                                new BDProcessController(dbController, "previousElement :=\ninput"),
                                                                new BDDataController(dbController, "Ввод input"),
                                                                new BDDecisionController(dbController,
                                                                        new BDContainerController(dbController,
                                                                                new BDProcessController(dbController, "min :=\npreviousElement"),
                                                                                new BDProcessController(dbController, "nextAfterMin := input")
                                                                        ), BDDecisionModel.Branch.LEFT,
                                                                        new BDContainerController(dbController),
                                                                        BDDecisionModel.Branch.RIGHT,
                                                                        "previousElement ≤ min"
                                                                ),
                                                                new BDDecisionController(dbController, new BDContainerController(dbController, new BDDataController(dbController, "Вывод \"Последний\nэлемент\nпоследовательности\nминимальный\"")), BDDecisionModel.Branch.LEFT, new BDContainerController(dbController, new BDDataController(dbController, "Вывод nextAfterMin")), BDDecisionModel.Branch.RIGHT, "nextAfterMin = 0")
                                                        )
                                                        , "input ≠ 0")
                                        ), BDDecisionModel.Branch.RIGHT, "input = 0")
                        ), BDDecisionModel.Branch.RIGHT, "input = 0"),
                new BDTerminatorController(dbController, "Конец"))));
    }

    public void openDiagramBlock(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("DGB", "*.dgb"),
                new FileChooser.ExtensionFilter("Any", "*")
        );
        File file = fileChooser.showOpenDialog(Main.stage);

        if (file == null) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                StandardCharsets.UTF_8))) {
            String res = "";
            while (reader.ready()) {
                res += reader.readLine() + "\n";
            }

            JSONObject origin = new JSONObject(res);

            BDElementController cnt = BDElementController.fromJSONObject(dbController, origin);

            dbController.setModel(new DiagramBlockModel(cnt instanceof BDContainerController ? (BDContainerController) cnt : new BDContainerController(dbController, cnt)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public class StyleDialogDiagramBlockController extends DiagramBlockController {
        private Style currentStyle;
        public boolean isViewportMode = false;
        public boolean isDragMode = false;
        EditorModel eModel;

        public StyleDialogDiagramBlockController(DiagramBlockModel model, AbstractPainter gc, Style style, EditorModel eModel) {
            super(null, null, gc);
            this.currentStyle = style;
            this.model = model.clone(this);
            this.view = new DiagramBlockView(this.model);
            this.model.posX = -1;
            this.model.posY = -1;
            this.setCanvasScale(1.0);
            this.model.selected = null;

            this.eModel = eModel;
        }

        public StyleDialogDiagramBlockController(AbstractPainter gc, Style style, EditorModel eModel) {
            super(null, null, gc);
            this.currentStyle = style;
            this.model = new DiagramBlockModel(new BDContainerController(this));
            this.view = new DiagramBlockView(this.model);
            this.model.posX = -1;
            this.model.posY = -1;
            this.setCanvasScale(1.0);
            this.model.selected = null;

            this.eModel = eModel;
        }

        public void setModel(DiagramBlockModel model) {
            this.model.root = model.root.clone(this);
            view.repaint(this.gc, new Dimension2D(eModel.canvasWidth, eModel.canvasHeight), isViewportMode(), currentStyle);
        }

        @Override
        public Style getCurrentStyle() {
            return currentStyle;
        }

        public void setCurrentStyle(Style style) {
            this.currentStyle = style;
            this.model.root = model.root.clone(this);
            view.repaint(this.gc, new Dimension2D(eModel.canvasWidth, eModel.canvasHeight), isViewportMode(), currentStyle);
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
            return isViewportMode;
        }

        @Override
        public boolean isDragMode() {
            return isDragMode;
        }

        public void setViewportMode(boolean viewportMode) {
            isViewportMode = viewportMode;

            this.model.root = model.root.clone(this);
            view.repaint(this.gc, new Dimension2D(eModel.canvasWidth, eModel.canvasHeight), isViewportMode(), currentStyle);
        }

        public void setDragMode(boolean dragMode) {
            isDragMode = dragMode;

            this.model.root = model.root.clone(this);
            view.repaint(this.gc, new Dimension2D(eModel.canvasWidth, eModel.canvasHeight), isViewportMode(), currentStyle);
        }
    }

    public ResizableCanvas canvas;
    public EditorModel eModel;
    public AbstractPainter gc;
    public VBox styleList;
    public TextField fontBasicName;
    public ColorPicker strokeColor;
    public ColorPicker fontColor;
    public ColorPicker bdBackgroundColor;
    public ColorPicker backgroundColor;
    public TextField fontBasicSize;
    public TextField strokeWidthDefault;
    public TextField connectorsWidth;
    public TextField textPadding;
    public TextField lineSpacing;
    public TextField elementsSpacing;
    public TextField decisionBlocksPadding;
    public TextField minDecisionShoulderLen;
    public TextField dashLineWidthLine;
    public TextField dashLineWidthSpace;
    public TextField positiveBranchText;
    public TextField negativeBranchText;
    public ColorPicker gridColor;
    public ColorPicker selectedColor;
    public ColorPicker overflowSelectionColor;
    public ColorPicker dragndropForegroundColor;
    public TextField tileSize;
    public TextField tileStrokeWidthDefault;
    public TextField selectionBorderWidth;
    public TextField containerOverflowPadding;
    public TextField maxBdContainerDragndropWidth;
    public TextField maxBdContainerDragndropWidthMargin;
    public TextField tilesInTile;
    public CheckBox isDebugModeEnabled;
    public CheckBox isDebugShowFps;
    public CheckBox isDebugTikzIncludeComments;
    public CheckBox debugDrawBorders;
    public ColorPicker debugBorderColor;
    StyleDialogDiagramBlockController dbController;

    @FXML
    public void initialize() {
        eModel = new EditorModel();
        this.gc = new CanvasPainter(canvas.getGraphicsContext2D());
        Style cStyle = Style.getCurrentStyle();
        dbController = new StyleDialogDiagramBlockController(this.gc, cStyle, eModel);
        dbController.model.onDataUpdate = () -> {
            dbController.model.root.recalculateSizes();
            dbController.repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        };
        dbController.setCanvasScale(1);

        dbController.setModel(new DiagramBlockModel(new BDContainerController(dbController,
                new BDTerminatorController(dbController, "Начало"),
                new BDDataController(dbController, "Ввод input"),
                new BDDecisionController(dbController,
                        new BDContainerController(dbController, new BDDataController(dbController, "Вывод\n\"Последовательность\nпуста\"")), BDDecisionModel.Branch.LEFT,
                        new BDContainerController(dbController,
                                new BDProcessController(dbController, "min := input"),
                                new BDDataController(dbController, "Ввод input"),
                                new BDDecisionController(dbController, new BDContainerController(dbController, new BDDataController(dbController, "Вывод\n\"Последовательность\nсодержит только один\nэлемент\"")), BDDecisionModel.Branch.LEFT,
                                        new BDContainerController(dbController,
                                                new BDProcessController(dbController, "nextAfterMin := input"),
                                                new BDCycleNotFixedController(dbController,
                                                        new BDContainerController(dbController,
                                                                new BDProcessController(dbController, "previousElement :=\ninput"),
                                                                new BDDataController(dbController, "Ввод input"),
                                                                new BDDecisionController(dbController,
                                                                        new BDContainerController(dbController,
                                                                                new BDProcessController(dbController, "min :=\npreviousElement"),
                                                                                new BDProcessController(dbController, "nextAfterMin := input")
                                                                        ), BDDecisionModel.Branch.LEFT,
                                                                        new BDContainerController(dbController),
                                                                        BDDecisionModel.Branch.RIGHT,
                                                                        "previousElement ≤ min"
                                                                ),
                                                                new BDDecisionController(dbController, new BDContainerController(dbController, new BDDataController(dbController, "Вывод \"Последний\nэлемент\nпоследовательности\nминимальный\"")), BDDecisionModel.Branch.LEFT, new BDContainerController(dbController, new BDDataController(dbController, "Вывод nextAfterMin")), BDDecisionModel.Branch.RIGHT, "nextAfterMin = 0")
                                                        )
                                                        , "input ≠ 0")
                                        ), BDDecisionModel.Branch.RIGHT, "input = 0")
                        ), BDDecisionModel.Branch.RIGHT, "input = 0"),
                new BDTerminatorController(dbController, "Конец"))));
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            eModel.canvasWidth = newValue.doubleValue();
            canvas.setWidth(eModel.canvasWidth);
            dbController.repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            eModel.canvasHeight = newValue.doubleValue();
            canvas.setHeight(eModel.canvasHeight);
            dbController.repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        });
        canvas.setOnMouseMoved(event -> {
            eModel.mousePosX = event.getX();
            eModel.mousePosY = event.getY();
            eModel.canvasMousePosX = eModel.mousePosX / dbController.model.canvasScale;
            eModel.canvasMousePosY = eModel.mousePosY / dbController.model.canvasScale;
            dbController.repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        });

        toggleEditMode.selectedProperty().addListener((observable, oldValue, newValue) -> dbController.setViewportMode(newValue));

        toggleDragMode.selectedProperty().addListener((observable, oldValue, newValue) -> dbController.setDragMode(newValue));

        dbController.repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
    }

    public void onCanvasScrolled(ScrollEvent event) {
        double scrollTime = event.getDeltaY();
        double scale = Math.pow(DiagramBlockModel.CANVAS_RESCALE_FACTOR, scrollTime);
        dbController.setCanvasScale(dbController.model.canvasScale * scale);

        double newCanvasHeight = eModel.canvasHeight * scale;
        double newCanvasWidth = eModel.canvasWidth * scale;

        double oldRelMouseX = eModel.mousePosX / eModel.canvasWidth;
        double oldRelMouseY = eModel.mousePosY / eModel.canvasHeight;
        double newRelMouseX = eModel.mousePosX / newCanvasWidth;
        double newRelMouseY = eModel.mousePosY / newCanvasHeight;
        dbController.model.posX += newCanvasWidth * (newRelMouseX - oldRelMouseX) / dbController.model.canvasScale;
        dbController.model.posY += newCanvasHeight * (newRelMouseY - oldRelMouseY) / dbController.model.canvasScale;

        dbController.repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
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

    public void select() {
        BDElementController newSelected = dbController.model.root.select(new Pair<>(dbController.model.posX, dbController.model.posY));
        if (newSelected != dbController.model.selected) {

            if (dbController.model.selected != null)
                dbController.model.selected.selected = false;
            dbController.model.selected = newSelected;
            dbController.repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        }
    }

    public void onCanvasMouseDragged(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            dbController.model.posX += (event.getX() - eModel.startX) / dbController.model.canvasScale;
            dbController.model.posY += (event.getY() - eModel.startY) / dbController.model.canvasScale;
            eModel.startX = event.getX();
            eModel.startY = event.getY();
            dbController.repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
        }
    }
}
