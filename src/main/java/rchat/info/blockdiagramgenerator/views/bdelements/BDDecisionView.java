package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.basicFont;

public class BDDecisionView extends BDElementView {
    protected BDDecisionModel model;
    public BDDecisionView(BDDecisionModel model) {
        this.model = model;
    }
    @Override
    public void repaint(GraphicsContext gc, Pair<Double, Double> drawPoint,
                        boolean selectionOverflow, boolean selected, double scale) {
        Dimension2D rhombusSize = model.getRhombusSize();
        Dimension2D textSize = model.getRhombusTextSize();
        double rhombusWidth = rhombusSize.getWidth() * scale;
        double rhombusHeight = rhombusSize.getHeight() * scale;
        double textHeight = textSize.getHeight() * scale;
        // Lazy fix
        if (DiagramBlockModel.IS_DEBUG_MODE_ENABLED) {
            if (DiagramBlockModel.DEBUG_DRAW_BORDERS) {
                gc.setStroke(DiagramBlockModel.DEBUG_BORDER_COLOR);
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokeRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale,
                        model.getSize().getWidth() * scale, model.getSize().getHeight() * scale);
            }
        }
        drawPoint = new Pair<>(drawPoint.getKey() + (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());

        gc.setFill(DiagramBlockModel.BD_BACKGROUND_COLOR);
        gc.fillPolygon(new double[]{drawPoint.getKey() * scale,
                        drawPoint.getKey() * scale + rhombusWidth / 2,
                        drawPoint.getKey() * scale + rhombusWidth,
                        drawPoint.getKey() * scale + rhombusWidth / 2},
                new double[]{
                        drawPoint.getValue() * scale + rhombusHeight / 2,
                        drawPoint.getValue() * scale + rhombusHeight,
                        drawPoint.getValue() * scale + rhombusHeight / 2,
                        drawPoint.getValue() * scale}, 4);

        gc.setStroke(DiagramBlockModel.STROKE_COLOR);
        gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
        gc.strokePolygon(new double[]{drawPoint.getKey() * scale,
                        drawPoint.getKey() * scale + rhombusWidth / 2,
                        drawPoint.getKey() * scale + rhombusWidth,
                        drawPoint.getKey() * scale + rhombusWidth / 2},
                new double[]{
                        drawPoint.getValue() * scale + rhombusHeight / 2,
                        drawPoint.getValue() * scale + rhombusHeight,
                        drawPoint.getValue() * scale + rhombusHeight / 2,
                        drawPoint.getValue() * scale}, 4);

        gc.setFont(basicFont);
        //TODO: This is so bad!
        double currentLevel = (rhombusHeight - textHeight) / 2 - 3 * DiagramBlockModel.LINE_SPACING * scale;
        for (String line : this.model.data) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.setFill(DiagramBlockModel.FONT_COLOR);
            gc.fillText(line, (rhombusWidth - d.getWidth()) / 2 + (drawPoint.getKey() * scale), drawPoint.getValue() * scale + currentLevel);
            currentLevel += DiagramBlockModel.LINE_SPACING * scale;
        }

        if (model.positiveBranch == BDDecisionModel.Branch.CENTER || model.negativeBranch == BDDecisionModel.Branch.CENTER) {
            BDContainerController centerBranch;
            boolean isCenterPositive;
            BDContainerController restContainer;
            BDDecisionModel.Branch restBranch;
            if (model.positiveBranch == BDDecisionModel.Branch.CENTER) {
                centerBranch = model.positive;
                isCenterPositive = true;
                restContainer = model.negative;
                restBranch = model.negativeBranch;
            } else {
                centerBranch = model.negative;
                isCenterPositive = false;
                restContainer = model.positive;
                restBranch = model.positiveBranch;
            }
            Pair<Double, Double> bottomRhombusConnector = model.getBottomRhombusConnector(drawPoint);
            Pair<Double, Double> fakeDrawPoint = centerBranch.getModel().getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING));
            Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                    bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING);
            centerBranch.update(gc, trueCenterDrawPoint, scale);
            gc.setStroke(DiagramBlockModel.STROKE_COLOR);
            gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
            selectionOverflow &= !centerBranch.isMouseInElement(trueCenterDrawPoint);
            gc.strokeLine(
                    bottomRhombusConnector.getKey() * scale,
                    bottomRhombusConnector.getValue() * scale,
                    bottomRhombusConnector.getKey() * scale,
                    (bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING) * scale);
            if (isCenterPositive) {
                Dimension2D centerBranchTextSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.POSITIVE_BRANCH_TEXT);
                gc.setFill(DiagramBlockModel.FONT_COLOR);
                gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                        (bottomRhombusConnector.getKey() - centerBranchTextSize.getWidth() / scale - DiagramBlockModel.LINE_SPACING) * scale,
                        (bottomRhombusConnector.getValue() + centerBranchTextSize.getHeight() / scale) * scale);
            } else {
                Dimension2D centerBranchTextSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.NEGATIVE_BRANCH_TEXT);
                gc.setFill(DiagramBlockModel.FONT_COLOR);
                gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                        (bottomRhombusConnector.getKey() - centerBranchTextSize.getWidth() / scale - DiagramBlockModel.LINE_SPACING) * scale,
                        (bottomRhombusConnector.getValue() + centerBranchTextSize.getHeight() / scale) * scale);
            }

            Pair<Double, Double> bottomRestConnector;
            Pair<Double, Double> bottomCenterConnector = centerBranch.getModel().getBottomConnector(trueCenterDrawPoint);

            if (restBranch == BDDecisionModel.Branch.LEFT) {
                double restShoulderLen = centerBranch.getModel().getDistanceToLeftBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        + restContainer.getModel().getSize().getWidth() - rhombusWidth / scale / 2;

                Pair<Double, Double> rhombusLeftConnector = model.getLeftRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusLeftConnector.getKey() - restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (rhombusLeftConnector.getKey() - restConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + restConnector.getKey()),
                            drawRestPoint.getValue());
                    restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                }
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{
                                rhombusLeftConnector.getKey() * scale,
                                restConnector.getKey() * scale,
                                restConnector.getKey() * scale},
                        new double[]{
                                rhombusLeftConnector.getValue() * scale,
                                rhombusLeftConnector.getValue() * scale,
                                restConnector.getValue() * scale},
                        3);
                if (isCenterPositive) {
                    gc.setFill(DiagramBlockModel.FONT_COLOR);
                    gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                            restConnector.getKey() * scale,
                            (rhombusLeftConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * scale);
                } else {
                    gc.setFill(DiagramBlockModel.FONT_COLOR);
                    gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                            restConnector.getKey() * scale,
                            (rhombusLeftConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * scale);

                }
                restContainer.update(gc, drawRestPoint, scale);
                gc.setStroke(DiagramBlockModel.STROKE_COLOR);
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
                selectionOverflow &= !restContainer.isMouseInElement(drawRestPoint);

                bottomRestConnector = restContainer.getModel().getBottomConnector(drawRestPoint);
            } else {
                double restShoulderLen = centerBranch.getModel().getDistanceToRightBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        - rhombusWidth / scale / 2;

                Pair<Double, Double> rhombusRightConnector = model.getRightRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusRightConnector.getKey() + restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (restConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - restConnector.getKey() + rhombusRightConnector.getKey()),
                            drawRestPoint.getValue());
                    restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                }
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{
                                rhombusRightConnector.getKey() * scale,
                                restConnector.getKey() * scale,
                                restConnector.getKey() * scale},
                        new double[]{
                                rhombusRightConnector.getValue() * scale,
                                rhombusRightConnector.getValue() * scale,
                                restConnector.getValue() * scale},
                        3);
                if (isCenterPositive) {
                    Dimension2D restTextSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.NEGATIVE_BRANCH_TEXT);
                    gc.setFill(DiagramBlockModel.FONT_COLOR);
                    gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                            (restConnector.getKey() - restTextSize.getWidth() / scale) * scale,
                            (rhombusRightConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * scale);
                } else {
                    Dimension2D restTextSize = Utils.computeTextWidth(basicFont, DiagramBlockModel.POSITIVE_BRANCH_TEXT);
                    gc.setFill(DiagramBlockModel.FONT_COLOR);
                    gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                            (restConnector.getKey() - restTextSize.getWidth() / scale) * scale,
                            (rhombusRightConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * scale);

                }
                restContainer.update(gc, drawRestPoint, scale);
                gc.setStroke(DiagramBlockModel.STROKE_COLOR);
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
                selectionOverflow &= !restContainer.isMouseInElement(drawRestPoint);

                bottomRestConnector = restContainer.getModel().getBottomConnector(drawRestPoint);
            }

            double totalHeight = rhombusHeight / scale + 2 * DiagramBlockModel.ELEMENTS_SPACING +
                    Math.max(centerBranch.getModel().getSize().getHeight(), restContainer.getModel().getSize().getHeight());


            gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
            gc.strokePolyline(
                    new double[]{
                            bottomRestConnector.getKey() * scale,
                            bottomRestConnector.getKey() * scale,
                            model.getBottomRhombusConnector(drawPoint).getKey() * scale},
                    new double[]{
                            bottomRestConnector.getValue() * scale,
                            (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale,
                            (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale},
                    3);

            gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
            gc.strokePolyline(
                    new double[]{
                            bottomCenterConnector.getKey() * scale,
                            bottomCenterConnector.getKey() * scale,
                            model.getBottomRhombusConnector(drawPoint).getKey() * scale},
                    new double[]{
                            bottomCenterConnector.getValue() * scale,
                            (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale,
                            (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale},
                    3);
        } else {
            Dimension2D negativeSize = model.negative.getModel().getSize();
            Dimension2D positiveSize = model.positive.getModel().getSize();
            Pair<Double, Double> rhombusLeftConnector = model.getLeftRhombusConnector(drawPoint);
            Pair<Double, Double> rhombusRightConnector = model.getRightRhombusConnector(drawPoint);
            double totalHeight = rhombusHeight / scale + 2 * DiagramBlockModel.ELEMENTS_SPACING +
                    Math.max(negativeSize.getHeight(), positiveSize.getHeight());

            if (model.negativeBranch == BDDecisionModel.Branch.LEFT) {
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + negativeSize.getWidth() - rhombusWidth / scale / 2;
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / scale / 2;

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusLeftConnector.getKey() - negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (rhombusLeftConnector.getKey() - negConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + negConnector.getKey()),
                            drawNegativePoint.getValue());
                    negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                }

                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{rhombusLeftConnector.getKey() * scale,
                                negConnector.getKey() * scale,
                                negConnector.getKey() * scale},
                        new double[]{rhombusLeftConnector.getValue() * scale,
                                rhombusLeftConnector.getValue() * scale,
                                negConnector.getValue() * scale},
                        3);
                gc.setFill(DiagramBlockModel.FONT_COLOR);
                gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                        negConnector.getKey() * scale,
                        (rhombusLeftConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * scale);
                model.negative.update(gc, drawNegativePoint, scale);
                gc.setStroke(DiagramBlockModel.STROKE_COLOR);
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
                selectionOverflow &= !model.negative.isMouseInElement(drawNegativePoint);

                Pair<Double, Double> bottomNegConnector = model.negative.getModel().getBottomConnector(drawNegativePoint);

                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{bottomNegConnector.getKey() * scale,
                                bottomNegConnector.getKey() * scale,
                                model.getBottomRhombusConnector(drawPoint).getKey() * scale},
                        new double[]{bottomNegConnector.getValue() * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale},
                        3);

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusRightConnector.getKey() + positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (posConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (posConnector.getKey() - rhombusRightConnector.getKey())),
                            drawPositivePoint.getValue());
                    posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                }

                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{rhombusRightConnector.getKey() * scale,
                                posConnector.getKey() * scale,
                                posConnector.getKey() * scale},
                        new double[]{rhombusRightConnector.getValue() * scale,
                                rhombusRightConnector.getValue() * scale,
                                posConnector.getValue() * scale},
                        3);
                gc.setFill(DiagramBlockModel.FONT_COLOR);
                gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                        (posConnector.getKey() - Utils.computeTextWidth(basicFont, DiagramBlockModel.POSITIVE_BRANCH_TEXT).getWidth() / scale) * scale,
                        (rhombusRightConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * scale);
                model.positive.update(gc, drawPositivePoint, scale);
                gc.setStroke(DiagramBlockModel.STROKE_COLOR);
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
                selectionOverflow &= !model.positive.isMouseInElement(drawPositivePoint);

                Pair<Double, Double> bottomPosConnector = model.positive.getModel().getBottomConnector(drawPositivePoint);
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{bottomPosConnector.getKey() * scale,
                                bottomPosConnector.getKey() * scale,
                                model.getBottomRhombusConnector(drawPoint).getKey() * scale},
                        new double[]{bottomPosConnector.getValue() * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale},
                        3);
            } else {
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + positiveSize.getWidth() - rhombusWidth / scale / 2;
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / scale / 2;

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusLeftConnector.getKey() - positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (rhombusLeftConnector.getKey() - posConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + posConnector.getKey()),
                            drawPositivePoint.getValue());
                    posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                }
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{rhombusLeftConnector.getKey() * scale,
                                posConnector.getKey() * scale,
                                posConnector.getKey() * scale},
                        new double[]{rhombusLeftConnector.getValue() * scale,
                                rhombusLeftConnector.getValue() * scale,
                                posConnector.getValue() * scale},
                        3);
                model.positive.update(gc, drawPositivePoint, scale);
                gc.setStroke(DiagramBlockModel.STROKE_COLOR);
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
                selectionOverflow &= !model.positive.isMouseInElement(drawPositivePoint);
                gc.setFill(DiagramBlockModel.FONT_COLOR);
                gc.fillText(DiagramBlockModel.POSITIVE_BRANCH_TEXT,
                        posConnector.getKey() * scale,
                        (rhombusLeftConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * scale);

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusRightConnector.getKey() + negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (negConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (negConnector.getKey() - rhombusRightConnector.getKey())),
                            drawNegativePoint.getValue());
                    negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                }
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{rhombusRightConnector.getKey() * scale,
                                negConnector.getKey() * scale,
                                negConnector.getKey() * scale},
                        new double[]{rhombusRightConnector.getValue() * scale,
                                rhombusRightConnector.getValue() * scale,
                                negConnector.getValue() * scale},
                        3);
                gc.setFill(DiagramBlockModel.FONT_COLOR);
                gc.fillText(DiagramBlockModel.NEGATIVE_BRANCH_TEXT,
                        (negConnector.getKey() - Utils.computeTextWidth(basicFont, DiagramBlockModel.NEGATIVE_BRANCH_TEXT).getWidth() / scale) * scale,
                        (rhombusRightConnector.getValue() - DiagramBlockModel.TEXT_PADDING) * scale);
                model.negative.update(gc, drawNegativePoint, scale);
                gc.setStroke(DiagramBlockModel.STROKE_COLOR);
                gc.setLineWidth(DiagramBlockModel.STROKE_WIDTH_DEFAULT * scale);
                selectionOverflow &= !model.negative.isMouseInElement(drawNegativePoint);

                Pair<Double, Double> bottomNegConnector = model.negative.getModel().getBottomConnector(drawNegativePoint);
                Pair<Double, Double> bottomPosConnector = model.positive.getModel().getBottomConnector(drawPositivePoint);
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{bottomNegConnector.getKey() * scale,
                                bottomNegConnector.getKey() * scale,
                                model.getBottomRhombusConnector(drawPoint).getKey() * scale},
                        new double[]{bottomNegConnector.getValue() * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale},
                        3);

                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokePolyline(
                        new double[]{bottomPosConnector.getKey() * scale,
                                bottomPosConnector.getKey() * scale,
                                model.getBottomRhombusConnector(drawPoint).getKey() * scale},
                        new double[]{bottomPosConnector.getValue() * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale},
                        3);
            }
        }


        if (selected) {
            drawPoint = new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());
            drawSelectBorder(gc, drawPoint, model.getSize(), scale);
        } else if (selectionOverflow) {
            drawPoint = new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());
            drawOverflowBorder(gc, drawPoint, model.getSize(), scale);
        }

    }
}
