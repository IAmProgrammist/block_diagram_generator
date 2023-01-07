package rchat.info.blockdiagramgenerator.model;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.DiagramBlockController;
import rchat.info.blockdiagramgenerator.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.Utils;

import java.util.Arrays;

public class BDPreProcess extends BDElement {
    public BDPreProcess(DiagramBlockController context, String data) {
        super(context, Arrays.asList(data.split("\n")));
        recalculateSizes();
    }

    @Override
    public void drawElement(Pair<Double, Double> drawPoint) {
        Font basicFont = new Font(DiagramBlockModel.FONT_BASIC_NAME, DiagramBlockModel.FONT_BASIC_SIZE * context.model.canvasScale);
        Dimension2D size = getSize();
        double textHeight = size.getHeight() * context.model.canvasScale;
        double totalWidth = size.getWidth() * context.model.canvasScale;
        GraphicsContext gc = context.canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
        gc.strokeRect((drawPoint.getKey() + context.model.posX) * context.model.canvasScale, (drawPoint.getValue() + context.model.posY) * context.model.canvasScale, totalWidth, textHeight);
        gc.strokeRect((drawPoint.getKey() + context.model.posX + 0.15 * textHeight / context.model.canvasScale) * context.model.canvasScale, (drawPoint.getValue() + context.model.posY) * context.model.canvasScale,
                totalWidth - 0.3 * textHeight, textHeight);
        gc.setFont(basicFont);
        double currentLevel = DiagramBlockModel.TEXT_PADDING * context.model.canvasScale -
                DiagramBlockModel.LINE_SPACING * context.model.canvasScale;
        for (String line : this.data) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.fillText(line, (totalWidth - d.getWidth()) / 2 + ((drawPoint.getKey() + context.model.posX) * context.model.canvasScale), (drawPoint.getValue() + context.model.posY) * context.model.canvasScale + currentLevel);
            currentLevel += DiagramBlockModel.LINE_SPACING * context.model.canvasScale;
        }
        //TODO: Add alignment
    }

    @Override
    public Pair<Double, Double> getLeftConnector(Pair<Double, Double> pos) {
        Dimension2D size = getSize();
        return new Pair<>(pos.getKey(), pos.getValue() + size.getHeight() / 2);
    }

    @Override
    public Pair<Double, Double> getRightConnector(Pair<Double, Double> pos) {
        Dimension2D size = getSize();
        return new Pair<>(pos.getKey() + size.getWidth(), pos.getValue() + size.getHeight() / 2);
    }

    @Override
    public Pair<Double, Double> getTopConnector(Pair<Double, Double> pos) {
        Dimension2D size = getSize();
        return new Pair<>(pos.getKey() + size.getWidth() / 2, pos.getValue());
    }

    @Override
    public Pair<Double, Double> getBottomConnector(Pair<Double, Double> pos) {
        Dimension2D size = getSize();
        return new Pair<>(pos.getKey() + size.getWidth() / 2, pos.getValue() + size.getHeight());
    }

    @Override
    public void recalculateSizes() {
        double maxLineLen = 0;
        Font basicFont = new Font(DiagramBlockModel.FONT_BASIC_NAME, DiagramBlockModel.FONT_BASIC_SIZE);
        double textHeight = this.data.size() == 0 ? Utils.computeTextWidth(basicFont, "").getHeight() : 0;
        for (String line : this.data) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            if (d.getWidth() > maxLineLen) {
                maxLineLen = d.getWidth();
            }
            textHeight += d.getHeight() + DiagramBlockModel.LINE_SPACING;
        }
        textHeight += 2 * DiagramBlockModel.TEXT_PADDING;
        textHeight -= DiagramBlockModel.LINE_SPACING;
        Dimension2D size = new Dimension2D(Math.max(maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING + 0.3 * textHeight, textHeight * 2),
                textHeight);
        double leftBound = size.getWidth() / 2;
        recalculateSizes(size, leftBound, leftBound);
    }
}
