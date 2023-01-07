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

public class BDCycleNotFixed extends BDElement {
    BDContainer body;

    public BDCycleNotFixed(DiagramBlockController context, String data, BDContainer body) {
        super(context, Arrays.asList(data.split("\n")));
        this.body = body;
        recalculateSizes();
    }

    @Override
    public void drawElement(Pair<Double, Double> drawPoint) {
        Dimension2D rhombusSize = getRhombusSize();
        Dimension2D textSize = getRhombusTextSize();
        double rhombusWidth = rhombusSize.getWidth() * context.model.canvasScale;
        double rhombusHeight = rhombusSize.getHeight() * context.model.canvasScale;
        double textHeight = textSize.getHeight() * context.model.canvasScale;
        // Lazy fix
        if (DiagramBlockModel.IS_DEBUG_MODE_ENABLED) {
            if (DiagramBlockModel.DEBUG_DRAW_BORDERS) {
                GraphicsContext gc = context.canvas.getGraphicsContext2D();
                gc.setStroke(DiagramBlockModel.DEBUG_BORDER_COLOR);
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * context.model.canvasScale);
                gc.strokeRect((drawPoint.getKey() + context.model.posX) * context.model.canvasScale, (drawPoint.getValue() + context.model.posY) * context.model.canvasScale,
                        getSize().getWidth() * context.model.canvasScale, getSize().getHeight() * context.model.canvasScale);
            }
        }
        drawPoint = new Pair<>(drawPoint.getKey() + (getDistanceToLeftBound() - rhombusWidth / context.model.canvasScale / 2), drawPoint.getValue());

        Font basicFont = new Font(DiagramBlockModel.FONT_BASIC_NAME, DiagramBlockModel.FONT_BASIC_SIZE * context.model.canvasScale);
        GraphicsContext gc = context.canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
        gc.strokePolygon(new double[]{(drawPoint.getKey() + context.model.posX) * context.model.canvasScale,
                        (drawPoint.getKey() + context.model.posX) * context.model.canvasScale + rhombusWidth / 2,
                        (drawPoint.getKey() + context.model.posX) * context.model.canvasScale + rhombusWidth,
                        (drawPoint.getKey() + context.model.posX) * context.model.canvasScale + rhombusWidth / 2,
                        (drawPoint.getKey() + context.model.posX) * context.model.canvasScale},
                new double[]{(drawPoint.getValue() + context.model.posY) * context.model.canvasScale + rhombusHeight / 2,
                        (drawPoint.getValue() + context.model.posY) * context.model.canvasScale + rhombusHeight,
                        (drawPoint.getValue() + context.model.posY) * context.model.canvasScale + rhombusHeight / 2,
                        (drawPoint.getValue() + context.model.posY) * context.model.canvasScale,
                        (drawPoint.getValue() + context.model.posY) * context.model.canvasScale + rhombusHeight / 2}, 5);

        gc.setFont(basicFont);
        //TODO: This is so bad!
        double currentLevel = (rhombusHeight - textHeight) / 2 - 3 * DiagramBlockModel.LINE_SPACING * context.model.canvasScale;
        for (String line : this.data) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.fillText(line, (rhombusWidth - d.getWidth()) / 2 + ((drawPoint.getKey() + context.model.posX) * context.model.canvasScale), (drawPoint.getValue() + context.model.posY) * context.model.canvasScale + currentLevel);
            currentLevel += DiagramBlockModel.LINE_SPACING * context.model.canvasScale;
        }


        Pair<Double, Double> bottomRhombusConnector = getBottomRhombusConnector(drawPoint);
        Pair<Double, Double> topRhombusConnector = getTopRhombusConnector(drawPoint);
        Pair<Double, Double> rightRhombusConnector = getRightRhombusConnector(drawPoint);
        Pair<Double, Double> fakeDrawPoint = body.getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING));
        Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING);
        body.drawElement(trueCenterDrawPoint);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
        gc.strokeLine((bottomRhombusConnector.getKey() + context.model.posX) * context.model.canvasScale,
                (bottomRhombusConnector.getValue() + context.model.posY) * context.model.canvasScale,
                (bottomRhombusConnector.getKey() + context.model.posX) * context.model.canvasScale,
                (bottomRhombusConnector.getValue() + context.model.posY + DiagramBlockModel.ELEMENTS_SPACING) * context.model.canvasScale);
        Dimension2D centerBranchTextSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.POSITIVE_BRANCH_TEXT);
        gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                (bottomRhombusConnector.getKey() + context.model.posX - centerBranchTextSize.getWidth() / context.model.canvasScale - DiagramBlockModel.LINE_SPACING) * context.model.canvasScale,
                (bottomRhombusConnector.getValue() + context.model.posY + centerBranchTextSize.getHeight() / context.model.canvasScale) * context.model.canvasScale);

        double leftLineOffset = Math.max(Math.max(rhombusWidth / context.model.canvasScale / 2, body.getDistanceToLeftBound())
                + DiagramBlockModel.DECISION_BLOCKS_PADDING, DiagramBlockModel.MIN_DECISION_SHOULDER_LEN);
        Pair<Double, Double> bottomBodyConnector = body.getBottomConnector(trueCenterDrawPoint);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
        gc.strokePolyline(new double[]{(bottomBodyConnector.getKey() + context.model.posX) * context.model.canvasScale,
                        (bottomBodyConnector.getKey() + context.model.posX) * context.model.canvasScale,
                        (bottomBodyConnector.getKey() + context.model.posX - leftLineOffset) * context.model.canvasScale,
                        (bottomBodyConnector.getKey() + context.model.posX - leftLineOffset) * context.model.canvasScale,
                        (bottomBodyConnector.getKey() + context.model.posX) * context.model.canvasScale},
                new double[]{(bottomBodyConnector.getValue() + context.model.posY) * context.model.canvasScale,
                        (bottomBodyConnector.getValue() + context.model.posY + DiagramBlockModel.ELEMENTS_SPACING) * context.model.canvasScale,
                        (bottomBodyConnector.getValue() + context.model.posY + DiagramBlockModel.ELEMENTS_SPACING) * context.model.canvasScale,
                        (topRhombusConnector.getValue() - DiagramBlockModel.ELEMENTS_SPACING / 2 + context.model.posY) * context.model.canvasScale,
                        (topRhombusConnector.getValue() - DiagramBlockModel.ELEMENTS_SPACING / 2 + context.model.posY) * context.model.canvasScale}, 5);
        double rightLineOffset = Math.max(Math.max(rhombusWidth / context.model.canvasScale / 2, body.getDistanceToRightBound()) - rhombusWidth / context.model.canvasScale / 2
                + DiagramBlockModel.DECISION_BLOCKS_PADDING, DiagramBlockModel.MIN_DECISION_SHOULDER_LEN);
        double bottomPoint = rhombusHeight / context.model.canvasScale / 2 + 2 * DiagramBlockModel.ELEMENTS_SPACING + body.getSize().getHeight() + DiagramBlockModel.DECISION_BLOCKS_PADDING;

        textSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.NEGATIVE_BRANCH_TEXT);
        gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT, (rightRhombusConnector.getKey() + context.model.posX + rightLineOffset - textSize.getWidth() / context.model.canvasScale) * context.model.canvasScale,
                (rightRhombusConnector.getValue() + context.model.posY - DiagramBlockModel.TEXT_PADDING) * context.model.canvasScale);

        gc.strokePolyline(new double[]{
                        (rightRhombusConnector.getKey() + context.model.posX) * context.model.canvasScale,
                        (rightRhombusConnector.getKey() + context.model.posX + rightLineOffset) * context.model.canvasScale,
                        (rightRhombusConnector.getKey() + context.model.posX + rightLineOffset) * context.model.canvasScale,
                        (bottomBodyConnector.getKey() + context.model.posX) * context.model.canvasScale},
                new double[]{
                        (rightRhombusConnector.getValue() + context.model.posY) * context.model.canvasScale,
                        (rightRhombusConnector.getValue() + context.model.posY) * context.model.canvasScale,
                        (rightRhombusConnector.getValue() + context.model.posY + bottomPoint) * context.model.canvasScale,
                        (rightRhombusConnector.getValue() + context.model.posY + bottomPoint) * context.model.canvasScale}, 4);

    }

    @Override
    public void recalculateSizes() {
        Pair<Double, Double> drawPoint = new Pair<>(0.0, 0.0);
        Dimension2D rhombusSize = getRhombusSize();
        Pair<Double, Double> topConnector = getTopConnector(drawPoint);
        double rhombusWidth = rhombusSize.getWidth();
        double rhombusHeight = rhombusSize.getHeight();

        double leftLineOffset = Math.max(Math.max(rhombusWidth / 2, body.getDistanceToLeftBound())
                + DiagramBlockModel.DECISION_BLOCKS_PADDING, DiagramBlockModel.MIN_DECISION_SHOULDER_LEN);

        double leftBound = Math.abs(topConnector.getKey() - leftLineOffset);
        double rightLineOffset = Math.max(Math.max(rhombusWidth / 2, body.getDistanceToRightBound())
                + DiagramBlockModel.DECISION_BLOCKS_PADDING, DiagramBlockModel.MIN_DECISION_SHOULDER_LEN);
        double bottomPoint = rhombusHeight + 2 * DiagramBlockModel.ELEMENTS_SPACING + body.getSize().getHeight() + DiagramBlockModel.DECISION_BLOCKS_PADDING;
        double rightBound = Math.abs(topConnector.getKey() - rightLineOffset);
        Dimension2D size = new Dimension2D(Math.abs(leftBound + rightBound), bottomPoint);
        recalculateSizes(size, leftBound, rightBound);
    }

    public Dimension2D getRhombusSize() {
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
        textHeight -= DiagramBlockModel.LINE_SPACING;
        double textWidth = maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING;
        double diag = textHeight + textWidth / 2;
        return new Dimension2D(diag * 2, diag);
    }

    public Dimension2D getRhombusTextSize() {
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
        textHeight -= DiagramBlockModel.LINE_SPACING;
        double textWidth = maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING;
        return new Dimension2D(textWidth, textHeight);
    }

    @Override
    public Pair<Double, Double> getLeftConnector(Pair<Double, Double> pos) {
        throw new RuntimeException();
    }

    @Override
    public Pair<Double, Double> getRightConnector(Pair<Double, Double> pos) {
        throw new RuntimeException();
    }

    @Override
    public Pair<Double, Double> getTopConnector(Pair<Double, Double> pos) {
        return new Pair<>(pos.getKey() + getDistanceToLeftBound(), pos.getValue());
    }

    public Pair<Double, Double> getLeftRhombusConnector(Pair<Double, Double> pos) {
        Dimension2D size = getRhombusSize();
        return new Pair<>(pos.getKey(), pos.getValue() + size.getHeight() / 2);
    }

    public Pair<Double, Double> getRightRhombusConnector(Pair<Double, Double> pos) {
        Dimension2D size = getRhombusSize();
        return new Pair<>(pos.getKey() + size.getWidth(), pos.getValue() + size.getHeight() / 2);
    }

    public Pair<Double, Double> getBottomRhombusConnector(Pair<Double, Double> pos) {
        Dimension2D size = getRhombusSize();
        return new Pair<>(pos.getKey() + size.getWidth() / 2, pos.getValue() + size.getHeight());
    }

    public Pair<Double, Double> getTopRhombusConnector(Pair<Double, Double> pos) {
        Dimension2D size = getRhombusSize();
        return new Pair<>(pos.getKey() + size.getWidth() / 2, pos.getValue());
    }

    @Override
    public Pair<Double, Double> getBottomConnector(Pair<Double, Double> pos) {
        return new Pair<>(pos.getKey() + getDistanceToLeftBound(), pos.getValue() + getSize().getHeight());
    }

}
