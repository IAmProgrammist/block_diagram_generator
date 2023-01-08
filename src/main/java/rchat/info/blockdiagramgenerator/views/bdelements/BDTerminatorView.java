package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDTerminatorModel;

public class BDTerminatorView extends BDElementView {
    protected BDTerminatorModel model;
    public BDTerminatorView(BDTerminatorModel model) {
        this.model = model;
    }

    @Override
    public void repaint(GraphicsContext gc, Pair<Double, Double> drawPoint, double scale) {
        Font basicFont = new Font(DiagramBlockModel.FONT_BASIC_NAME, DiagramBlockModel.FONT_BASIC_SIZE * scale);
        Dimension2D size = model.getSize();
        double textHeight = size.getHeight() * scale;
        double totalWidth = size.getWidth() * scale;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
        gc.strokeRoundRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale, totalWidth, textHeight, textHeight, textHeight);
        gc.setFont(basicFont);
        double currentLevel = DiagramBlockModel.TEXT_PADDING * scale -
                DiagramBlockModel.LINE_SPACING * scale;
        for (String line : this.model.data) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.fillText(line, (totalWidth - d.getWidth()) / 2 + (drawPoint.getKey() * scale), drawPoint.getValue() * scale + currentLevel);
            currentLevel += DiagramBlockModel.LINE_SPACING * scale;
        }
    }
}
