package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDataModel;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.basicFont;

public class BDDataView extends BDElementView {
    protected BDDataModel model;
    public BDDataView(BDDataModel model) {
        this.model = model;
    }
    @Override
    public void repaint(GraphicsContext gc, Pair<Double, Double> drawPoint,
                        boolean selectionOverflow, boolean selected, double scale) {
        Dimension2D size = model.getSize();
        double textHeight = size.getHeight() * scale;
        double totalWidth = size.getWidth() * scale;
        if (selected) {
            drawSelectBorder(gc, drawPoint, model.getSize(), scale);
        } else if (selectionOverflow) {
            drawOverflowBorder(gc, drawPoint, model.getSize(), scale);
        }
        gc.setFill(DiagramBlockModel.BD_BACKGROUND_COLOR);
        gc.fillPolygon(new double[]{(drawPoint.getKey()) * scale,
                        (drawPoint.getKey()) * scale + textHeight / 4,
                        (drawPoint.getKey()) * scale + totalWidth,
                        (drawPoint.getKey()) * scale + totalWidth - textHeight / 4},
                new double[]{(drawPoint.getValue()) * scale + textHeight,
                        (drawPoint.getValue()) * scale,
                        (drawPoint.getValue()) * scale,
                        (drawPoint.getValue()) * scale + textHeight}, 4);
        gc.setStroke(DiagramBlockModel.STROKE_COLOR);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
        gc.strokePolygon(new double[]{(drawPoint.getKey()) * scale,
                        (drawPoint.getKey()) * scale + textHeight / 4,
                        (drawPoint.getKey()) * scale + totalWidth,
                        (drawPoint.getKey()) * scale + totalWidth - textHeight / 4},
                new double[]{(drawPoint.getValue()) * scale + textHeight,
                        (drawPoint.getValue()) * scale,
                        (drawPoint.getValue()) * scale,
                        (drawPoint.getValue()) * scale + textHeight}, 4);
        gc.setFont(basicFont);
        double currentLevel = DiagramBlockModel.TEXT_PADDING * scale -
                DiagramBlockModel.LINE_SPACING * scale;
        for (String line : this.model.data) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.setFill(DiagramBlockModel.FONT_COLOR);
            gc.fillText(line, (totalWidth - d.getWidth()) / 2 + ((drawPoint.getKey()) * scale), (drawPoint.getValue()) * scale + currentLevel);
            currentLevel += DiagramBlockModel.LINE_SPACING * scale;
        }
    }
}
