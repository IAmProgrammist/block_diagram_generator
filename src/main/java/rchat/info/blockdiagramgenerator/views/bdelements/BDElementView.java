package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

public abstract class BDElementView {
    public abstract void repaint(AbstractPainter gc, Pair<Double, Double> drawPoint, Font basicFont, boolean selectionOverflow,
                                 boolean selected, boolean isViewport, boolean isDragmode, double scale, Style style);

    public void drawOverflowBorder(AbstractPainter gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale, Style style) {
        gc.setStroke(style.getOverflowSelectionColor());
        gc.setLineWidth(style.getSelectionBorderWidth());
        gc.strokeRect((drawPoint.getKey() - style.getSelectionBorderWidth()) * scale,
                (drawPoint.getValue() - style.getSelectionBorderWidth()) * scale,
                (size.getWidth() + 2 * style.getSelectionBorderWidth()) * scale,
                (size.getHeight() + 2 * style.getSelectionBorderWidth()) * scale);
    }
    public void drawSelectBorder(AbstractPainter gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale, Style style) {
        gc.setStroke(style.getSelectedColor());
        gc.setLineWidth(style.getSelectionBorderWidth());
        gc.strokeRect((drawPoint.getKey() - style.getSelectionBorderWidth()) * scale,
                (drawPoint.getValue() - style.getSelectionBorderWidth()) * scale,
                (size.getWidth() + 2 * style.getSelectionBorderWidth()) * scale,
                (size.getHeight() + 2 * style.getSelectionBorderWidth()) * scale);
    }

    public void drawDragNDropForeground(AbstractPainter gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale, Style style) {
        gc.setFill(style.getDragndropForegroundColor());
        gc.fillRect((drawPoint.getKey() - style.getSelectionBorderWidth()) * scale,
                (drawPoint.getValue() - style.getSelectionBorderWidth()) * scale,
                (size.getWidth() + 2 * style.getSelectionBorderWidth()) * scale,
                (size.getHeight() + 2 * style.getSelectionBorderWidth()) * scale);
    }
}
