package rchat.info.blockdiagramgenerator.views;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

import java.awt.*;

public class DiagramBlockView {
    public DiagramBlockModel model;

    public DiagramBlockView(DiagramBlockModel model) {
        this.model = model;
    }

    // Repaint canvas from controller.model
    public void repaint(AbstractPainter gc, Dimension2D size, boolean isViewport, Style style) {

        gc.clearRect(0, 0, size.getWidth(), size.getHeight());
        gc.drawBackground(style.getBackgroundColor());
        if (isViewport) {
            int maxTilesInTile = 100;
            int minTileSize = 5;

            gc.setStroke(style.getGridColor());
            gc.setLineWidth(style.getTileStrokeWidthDefault());
            double calculatedTileSize = Math.pow(Math.min(style.getTilesInTile(), maxTilesInTile),
                    -Math.floor(Math.log(model.canvasScale) / Math.log(Math.min(style.getTilesInTile(), maxTilesInTile)))) * Math.max(minTileSize, style.getTileSize());
            for (double x = model.posX - Math.ceil(model.posX / calculatedTileSize) * calculatedTileSize;
                 x < size.getWidth() / model.canvasScale;
                 x += calculatedTileSize) {
                gc.strokeLine(x * model.canvasScale, 0, x * model.canvasScale, size.getHeight());
            }
            for (double y = (model.posY - Math.ceil(model.posY / calculatedTileSize) * calculatedTileSize);
                 y < size.getHeight() / model.canvasScale;
                 y += calculatedTileSize) {
                gc.strokeLine(0, y * model.canvasScale, size.getWidth(), y * model.canvasScale);
            }
        }

        model.root.update(gc, new Pair<>(model.posX, model.posY), model.canvasScale);
    }
}
