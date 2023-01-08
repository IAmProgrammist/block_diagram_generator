package rchat.info.blockdiagramgenerator.controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import rchat.info.blockdiagramgenerator.DebounceDecorator;
import rchat.info.blockdiagramgenerator.controllers.bdelements.*;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.*;

public class DiagramBlockController {
    public DiagramBlockModel model;
    public DiagramBlockView view;
    @FXML
    public Canvas canvas;
    @FXML
    public VBox root;
    public History<DiagramBlockModel> applicationHistory = new History<>();
    public Runnable saveState = new Runnable() {
        @Override
        public void run() {
            DiagramBlockModel clonedAppState = model;
            applicationHistory.push(clonedAppState);
        }
    };

    @FXML
    public void initialize() {
        model = DiagramBlockModel.initDefault();
        view = new DiagramBlockView(model);
        /*model.root = new BDContainer(this, new BDProcess(this, "a := 12"), new BDPreProcess(this, "a := max(1, 3)"));
        BDContainer container2 = new BDContainer(this, new BDData(this, "a := 12"), new BDTerminator(this, "a := max(1,asdasd 3)"));
        BDContainer container3 = new BDContainer(this, new BDData(this, "a := 12"), new BDTerminator(this, "a := max(1, 3)"));
        BDContainer container4 = new BDContainer(this, new BDData(this, "a := 12"), new BDTerminator(this, "a := max(1, dasdasdasd3)"));
        container2.add(new BDDecision(container3, BDDecision.Branch.RIGHT, container4, BDDecision.Branch.LEFT, this, "a := max(1, 3)"));
        container2.add(new BDProcess(this, "Hello how a u"));
        model.root.add(new BDDecision(container3, BDDecision.Branch.RIGHT, container4, BDDecision.Branch.LEFT, this, "a := max(1, 3)"));
        BDDecision dec = new BDDecision(model.root, BDDecision.Branch.CENTER, container2, BDDecision.Branch.RIGHT, this, "Are you gay?");
        BDContainer killme = new BDContainer(this, dec, dec, dec);
        BDDecision c = new BDDecision(killme, BDDecision.Branch.LEFT, container2, BDDecision.Branch.RIGHT, this, "KILL ME");
        BDContainer cc = new BDContainer(this, c);
        cc.add(new BDTerminator(this, "Конец"));
        model.root = new BDContainer(this, new BDTerminator(this, "Начало"), new BDCycleNotFixed(this, "Hello World!", model.root), new BDTerminator(this, "Конец"));
        */



        model.root = new BDContainerController(this,
                new BDTerminatorController(this, "Начало"),
                new BDDataController(this, "Ввод input"),
                new BDDecisionController(this,
                        new BDContainerController(this, new BDDataController(this, "Вывод\n\"Последовательность\nпуста\"")), BDDecisionModel.Branch.LEFT,
                        new BDContainerController(this,
                            new BDProcessController(this, "min := input"),
                            new BDDataController(this, "Ввод input"),
                            new BDDecisionController(this, new BDContainerController(this, new BDDataController(this, "Вывод\n\"Последовательность\nсодержит только один\nэлемент\"")), BDDecisionModel.Branch.LEFT,
                                           new BDContainerController(this,
                                                   new BDProcessController(this, "nextAfterMin := input"),
                                                   new BDCycleFixedController(this,
                                                           new BDContainerController(this,
                                                                   new BDProcessController(this, "previousElement :=\ninput"),
                                                                   new BDDataController(this, "Ввод input"),
                                                                   new BDDecisionController(this,
                                                                           new BDContainerController(this,
                                                                                   new BDProcessController(this, "min :=\npreviousElement"),
                                                                                   new BDProcessController(this, "nextAfterMin := input")
                                                                           ), BDDecisionModel.Branch.LEFT,
                                                                           new BDContainerController(this),
                                                                           BDDecisionModel.Branch.RIGHT,
                                                                           "previousElement ≤ min"
                                                                   ),
                                                                   new BDDecisionController(this, new BDContainerController(this, new BDDataController(this, "Вывод \"Последний\nэлемент\nпоследовательности\nминимальный\"")), BDDecisionModel.Branch.LEFT, new BDContainerController(this, new BDDataController(this, "Вывод nextAfterMin")), BDDecisionModel.Branch.RIGHT,  "nextAfterMin = 0")
                                                           )
                                                   , "input ≠ 0")
                                           ), BDDecisionModel.Branch.RIGHT, "input = 0")
                        ), BDDecisionModel.Branch.RIGHT, "input = 0"),
                new BDTerminatorController(this, "Конец"));

        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            DiagramBlockModel.canvasWidth = newValue.doubleValue();
            canvas.setWidth(DiagramBlockModel.canvasWidth);
            view.repaint(canvas.getGraphicsContext2D());
        });
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            DiagramBlockModel.canvasHeight = newValue.doubleValue();
            canvas.setHeight(DiagramBlockModel.canvasHeight);
            view.repaint(canvas.getGraphicsContext2D());
        });
        canvas.setOnMouseMoved(event -> {
            DiagramBlockModel.mousePosX = event.getX();
            DiagramBlockModel.mousePosY = event.getY();
        });
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

        view.repaint(canvas.getGraphicsContext2D());
    }

    public void onCanvasMouseDragStarted(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            model.startX = event.getX();
            model.startY = event.getY();
        }
    }
    public void onCanvasMouseDragged(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            model.posX += (event.getX() -model.startX) / model.canvasScale;
            model.posY += (event.getY() - model.startY) / model.canvasScale;
            model.startX = event.getX();
            model.startY = event.getY();
            view.repaint(canvas.getGraphicsContext2D());
        }
    }
}