package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDTerminatorModel;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.basicFont;

public class BDTerminatorView extends BDElementView {
    protected BDTerminatorModel model;
    public BDTerminatorView(BDTerminatorModel model) {
        this.model = model;
    }

    @Override
    public void repaint(GraphicsContext gc, Pair<Double, Double> drawPoint,
                        boolean selectionOverflow, boolean selected, double scale) {
        Dimension2D size = model.getSize();
        double textHeight = size.getHeight() * scale;
        double totalWidth = size.getWidth() * scale;
        gc.setFill(DiagramBlockModel.BD_BACKGROUND_COLOR);
        gc.fillRoundRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale, totalWidth, textHeight, textHeight, textHeight);
        gc.setStroke(DiagramBlockModel.STROKE_COLOR);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
        gc.strokeRoundRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale, totalWidth, textHeight, textHeight, textHeight);
        gc.setFont(basicFont);
        double currentLevel = DiagramBlockModel.TEXT_PADDING * scale -
                DiagramBlockModel.LINE_SPACING * scale;
        for (String line : this.model.getDataLines()) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.setFill(DiagramBlockModel.FONT_COLOR);
            gc.fillText(line, (totalWidth - d.getWidth()) / 2 + (drawPoint.getKey() * scale), drawPoint.getValue() * scale + currentLevel);
            currentLevel += DiagramBlockModel.LINE_SPACING * scale;
        }

        if (selected) {
            drawSelectBorder(gc, drawPoint, model.getSize(), scale);
        } else if (selectionOverflow) {
            if (DiagramBlockModel.dragMode) {
                drawDragNDropForeground(gc, drawPoint, model.getSize(), scale);
            } else drawOverflowBorder(gc, drawPoint, model.getSize(), scale);
        }
    }
}
