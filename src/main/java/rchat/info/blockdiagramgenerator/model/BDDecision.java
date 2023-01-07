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

public class BDDecision extends BDElement{
    private BDContainer positive;
    private Branch positiveBranch;
    private BDContainer negative;
    private Branch negativeBranch;
    public static enum Branch {
        LEFT,
        CENTER,
        RIGHT
    }
    public BDDecision(BDContainer positive, Branch positiveBranch, BDContainer negative, Branch negativeBranch,
                      DiagramBlockController context, String data) {
        super(context, Arrays.asList(data.split("\n")));

        if (positiveBranch == negativeBranch)
            throw new IllegalArgumentException("Branches can't be the same");

        this.positive = positive;
        this.positiveBranch = positiveBranch;
        this.negative = negative;
        this.negativeBranch = negativeBranch;

        recalculateSizes();
    }

    // note that drawpoint is left top corner of the rhombus, no the rest of the branches.
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

        if (positiveBranch == Branch.CENTER || negativeBranch == Branch.CENTER) {
            BDContainer centerBranch;
            boolean isCenterPositive;
            BDContainer restContainer;
            Branch restBranch;
            if (positiveBranch == Branch.CENTER) {
                centerBranch = positive;
                isCenterPositive = true;
                restContainer = negative;
                restBranch = negativeBranch;
            } else {
                centerBranch = negative;
                isCenterPositive = false;
                restContainer = positive;
                restBranch = positiveBranch;
            }
            Pair<Double, Double> bottomRhombusConnector = getBottomRhombusConnector(drawPoint);
            Pair<Double, Double> fakeDrawPoint = centerBranch.getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING));
            Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                    bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING);
            centerBranch.drawElement(trueCenterDrawPoint);
            gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
            gc.strokeLine((bottomRhombusConnector.getKey() + context.model.posX) * context.model.canvasScale,
                    (bottomRhombusConnector.getValue() + context.model.posY) * context.model.canvasScale,
                    (bottomRhombusConnector.getKey() + context.model.posX) * context.model.canvasScale,
                    (bottomRhombusConnector.getValue() + context.model.posY + DiagramBlockModel.ELEMENTS_SPACING) * context.model.canvasScale);
            if (isCenterPositive) {
                Dimension2D centerBranchTextSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.POSITIVE_BRANCH_TEXT);
                gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                        (bottomRhombusConnector.getKey() + context.model.posX - centerBranchTextSize.getWidth() / context.model.canvasScale - DiagramBlockModel.LINE_SPACING) * context.model.canvasScale,
                        (bottomRhombusConnector.getValue() + context.model.posY + centerBranchTextSize.getHeight() / context.model.canvasScale) * context.model.canvasScale);
            } else {
                Dimension2D centerBranchTextSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.NEGATIVE_BRANCH_TEXT);
                gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                        (bottomRhombusConnector.getKey() + context.model.posX - centerBranchTextSize.getWidth() / context.model.canvasScale - DiagramBlockModel.LINE_SPACING) * context.model.canvasScale,
                        (bottomRhombusConnector.getValue() + context.model.posY + centerBranchTextSize.getHeight() / context.model.canvasScale) * context.model.canvasScale);
            }

            Pair<Double, Double> bottomRestConnector = null;
            Pair<Double, Double> bottomCenterConnector = centerBranch.getBottomConnector(trueCenterDrawPoint);

            if (restBranch == Branch.LEFT) {
                double restShoulderLen = centerBranch.getDistanceToLeftBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        + restContainer.getSize().getWidth() - rhombusWidth / context.model.canvasScale / 2;

                Pair<Double, Double> rhombusLeftConnector = getLeftRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusLeftConnector.getKey() - restShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getTopConnector(drawRestPoint);
                if (rhombusLeftConnector.getKey() - restConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + restConnector.getKey()),
                            drawRestPoint.getValue());
                    restConnector = restContainer.getTopConnector(drawRestPoint);
                }
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + rhombusLeftConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + restConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + restConnector.getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + rhombusLeftConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + rhombusLeftConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + restConnector.getValue()) * context.model.canvasScale},
                        3);
                if (isCenterPositive) {
                    gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                            (context.model.posX + restConnector.getKey()) * context.model.canvasScale,
                            (context.model.posY + rhombusLeftConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * context.model.canvasScale);
                } else {
                    gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                            (context.model.posX + restConnector.getKey()) * context.model.canvasScale,
                            (context.model.posY + rhombusLeftConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * context.model.canvasScale);

                }
                restContainer.drawElement(drawRestPoint);

                bottomRestConnector = restContainer.getBottomConnector(drawRestPoint);
            } else {
                double restShoulderLen = centerBranch.getDistanceToRightBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                         - rhombusWidth / context.model.canvasScale / 2;

                Pair<Double, Double> rhombusRightConnector = getRightRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusRightConnector.getKey() + restShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getTopConnector(drawRestPoint);
                if (restConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - restConnector.getKey() + rhombusRightConnector.getKey()),
                            drawRestPoint.getValue());
                    restConnector = restContainer.getTopConnector(drawRestPoint);
                }
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + rhombusRightConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + restConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + restConnector.getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + rhombusRightConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + rhombusRightConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + restConnector.getValue()) * context.model.canvasScale},
                        3);
                if (isCenterPositive) {
                    Dimension2D restTextSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.NEGATIVE_BRANCH_TEXT);
                    gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                            (context.model.posX + restConnector.getKey() - restTextSize.getWidth() / context.model.canvasScale) * context.model.canvasScale,
                            (context.model.posY + rhombusRightConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * context.model.canvasScale);
                } else {
                    Dimension2D restTextSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.POSITIVE_BRANCH_TEXT);
                    gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                            (context.model.posX + restConnector.getKey() - restTextSize.getWidth() / context.model.canvasScale) * context.model.canvasScale,
                            (context.model.posY + rhombusRightConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * context.model.canvasScale);

                }
                restContainer.drawElement(drawRestPoint);

                bottomRestConnector = restContainer.getBottomConnector(drawRestPoint);
            }

            double totalHeight = rhombusHeight / context.model.canvasScale + 2 * DiagramBlockModel.ELEMENTS_SPACING +
                    Math.max(centerBranch.getSize().getHeight(), restContainer.getSize().getHeight());


            gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
            gc.strokePolyline(
                    new double[]{(context.model.posX + bottomRestConnector.getKey()) * context.model.canvasScale,
                            (context.model.posX + bottomRestConnector.getKey()) * context.model.canvasScale,
                            (context.model.posX + getBottomRhombusConnector(drawPoint).getKey()) * context.model.canvasScale},
                    new double[]{(context.model.posY + bottomRestConnector.getValue()) * context.model.canvasScale,
                            (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale,
                            (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale},
                    3);

            gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
            gc.strokePolyline(
                    new double[]{(context.model.posX + bottomCenterConnector.getKey()) * context.model.canvasScale,
                            (context.model.posX + bottomCenterConnector.getKey()) * context.model.canvasScale,
                            (context.model.posX + getBottomRhombusConnector(drawPoint).getKey()) * context.model.canvasScale},
                    new double[]{(context.model.posY + bottomCenterConnector.getValue()) * context.model.canvasScale,
                            (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale,
                            (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale},
                    3);
        } else {
            Dimension2D negativeSize = negative.getSize();
            Dimension2D positiveSize = positive.getSize();
            Pair<Double, Double> rhombusLeftConnector = getLeftRhombusConnector(drawPoint);
            Pair<Double, Double> rhombusRightConnector = getRightRhombusConnector(drawPoint);
            double totalHeight = rhombusHeight / context.model.canvasScale + 2 * DiagramBlockModel.ELEMENTS_SPACING +
                    Math.max(negativeSize.getHeight(), positiveSize.getHeight());

            if(negativeBranch == Branch.LEFT) {
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + negativeSize.getWidth() - rhombusWidth / context.model.canvasScale / 2;
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / context.model.canvasScale / 2;

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusLeftConnector.getKey() - negativeShoulderLen,
                         getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = negative.getTopConnector(drawNegativePoint);
                if (rhombusLeftConnector.getKey() - negConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + negConnector.getKey()),
                            drawNegativePoint.getValue());
                    negConnector = negative.getTopConnector(drawNegativePoint);
                }

                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + rhombusLeftConnector.getKey()) * context.model.canvasScale,
                                     (context.model.posX + negConnector.getKey()) * context.model.canvasScale,
                                     (context.model.posX + negConnector.getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + rhombusLeftConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + rhombusLeftConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + negConnector.getValue()) * context.model.canvasScale},
                        3);
                gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                        (context.model.posX + negConnector.getKey()) * context.model.canvasScale,
                        (context.model.posY + rhombusLeftConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * context.model.canvasScale);
                negative.drawElement(drawNegativePoint);

                Pair<Double, Double> bottomNegConnector = negative.getBottomConnector(drawNegativePoint);

                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + bottomNegConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + bottomNegConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + getBottomRhombusConnector(drawPoint).getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + bottomNegConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale,
                                (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale},
                        3);

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusRightConnector.getKey() + positiveShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = positive.getTopConnector(drawPositivePoint);
                if (posConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (posConnector.getKey() - rhombusRightConnector.getKey())),
                            drawPositivePoint.getValue());
                    posConnector = positive.getTopConnector(drawPositivePoint);
                }

                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + rhombusRightConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + posConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + posConnector.getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + rhombusRightConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + rhombusRightConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + posConnector.getValue()) * context.model.canvasScale},
                        3);
                gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                        (context.model.posX + posConnector.getKey() - Utils.computeTextWidth(basicFont, DiagramBlockModel.POSITIVE_BRANCH_TEXT).getWidth() / context.model.canvasScale) * context.model.canvasScale,
                        (context.model.posY + rhombusRightConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * context.model.canvasScale);
                positive.drawElement(drawPositivePoint);

                Pair<Double, Double> bottomPosConnector = positive.getBottomConnector(drawPositivePoint);
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + bottomPosConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + bottomPosConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + getBottomRhombusConnector(drawPoint).getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + bottomPosConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale,
                                (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale},
                        3);
            } else {
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + positiveSize.getWidth() - rhombusWidth / context.model.canvasScale / 2;
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / context.model.canvasScale / 2;

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusLeftConnector.getKey() - positiveShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = positive.getTopConnector(drawPositivePoint);
                if (rhombusLeftConnector.getKey() - posConnector.getKey()  < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + posConnector.getKey()),
                            drawPositivePoint.getValue());
                    posConnector = positive.getTopConnector(drawPositivePoint);
                }
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + rhombusLeftConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + posConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + posConnector.getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + rhombusLeftConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + rhombusLeftConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + posConnector.getValue()) * context.model.canvasScale},
                        3);
                positive.drawElement(drawPositivePoint);
                gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                        (context.model.posX + posConnector.getKey()) * context.model.canvasScale,
                        (context.model.posY + rhombusLeftConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * context.model.canvasScale);

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusRightConnector.getKey() + negativeShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = negative.getTopConnector(drawNegativePoint);
                if (negConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (negConnector.getKey() - rhombusRightConnector.getKey())),
                            drawNegativePoint.getValue());
                    negConnector = negative.getTopConnector(drawNegativePoint);
                }
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + rhombusRightConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + negConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + negConnector.getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + rhombusRightConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + rhombusRightConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + negConnector.getValue()) * context.model.canvasScale},
                        3);
                gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                        (context.model.posX + negConnector.getKey() - Utils.computeTextWidth(basicFont, DiagramBlockModel.NEGATIVE_BRANCH_TEXT).getWidth() / context.model.canvasScale) * context.model.canvasScale,
                        (context.model.posY + rhombusRightConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * context.model.canvasScale);
                negative.drawElement(drawNegativePoint);

                Pair<Double, Double> bottomNegConnector = negative.getBottomConnector(drawNegativePoint);
                Pair<Double, Double> bottomPosConnector = positive.getBottomConnector(drawPositivePoint);
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + bottomNegConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + bottomNegConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + getBottomRhombusConnector(drawPoint).getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + bottomNegConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale,
                                (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale},
                        3);

                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * context.model.canvasScale);
                gc.strokePolyline(
                        new double[]{(context.model.posX + bottomPosConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + bottomPosConnector.getKey()) * context.model.canvasScale,
                                (context.model.posX + getBottomRhombusConnector(drawPoint).getKey()) * context.model.canvasScale},
                        new double[]{(context.model.posY + bottomPosConnector.getValue()) * context.model.canvasScale,
                                (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale,
                                (context.model.posY + getTopRhombusConnector(drawPoint).getValue() + totalHeight) * context.model.canvasScale},
                        3);
            }
        }
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

    @Override
    public void recalculateSizes() {
        Dimension2D rhombusSize = getRhombusSize();
        double rhombusWidth = rhombusSize.getWidth();
        Pair<Double, Double> drawPoint = new Pair<>(0.0, 0.0);
        double leftPoint;
        double leftBound;
        double rightPoint;
        double rightBound;
        Pair<Double, Double> topConnector = getTopRhombusConnector(drawPoint);

        if (positiveBranch == Branch.CENTER || negativeBranch == Branch.CENTER) {
            BDContainer centerBranch;
            BDContainer restContainer;
            Branch restBranch;
            if (positiveBranch == Branch.CENTER) {
                centerBranch = positive;
                restContainer = negative;
                restBranch = negativeBranch;
            } else {
                centerBranch = negative;
                restContainer = positive;
                restBranch = positiveBranch;
            }
            Pair<Double, Double> bottomRhombusConnector = getBottomRhombusConnector(drawPoint);
            Pair<Double, Double> fakeDrawPoint = centerBranch.getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING));
            Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                    bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING);
            leftPoint = Math.min(trueCenterDrawPoint.getKey(), getLeftRhombusConnector(drawPoint).getKey());
            rightPoint = Math.max(trueCenterDrawPoint.getKey() + centerBranch.getSize().getWidth(),
                    getRightRhombusConnector(drawPoint).getKey());

            if (restBranch == Branch.LEFT) {
                double restShoulderLen = centerBranch.getDistanceToLeftBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        + restContainer.getSize().getWidth() - rhombusWidth / 2;

                Pair<Double, Double> rhombusLeftConnector = getLeftRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusLeftConnector.getKey() - restShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getTopConnector(drawRestPoint);
                if (rhombusLeftConnector.getKey() - restConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + restConnector.getKey()),
                            drawRestPoint.getValue());
                }

                leftPoint = drawRestPoint.getKey();
            } else {
                double restShoulderLen = centerBranch.getDistanceToRightBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        - rhombusWidth / 2;

                Pair<Double, Double> rhombusRightConnector = getRightRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusRightConnector.getKey() + restShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getTopConnector(drawRestPoint);
                if (restConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - restConnector.getKey() + rhombusRightConnector.getKey()),
                            drawRestPoint.getValue());
                }

                rightPoint = drawRestPoint.getKey() + restContainer.getSize().getWidth();
            }

            // Left bound
            leftBound = Math.abs(topConnector.getKey() - leftPoint);
            // Right bound
            rightBound = Math.abs(topConnector.getKey() - rightPoint);
        } else {
            Dimension2D negativeSize = negative.getSize();
            Dimension2D positiveSize = positive.getSize();
            Pair<Double, Double> rhombusLeftConnector = getLeftRhombusConnector(drawPoint);
            Pair<Double, Double> rhombusRightConnector = getRightRhombusConnector(drawPoint);

            if(negativeBranch == Branch.LEFT) {
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + negativeSize.getWidth() - rhombusWidth / 2;
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / 2;

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusLeftConnector.getKey() - negativeShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = negative.getTopConnector(drawNegativePoint);
                if (rhombusLeftConnector.getKey() - negConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + negConnector.getKey()),
                            drawNegativePoint.getValue());
                }
                leftPoint = drawNegativePoint.getKey();

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusRightConnector.getKey() + positiveShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = positive.getTopConnector(drawPositivePoint);
                if (posConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (posConnector.getKey() - rhombusRightConnector.getKey())),
                            drawPositivePoint.getValue());
                }
                rightPoint = drawPositivePoint.getKey() + positive.getSize().getWidth();
                // Left bound
                leftBound = Math.abs(topConnector.getKey() - drawNegativePoint.getKey());
                // Right bound
                rightBound = Math.abs(topConnector.getKey() - (drawPositivePoint.getKey() + positive.getSize().getWidth()));
            } else {
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + positiveSize.getWidth() - rhombusWidth / 2;
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / 2;

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusLeftConnector.getKey() - positiveShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = positive.getTopConnector(drawPositivePoint);
                if (rhombusLeftConnector.getKey() - posConnector.getKey()  < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + posConnector.getKey()),
                            drawPositivePoint.getValue());
                }
                leftPoint = drawPositivePoint.getKey();

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusRightConnector.getKey() + negativeShoulderLen,
                        getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = negative.getTopConnector(drawNegativePoint);
                if (negConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (negConnector.getKey() - rhombusRightConnector.getKey())),
                            drawNegativePoint.getValue());
                }
                rightPoint = drawNegativePoint.getKey() + negative.getSize().getWidth();
                // Left bound
                leftBound = Math.abs(topConnector.getKey() - drawPositivePoint.getKey());
                // Right bound
                rightBound = Math.abs(topConnector.getKey() - (drawNegativePoint.getKey() + negative.getSize().getWidth()));
            }
        }
        Dimension2D size = new Dimension2D(Math.abs(rightPoint - leftPoint),
                rhombusSize.getHeight() + 2 * DiagramBlockModel.ELEMENTS_SPACING + Math.max(negative.getSize().getHeight(), positive.getSize().getHeight()));
        recalculateSizes(size, leftBound, rightBound);
    }
}
