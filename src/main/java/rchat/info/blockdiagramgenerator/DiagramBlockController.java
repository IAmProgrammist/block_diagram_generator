package rchat.info.blockdiagramgenerator;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rchat.info.blockdiagramgenerator.model.*;

import java.io.IOException;

public class DiagramBlockController extends Application {
    public DiagramBlockModel model;
    public DiagramBlockView view;
    @FXML
    public Canvas canvas;
    @FXML
    public VBox root;

    @FXML
    public void initialize() {
        model = DiagramBlockModel.initDefault();
        view = new DiagramBlockView(this);
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

        model.root = new BDContainer(this,
                new BDTerminator(this, "Начало"),
                new BDData(this, "Ввод input"),
                new BDDecision(
                        new BDContainer(this, new BDData(this, "Вывод\n\"Последовательность\nпуста\"")), BDDecision.Branch.LEFT,
                        new BDContainer(this,
                            new BDProcess(this, "min := input"),
                            new BDData(this, "Ввод input"),
                            new BDDecision(new BDContainer(this, new BDData(this, "Вывод\n\"Последовательность\nсодержит только один\nэлемент\"")), BDDecision.Branch.LEFT,
                                           new BDContainer(this,
                                                   new BDProcess(this, "nextAfterMin := input"),
                                                   new BDCycleNotFixed(this, "input ≠ 0",
                                                           new BDContainer(this,
                                                                   new BDProcess(this, "previousElement :=\ninput"),
                                                                   new BDData(this, "Ввод input"),
                                                                   new BDDecision(
                                                                           new BDContainer(this,
                                                                                   new BDProcess(this, "min :=\npreviousElement"),
                                                                                   new BDProcess(this, "nextAfterMin := input")
                                                                           ), BDDecision.Branch.LEFT,
                                                                           new BDContainer(this),
                                                                           BDDecision.Branch.RIGHT,
                                                                           this,
                                                                           "previousElement ≤ min"
                                                                   ),
                                                                   new BDDecision(new BDContainer(this, new BDData(this, "Вывод \"Последний\nэлемент\nпоследовательности\nминимальный\"")), BDDecision.Branch.LEFT, new BDContainer(this, new BDData(this, "Вывод nextAfterMin")), BDDecision.Branch.RIGHT, this, "nextAfterMin = 0")
                                                           )
                                                   )
                                           ), BDDecision.Branch.RIGHT, this, "input = 0")
                        ), BDDecision.Branch.RIGHT, this, "input = 0"),
                new BDTerminator(this, "Конец"));

        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            DiagramBlockModel.canvasWidth = newValue.doubleValue();
            canvas.setWidth(DiagramBlockModel.canvasWidth);
            view.repaint();
        });
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            DiagramBlockModel.canvasHeight = newValue.doubleValue();
            canvas.setHeight(DiagramBlockModel.canvasHeight);
            view.repaint();
        });
        canvas.setOnMouseMoved(event -> {
            DiagramBlockModel.mousePosX = event.getX();
            DiagramBlockModel.mousePosY = event.getY();
        });
        view.repaint();
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

        view.repaint();
    }

    public void onCanvasMouseDragStarted(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            model.startX = event.getX();
            model.startY = event.getY();
        }
    }
    public void onCanvasMouseDragged(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            model.posX += (event.getX() - model.startX) / model.canvasScale;
            model.posY += (event.getY() - model.startY) / model.canvasScale;
            model.startX = event.getX();
            model.startY = event.getY();
            view.repaint();
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DiagramBlockController.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), DiagramBlockModel.canvasWidth, DiagramBlockModel.canvasHeight);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}