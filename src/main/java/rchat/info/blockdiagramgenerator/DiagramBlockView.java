package rchat.info.blockdiagramgenerator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.model.*;

public class DiagramBlockView {
    public DiagramBlockController controller;

    public DiagramBlockView(DiagramBlockController diagramBlockController) {
        this.controller = diagramBlockController;
    }

    // Repaint canvas from controller.model
    public void repaint() {
        //Long now = System.currentTimeMillis();
        //if (now - controller.model.lastMoved < DiagramBlockModel.FPS_LIMITER) {
        //    return;
        //}
        GraphicsContext gc = this.controller.canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, DiagramBlockModel.canvasWidth, DiagramBlockModel.canvasHeight);
        gc.setStroke(Paint.valueOf("#D3D3D3"));
        gc.setLineWidth(DiagramBlockModel.TILE_STROKE_WIDTH_DEFAULT);
        double calculatedTileSize = Math.pow(DiagramBlockModel.TILES_IN_TILE, -Math.floor(Math.log(controller.model.canvasScale) / Math.log(DiagramBlockModel.TILES_IN_TILE))) * DiagramBlockModel.TILE_SIZE;
        for (double x = controller.model.posX - Math.ceil(controller.model.posX / calculatedTileSize) * calculatedTileSize;
             x < DiagramBlockModel.canvasWidth / controller.model.canvasScale;
             x += calculatedTileSize) {
            gc.strokeLine(x * controller.model.canvasScale, 0, x * controller.model.canvasScale, DiagramBlockModel.canvasHeight);
        }
        //TODO: breaks on canvasSize < 1 and scrolling down
        for (double y = (controller.model.posY - Math.ceil(controller.model.posY / calculatedTileSize) * calculatedTileSize);
             y < DiagramBlockModel.canvasHeight / controller.model.canvasScale;
             y += calculatedTileSize) {
            gc.strokeLine(0, y * controller.model.canvasScale, DiagramBlockModel.canvasWidth, y * controller.model.canvasScale);
        }
        controller.model.root.drawElement(new Pair<>(0.0, 0.0));
    }
}
