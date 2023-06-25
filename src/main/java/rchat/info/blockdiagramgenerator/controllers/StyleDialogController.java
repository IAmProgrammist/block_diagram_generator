package rchat.info.blockdiagramgenerator.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Pair;
import org.controlsfx.dialog.FontSelectorDialog;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.controllers.bdelements.*;
import rchat.info.blockdiagramgenerator.elements.ResizableCanvas;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.EditorModel;
import rchat.info.blockdiagramgenerator.models.SaveDialogModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.painter.CanvasPainter;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ResourceBundle;

public class StyleDialogController extends Dialog<Style> {


    public CheckBox toggleEditMode;
    public CheckBox toggleDragMode;
    public ChoiceBox stylesList;
    public Button deleteStyle;
    public TextField styleName;

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

    public void cloneStyle(ActionEvent actionEvent) {
        String newStyleName = styleName.getText().trim();

        Style copyStyle = Style.createStyle(newStyleName.isBlank() ? cStyle.getStyleName() : newStyleName, cStyle);

        if (copyStyle == null)
            return;

        styleName.setText("");
        stylesList.getItems().add(copyStyle.getStyleName());

        setCStyle(copyStyle);
    }

    private void setCStyle(Style copyStyle) {
        cStyle = copyStyle;

        if (cStyle.getStyleName().equals(Style.DEFAULT_STYLE_NAME))
            deleteStyle.setDisable(true);
        else
            deleteStyle.setDisable(false);

        stylesList.setValue(cStyle.getStyleName());
        updateStyleControls();
    }

    public void deleteStyle(ActionEvent actionEvent) {
        Style.removeStyle(cStyle.getStyleName());
        stylesList.getItems().setAll(Style.getStyles());
        Style newStyle = Style.getStyle((String) stylesList.getItems().get(0));

        if (newStyle == null)
            newStyle = Style.getCurrentStyle();

        setCStyle(newStyle);
    }

    public void importStyle(ActionEvent actionEvent) {
        String newStyleName = styleName.getText().trim();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("Any", "*")
        );
        File file = fileChooser.showOpenDialog(Main.stage);
        Style newStyle = Style.fromFile(newStyleName.isBlank() ? cStyle.getStyleName() : newStyleName, file);

        if (newStyle == null)
            return;

        styleName.setText("");
        stylesList.getItems().add(newStyle.getStyleName());

        setCStyle(newStyle);
    }

    public void exportStyle(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("Any", "*")
        );

        File file = fileChooser.showSaveDialog(Main.stage);

        Style.toFile(cStyle, file);
    }

    public void pickFont(MouseEvent mouseEvent) {
        FontSelectorDialog fontPicker = new FontSelectorDialog(new Font(cStyle.getFontBasicName(), cStyle.getFontBasicSize()));

        fontPicker.showAndWait().ifPresent(font -> {
            fontBasicName.setText(font.getName());
            fontBasicSizeFormatter.setValue((int) font.getSize());
        });
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
            setCanvasScale(this.model.canvasScale);
            view.repaint(this.gc, new Dimension2D(eModel.canvasWidth, eModel.canvasHeight), isViewportMode(), currentStyle);
        }

        @Override
        public Style getCurrentStyle() {
            return currentStyle;
        }

        public void setCurrentStyle(Style style) {
            this.currentStyle = style;
            this.model.root = model.root.clone(this);
            setCanvasScale(this.model.canvasScale);
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
            setCanvasScale(this.model.canvasScale);
            view.repaint(this.gc, new Dimension2D(eModel.canvasWidth, eModel.canvasHeight), isViewportMode(), currentStyle);
        }

        public void setDragMode(boolean dragMode) {
            isDragMode = dragMode;

            this.model.root = model.root.clone(this);
            setCanvasScale(this.model.canvasScale);
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
    TextFormatter<Integer> fontBasicSizeFormatter = TextareaUtils.uIntTextFormatter();
    public TextField strokeWidthDefault;
    TextFormatter<Integer> strokeWidthDefaultFormatter = TextareaUtils.uIntTextFormatter();
    public TextField connectorsWidth;
    TextFormatter<Integer> connectorsWidthFormatter = TextareaUtils.uIntTextFormatter();
    public TextField textPadding;
    TextFormatter<Integer> textPaddingFormatter = TextareaUtils.uIntTextFormatter();
    public TextField lineSpacing;
    TextFormatter<Integer> lineSpacingFormatter = TextareaUtils.uIntTextFormatter();
    public TextField elementsSpacing;
    TextFormatter<Integer> elementsSpacingFormatter = TextareaUtils.uIntTextFormatter();
    public TextField decisionBlocksPadding;
    TextFormatter<Integer> decisionBlocksPaddingFormatter = TextareaUtils.uIntTextFormatter();
    public TextField minDecisionShoulderLen;
    TextFormatter<Integer> minDecisionShoulderLenFormatter = TextareaUtils.uIntTextFormatter();
    public TextField dashLineWidthLine;
    TextFormatter<Integer> dashLineWidthLineFormatter = TextareaUtils.uIntTextFormatter();
    public TextField dashLineWidthSpace;
    TextFormatter<Integer> dashLineWidthSpaceFormatter = TextareaUtils.uIntTextFormatter();
    public TextField positiveBranchText;
    public TextField negativeBranchText;
    public ColorPicker gridColor;
    public ColorPicker selectedColor;
    public ColorPicker overflowSelectionColor;
    public ColorPicker dragndropForegroundColor;
    public TextField tileSize;
    TextFormatter<Integer> tileSizeFormatter = TextareaUtils.uIntTextFormatter();
    public TextField tileStrokeWidthDefault;
    TextFormatter<Integer> tileStrokeWidthDefaultFormatter = TextareaUtils.uIntTextFormatter();
    public TextField selectionBorderWidth;
    TextFormatter<Integer> selectionBorderWidthFormatter = TextareaUtils.uIntTextFormatter();
    public TextField containerOverflowPadding;
    TextFormatter<Integer> containerOverflowPaddingFormatter = TextareaUtils.uIntTextFormatter();
    public TextField maxBdContainerDragndropWidth;
    TextFormatter<Integer> maxBdContainerDragndropWidthFormatter = TextareaUtils.uIntTextFormatter();
    public TextField maxBdContainerDragndropWidthMargin;
    TextFormatter<Integer> maxBdContainerDragndropWidthMarginFormatter = TextareaUtils.uIntTextFormatter();
    public TextField tilesInTile;
    TextFormatter<Integer> tilesInTileFormatter = TextareaUtils.uIntTextFormatter();
    public CheckBox isDebugModeEnabled;
    public CheckBox isDebugShowFps;
    public CheckBox isDebugTikzIncludeComments;
    public CheckBox debugDrawBorders;
    public ColorPicker debugBorderColor;
    public Control elements[];
    StyleDialogDiagramBlockController dbController;
    String[] styles;
    Style cStyle;
    private ObjectProperty<Style> connection = new SimpleObjectProperty<>(null);

    public StyleDialogController(DiagramBlockModel cloned, Window window) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/rchat/info/blockdiagramgenerator/layouts/styles-dialog.fxml"));
            ResourceBundle rb = ResourceBundle.getBundle("rchat/info/blockdiagramgenerator/bundles/languages");
            loader.setResources(rb);
            loader.setController(this);

            DialogPane pane = loader.load();

            initOwner(window);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(true);
            setTitle(rb.getString("style_dialog_title"));

            setDialogPane(pane);

            elements = new Control[]{fontBasicName, strokeColor, fontColor, bdBackgroundColor, backgroundColor, fontBasicSize, strokeWidthDefault,
                    connectorsWidth, textPadding, lineSpacing, elementsSpacing, decisionBlocksPadding, minDecisionShoulderLen, dashLineWidthLine,
                    dashLineWidthSpace, positiveBranchText, negativeBranchText, gridColor, selectedColor, overflowSelectionColor, dragndropForegroundColor,
                    tileSize, tileStrokeWidthDefault, selectionBorderWidth, containerOverflowPadding, maxBdContainerDragndropWidth, maxBdContainerDragndropWidthMargin,
                    tilesInTile, isDebugModeEnabled, isDebugShowFps, isDebugTikzIncludeComments, debugDrawBorders, debugBorderColor};

            fontBasicSize.setTextFormatter(fontBasicSizeFormatter);
            strokeWidthDefault.setTextFormatter(strokeWidthDefaultFormatter);
            connectorsWidth.setTextFormatter(connectorsWidthFormatter);
            textPadding.setTextFormatter(textPaddingFormatter);
            lineSpacing.setTextFormatter(lineSpacingFormatter);
            elementsSpacing.setTextFormatter(elementsSpacingFormatter);
            decisionBlocksPadding.setTextFormatter(decisionBlocksPaddingFormatter);
            minDecisionShoulderLen.setTextFormatter(minDecisionShoulderLenFormatter);
            dashLineWidthLine.setTextFormatter(dashLineWidthLineFormatter);
            dashLineWidthSpace.setTextFormatter(dashLineWidthSpaceFormatter);
            tileSize.setTextFormatter(tileSizeFormatter);
            tileStrokeWidthDefault.setTextFormatter(tileStrokeWidthDefaultFormatter);
            selectionBorderWidth.setTextFormatter(selectionBorderWidthFormatter);
            containerOverflowPadding.setTextFormatter(containerOverflowPaddingFormatter);
            maxBdContainerDragndropWidth.setTextFormatter(maxBdContainerDragndropWidthFormatter);
            maxBdContainerDragndropWidthMargin.setTextFormatter(maxBdContainerDragndropWidthMarginFormatter);
            tilesInTile.setTextFormatter(tilesInTileFormatter);

            styles = Style.getStyles();
            cStyle = Style.getCurrentStyle();

            if (cStyle.getStyleName().equals(Style.DEFAULT_STYLE_NAME))
                deleteStyle.setDisable(true);
            else
                deleteStyle.setDisable(false);

            stylesList.getItems().setAll(styles);
            stylesList.setValue(cStyle.getStyleName());
            updateStyleControls();

            stylesList.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle = Style.getStyle((String) newValue);

                dbController.setCurrentStyle(cStyle);

                if (cStyle.getStyleName().equals(Style.DEFAULT_STYLE_NAME))
                    deleteStyle.setDisable(true);
                else
                    deleteStyle.setDisable(false);

                updateStyleControls();

            });

            eModel = new EditorModel();
            this.gc = new CanvasPainter(canvas.getGraphicsContext2D());
            dbController = new StyleDialogDiagramBlockController(this.gc, cStyle, eModel);
            dbController.model.onDataUpdate = () -> {
                dbController.model.root.recalculateSizes();
                dbController.repaint(new Dimension2D(eModel.canvasWidth, eModel.canvasHeight));
            };
            dbController.setCanvasScale(1);


            if (cloned == null)
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
            else
                dbController.setModel(new DiagramBlockModel(cloned.root.clone(dbController)));

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

            fontBasicName.textProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setFontBasicName(newValue);
                dbController.setCurrentStyle(cStyle);
            });

            positiveBranchText.textProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setPositiveBranchText(newValue);
                dbController.setCurrentStyle(cStyle);
            });

            negativeBranchText.textProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setNegativeBranchText(newValue);
                dbController.setCurrentStyle(cStyle);
            });


            strokeColor.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setStrokeColor(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            fontColor.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setFontColor(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            bdBackgroundColor.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setBdBackgroundColor(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            backgroundColor.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setBackgroundColor(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            gridColor.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setGridColor(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            selectedColor.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setSelectedColor(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            overflowSelectionColor.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setOverflowSelectionColor(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            dragndropForegroundColor.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setDragndropForegroundColor(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            debugBorderColor.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setDebugBorderColor(newValue);
                dbController.setCurrentStyle(cStyle);
            });


            isDebugModeEnabled.selectedProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setDebugModeEnabled(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            isDebugShowFps.selectedProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setDebugShowFps(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            isDebugTikzIncludeComments.selectedProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setDebugTikzIncludeComments(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            debugDrawBorders.selectedProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setDebugDrawBorders(newValue);
                dbController.setCurrentStyle(cStyle);
            });


            fontBasicSizeFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setFontBasicSize(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            strokeWidthDefaultFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setStrokeWidthDefault(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            connectorsWidthFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setConnectorsWidth(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            textPaddingFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setTextPadding(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            lineSpacingFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setLineSpacing(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            elementsSpacingFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setElementsSpacing(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            decisionBlocksPaddingFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setDecisionBlocksPadding(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            minDecisionShoulderLenFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setMinDecisionShoulderLen(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            dashLineWidthLineFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setDashLineWidthLine(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            dashLineWidthSpaceFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setDashLineWidthSpace(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            tileSizeFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setTileSize(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            tileStrokeWidthDefaultFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setTileStrokeWidthDefault(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            selectionBorderWidthFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setSelectionBorderWidth(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            fontBasicSizeFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setFontBasicSize(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            containerOverflowPaddingFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setContainerOverflowPadding(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            maxBdContainerDragndropWidthFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setMaxBdContainerDragndropWidth(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            maxBdContainerDragndropWidthMarginFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setMaxBdContainerDragndropWidthMargin(newValue);
                dbController.setCurrentStyle(cStyle);
            });
            tilesInTileFormatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                cStyle.setTilesInTile(newValue);
                dbController.setCurrentStyle(cStyle);
            });

            setResultConverter(buttonType -> {
                if (!Objects.equals(ButtonBar.ButtonData.APPLY, buttonType.getButtonData())) {
                    return null;
                }

                return cStyle;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateStyleControls() {
        fontBasicName.setText(cStyle.getFontBasicName());
        strokeColor.setValue(cStyle.getStrokeColor());
        fontColor.setValue(cStyle.getFontColor());
        bdBackgroundColor.setValue(cStyle.getBdBackgroundColor());
        backgroundColor.setValue(cStyle.getBackgroundColor());
        fontBasicSizeFormatter.setValue((int) cStyle.getFontBasicSize());
        strokeWidthDefaultFormatter.setValue((int) cStyle.getStrokeWidthDefault());

        connectorsWidthFormatter.setValue((int) cStyle.getConnectorsWidth());
        textPaddingFormatter.setValue((int) cStyle.getTextPadding());
        lineSpacingFormatter.setValue((int) cStyle.getLineSpacing());
        elementsSpacingFormatter.setValue((int) cStyle.getElementsSpacing());
        decisionBlocksPaddingFormatter.setValue((int) cStyle.getDecisionBlocksPadding());
        minDecisionShoulderLenFormatter.setValue((int) cStyle.getMinDecisionShoulderLen());
        dashLineWidthLineFormatter.setValue((int) cStyle.getDashLineWidthLine());

        dashLineWidthSpaceFormatter.setValue((int) cStyle.getDashLineWidthSpace());
        positiveBranchText.setText(cStyle.getPositiveBranchText());
        negativeBranchText.setText(cStyle.getNegativeBranchText());
        gridColor.setValue(cStyle.getGridColor());
        selectedColor.setValue(cStyle.getSelectedColor());
        overflowSelectionColor.setValue(cStyle.getOverflowSelectionColor());
        dragndropForegroundColor.setValue(cStyle.getDragndropForegroundColor());

        tileSizeFormatter.setValue((int) cStyle.getTileSize());
        tileStrokeWidthDefaultFormatter.setValue((int) cStyle.getTileStrokeWidthDefault());
        selectionBorderWidthFormatter.setValue((int) cStyle.getSelectionBorderWidth());
        containerOverflowPaddingFormatter.setValue((int) cStyle.getContainerOverflowPadding());
        maxBdContainerDragndropWidthFormatter.setValue((int) cStyle.getMaxBdContainerDragndropWidth());
        maxBdContainerDragndropWidthMarginFormatter.setValue((int) cStyle.getMaxBdContainerDragndropWidthMargin());

        tilesInTileFormatter.setValue(cStyle.getTilesInTile());
        isDebugModeEnabled.setSelected(cStyle.isDebugModeEnabled());
        isDebugShowFps.setSelected(cStyle.isDebugShowFps());
        isDebugTikzIncludeComments.setSelected(cStyle.isDebugTikzIncludeComments());
        debugDrawBorders.setSelected(cStyle.isDebugDrawBorders());
        debugBorderColor.setValue(cStyle.getDebugBorderColor());

        for (Control control : elements)
            control.setDisable(cStyle.getStyleName().equals(Style.DEFAULT_STYLE_NAME));
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
