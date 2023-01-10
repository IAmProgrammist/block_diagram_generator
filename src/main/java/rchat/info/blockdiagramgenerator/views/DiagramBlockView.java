package rchat.info.blockdiagramgenerator.views;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;

public class DiagramBlockView {
    public DiagramBlockModel model;

    public DiagramBlockView(DiagramBlockModel model) {
        this.model = model;
    }

    // Repaint canvas from controller.model
    public void repaint(GraphicsContext gc) {
        gc.clearRect(0, 0, DiagramBlockModel.canvasWidth, DiagramBlockModel.canvasHeight);
        gc.setFill(DiagramBlockModel.BACKGROUND_COLOR);
        gc.setStroke(DiagramBlockModel.GRID_COLOR);
        gc.setLineWidth(DiagramBlockModel.TILE_STROKE_WIDTH_DEFAULT);
        double calculatedTileSize = Math.pow(DiagramBlockModel.TILES_IN_TILE, -Math.floor(Math.log(model.canvasScale) / Math.log(DiagramBlockModel.TILES_IN_TILE))) * DiagramBlockModel.TILE_SIZE;
        for (double x = model.posX - Math.ceil(model.posX / calculatedTileSize) * calculatedTileSize;
             x < DiagramBlockModel.canvasWidth / model.canvasScale;
             x += calculatedTileSize) {
            gc.strokeLine(x * model.canvasScale, 0, x * model.canvasScale, DiagramBlockModel.canvasHeight);
        }
        for (double y = (model.posY - Math.ceil(model.posY / calculatedTileSize) * calculatedTileSize);
             y < DiagramBlockModel.canvasHeight / model.canvasScale;
             y += calculatedTileSize) {
            gc.strokeLine(0, y * model.canvasScale, DiagramBlockModel.canvasWidth, y * model.canvasScale);
        }
        model.root.update(gc, new Pair<>(model.posX, model.posY), model.canvasScale);
    }
}
