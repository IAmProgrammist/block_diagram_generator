package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDPreProcessModel;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.basicFont;

public class BDPreProcessView extends BDElementView {
    protected BDPreProcessModel model;
    public BDPreProcessView(BDPreProcessModel model) {
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
        gc.fillRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale, totalWidth, textHeight);
        gc.setStroke(DiagramBlockModel.STROKE_COLOR);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
        gc.strokeRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale, totalWidth, textHeight);
        gc.strokeRect((drawPoint.getKey() + 0.15 * textHeight / scale) * scale, drawPoint.getValue() * scale,
                totalWidth - 0.3 * textHeight, textHeight);
        gc.setFont(basicFont);
        double currentLevel = DiagramBlockModel.TEXT_PADDING * scale -
                DiagramBlockModel.LINE_SPACING * scale;
        for (String line : this.model.data) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.setFill(DiagramBlockModel.FONT_COLOR);
            gc.fillText(line, (totalWidth - d.getWidth()) / 2 + (drawPoint.getKey() * scale), drawPoint.getValue() * scale + currentLevel);
            currentLevel += DiagramBlockModel.LINE_SPACING * scale;
        }
    }
}
