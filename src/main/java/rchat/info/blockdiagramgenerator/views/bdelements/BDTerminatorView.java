package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDTerminatorModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

public class BDTerminatorView extends BDElementView {
    protected BDTerminatorModel model;
    public BDTerminatorView(BDTerminatorModel model) {
        this.model = model;
    }

    @Override
    public void repaint(AbstractPainter gc, Pair<Double, Double> drawPoint, Font basicFont,
                        boolean selectionOverflow, boolean selected, boolean isViewport, boolean isDragmode, double scale, Style style) {
        Dimension2D size = model.getSize();
        double textHeight = size.getHeight() * scale;
        double totalWidth = size.getWidth() * scale;
        gc.setFill(style.getBdBackgroundColor());
        gc.fillRoundRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale, totalWidth, textHeight, textHeight, textHeight);
        gc.setStroke(style.getStrokeColor());
        gc.setLineWidth(style.getStrokeWidthDefault() * scale);
        gc.strokeRoundRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale, totalWidth, textHeight, textHeight, textHeight);
        gc.setFont(basicFont);
        double currentLevel = style.getTextPadding() * scale;
        for (String line : this.model.getDataLines()) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.setFill(style.getFontColor());
            gc.fillText(line, (totalWidth - d.getWidth()) / 2 + (drawPoint.getKey() * scale), drawPoint.getValue() * scale + currentLevel);
            currentLevel += style.getLineSpacing() * scale;
        }
        if (isViewport) {
            if (selected) {
                drawSelectBorder(gc, drawPoint, model.getSize(), scale, style);
            } else if (selectionOverflow) {
                if (isDragmode) {
                    drawDragNDropForeground(gc, drawPoint, model.getSize(), scale, style);
                } else drawOverflowBorder(gc, drawPoint, model.getSize(), scale, style);
            }
        }
    }
}
