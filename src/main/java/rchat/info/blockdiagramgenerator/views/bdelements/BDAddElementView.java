package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDAddElementModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.basicFont;

public class BDAddElementView extends BDElementView{
    protected BDAddElementModel model;
    public BDAddElementView(BDAddElementModel model) {
        this.model = model;
    }
    @Override
    public void repaint(AbstractPainter gc, Pair<Double, Double> drawPoint,
                        boolean selectionOverflow, boolean selected, double scale) {
        Dimension2D size = model.getSize();
        double textHeight = size.getHeight() * scale;
        double totalWidth = size.getWidth() * scale;

        gc.setFill(DiagramBlockModel.BD_BACKGROUND_COLOR);
        gc.fillRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale, totalWidth, textHeight);

        gc.setLineDashes(DiagramBlockModel.DASH_LINE_WIDTH_LINE * scale, DiagramBlockModel.DASH_LINE_WIDTH_SPACE * scale);
        gc.setStroke(DiagramBlockModel.STROKE_COLOR);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
        gc.strokeRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale, totalWidth, textHeight);
        gc.setLineDashes();

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
        if (DiagramBlockModel.VIEWPORT_MODE) {
            if (selected) {
                drawSelectBorder(gc, drawPoint, model.getSize(), scale);
            } else if (selectionOverflow) {
                if (DiagramBlockModel.dragMode) {
                    drawDragNDropForeground(gc, drawPoint, model.getSize(), scale);
                } else drawOverflowBorder(gc, drawPoint, model.getSize(), scale);
            }
        }
    }
}
