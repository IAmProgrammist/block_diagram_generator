package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

public abstract class BDElementView {
    public abstract void repaint(AbstractPainter gc, Pair<Double, Double> drawPoint, boolean selectionOverflow,
                                 boolean selected, double scale);

    public void drawOverflowBorder(AbstractPainter gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale) {
        gc.setStroke(DiagramBlockModel.OVERFLOW_SELECTION_COLOR);
        gc.setLineWidth(DiagramBlockModel.SELECTION_BORDER_WIDTH);
        gc.strokeRect((drawPoint.getKey() - DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale,
                (drawPoint.getValue() - DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale,
                (size.getWidth() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale,
                (size.getHeight() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale);
    }
    public void drawSelectBorder(AbstractPainter gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale) {
        gc.setStroke(DiagramBlockModel.SELECTED_COLOR);
        gc.setLineWidth(DiagramBlockModel.SELECTION_BORDER_WIDTH);
        gc.strokeRect((drawPoint.getKey() - DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale,
                (drawPoint.getValue() - DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale,
                (size.getWidth() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale,
                (size.getHeight() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale);
    }

    public void drawDragNDropForeground(AbstractPainter gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale) {
        gc.setFill(DiagramBlockModel.DRAGNDROP_FOREGROUND_COLOR);
        gc.fillRect((drawPoint.getKey() - DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale,
                (drawPoint.getValue() - DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale,
                (size.getWidth() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale,
                (size.getHeight() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH) * scale);
    }
}
