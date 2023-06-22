package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDataModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.basicFont;

public class BDDataView extends BDElementView {
    protected BDDataModel model;
    public BDDataView(BDDataModel model) {
        this.model = model;
    }
    @Override
    public void repaint(AbstractPainter gc, Pair<Double, Double> drawPoint,
                        boolean selectionOverflow, boolean selected, double scale, Style style) {
        Dimension2D size = model.getSize();
        double textHeight = size.getHeight() * scale;
        double totalWidth = size.getWidth() * scale;

        gc.setFill(style.getBdBackgroundColor());
        gc.fillPolygon(new double[]{(drawPoint.getKey()) * scale,
                        (drawPoint.getKey()) * scale + textHeight / 4,
                        (drawPoint.getKey()) * scale + totalWidth,
                        (drawPoint.getKey()) * scale + totalWidth - textHeight / 4},
                new double[]{(drawPoint.getValue()) * scale + textHeight,
                        (drawPoint.getValue()) * scale,
                        (drawPoint.getValue()) * scale,
                        (drawPoint.getValue()) * scale + textHeight}, 4);
        gc.setStroke(style.getStrokeColor());
        gc.setLineWidth(style.getStrokeWidthDefault() * scale);
        gc.strokePolygon(new double[]{(drawPoint.getKey()) * scale,
                        (drawPoint.getKey()) * scale + textHeight / 4,
                        (drawPoint.getKey()) * scale + totalWidth,
                        (drawPoint.getKey()) * scale + totalWidth - textHeight / 4},
                new double[]{(drawPoint.getValue()) * scale + textHeight,
                        (drawPoint.getValue()) * scale,
                        (drawPoint.getValue()) * scale,
                        (drawPoint.getValue()) * scale + textHeight}, 4);
        gc.setFont(basicFont);
        double currentLevel = style.getTextPadding() * scale -
                style.getLineSpacing() * scale;
        for (String line : this.model.getDataLines()) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.setFill(style.getFontColor());
            gc.fillText(line, (totalWidth - d.getWidth()) / 2 + ((drawPoint.getKey()) * scale), (drawPoint.getValue()) * scale + currentLevel);
            currentLevel += style.getLineSpacing() * scale;
        }
        if (DiagramBlockModel.VIEWPORT_MODE) {
            if (selected) {
                drawSelectBorder(gc, drawPoint, model.getSize(), scale, style);
            } else if (selectionOverflow) {
                if (DiagramBlockModel.dragMode) {
                    drawDragNDropForeground(gc, drawPoint, model.getSize(), scale, style);
                } else drawOverflowBorder(gc, drawPoint, model.getSize(), scale, style);
            }
        }
    }
}
