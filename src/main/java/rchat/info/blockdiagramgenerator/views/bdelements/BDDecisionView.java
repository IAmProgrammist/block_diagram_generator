package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

public class BDDecisionView extends BDElementView {
    protected BDDecisionModel model;

    public BDDecisionView(BDDecisionModel model) {
        this.model = model;
    }

    @Override
    public void repaint(AbstractPainter gc, Pair<Double, Double> drawPoint, Font basicFont,
                        boolean selectionOverflow, boolean selected, boolean isViewport, boolean isDragmode, double scale, Style style) {
        Dimension2D rhombusSize = model.getRhombusSize();
        Dimension2D textSize = model.getRhombusTextSize();
        double rhombusWidth = rhombusSize.getWidth() * scale;
        double rhombusHeight = rhombusSize.getHeight() * scale;
        double textHeight = textSize.getHeight() * scale;
        // Lazy fix
        if (style.isDebugModeEnabled()) {
            if (style.isDebugDrawBorders()) {
                gc.setStroke(style.getDebugBorderColor());
                gc.setLineWidth(style.getConnectorsWidth() * scale);
                gc.strokeRect(drawPoint.getKey() * scale, drawPoint.getValue() * scale,
                        model.getSize().getWidth() * scale, model.getSize().getHeight() * scale);
            }
        }
        drawPoint = new Pair<>(drawPoint.getKey() + (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());

        gc.setFill(style.getBdBackgroundColor());
        gc.fillPolygon(new double[]{drawPoint.getKey() * scale,
                        drawPoint.getKey() * scale + rhombusWidth / 2,
                        drawPoint.getKey() * scale + rhombusWidth,
                        drawPoint.getKey() * scale + rhombusWidth / 2},
                new double[]{
                        drawPoint.getValue() * scale + rhombusHeight / 2,
                        drawPoint.getValue() * scale + rhombusHeight,
                        drawPoint.getValue() * scale + rhombusHeight / 2,
                        drawPoint.getValue() * scale}, 4);

        gc.setStroke(style.getStrokeColor());
        gc.setLineWidth(style.getStrokeWidthDefault() * scale);
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
        double currentLevel = (rhombusHeight - textHeight) / 2 - 3 * style.getLineSpacing() * scale;
        for (String line : this.model.getDataLines()) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.setFill(style.getFontColor());
            gc.fillText(line, (rhombusWidth - d.getWidth()) / 2 + (drawPoint.getKey() * scale), drawPoint.getValue() * scale + currentLevel);
            currentLevel += style.getLineSpacing() * scale;
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
            Pair<Double, Double> fakeDrawPoint = centerBranch.getModel().getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + style.getElementsSpacing()));
            Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                    bottomRhombusConnector.getValue() + style.getElementsSpacing());
            centerBranch.update(gc, trueCenterDrawPoint, scale);
            gc.setStroke(style.getStrokeColor());
            gc.setLineWidth(style.getConnectorsWidth() * scale);
            selectionOverflow &= !centerBranch.isMouseInElement(trueCenterDrawPoint);
            gc.strokeLine(
                    bottomRhombusConnector.getKey() * scale,
                    bottomRhombusConnector.getValue() * scale,
                    bottomRhombusConnector.getKey() * scale,
                    (bottomRhombusConnector.getValue() + style.getElementsSpacing()) * scale);
            if (isCenterPositive) {
                Dimension2D centerBranchTextSize = Utils.computeTextWidth(basicFont, style.getPositiveBranchText());
                gc.setFill(style.getFontColor());
                gc.fillText(style.getPositiveBranchText(),
                        (bottomRhombusConnector.getKey() - centerBranchTextSize.getWidth() / scale - style.getLineSpacing()) * scale,
                        (bottomRhombusConnector.getValue() + centerBranchTextSize.getHeight() / scale) * scale);
            } else {
                Dimension2D centerBranchTextSize = Utils.computeTextWidth(basicFont, style.getNegativeBranchText());
                gc.setFill(style.getFontColor());
                gc.fillText(style.getNegativeBranchText(),
                        (bottomRhombusConnector.getKey() - centerBranchTextSize.getWidth() / scale - style.getLineSpacing()) * scale,
                        (bottomRhombusConnector.getValue() + centerBranchTextSize.getHeight() / scale) * scale);
            }

            Pair<Double, Double> bottomRestConnector;
            Pair<Double, Double> bottomCenterConnector = centerBranch.getModel().getBottomConnector(trueCenterDrawPoint);

            if (restBranch == BDDecisionModel.Branch.LEFT) {
                double restShoulderLen = centerBranch.getModel().getDistanceToLeftBound() + style.getDecisionBlocksPadding()
                        + restContainer.getModel().getSize().getWidth() - rhombusWidth / scale / 2;

                Pair<Double, Double> rhombusLeftConnector = model.getLeftRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusLeftConnector.getKey() - restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + style.getElementsSpacing());
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (rhombusLeftConnector.getKey() - restConnector.getKey() < style.getMinDecisionShoulderLen()) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() - (style.getMinDecisionShoulderLen() - rhombusLeftConnector.getKey() + restConnector.getKey()),
                            drawRestPoint.getValue());
                    restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                }
                gc.setLineWidth(style.getConnectorsWidth() * scale);
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
                    gc.setFill(style.getFontColor());
                    gc.fillText(style.getNegativeBranchText(),
                            restConnector.getKey() * scale,
                            (rhombusLeftConnector.getValue() - style.getTextPadding()) * scale);
                } else {
                    gc.setFill(style.getFontColor());
                    gc.fillText(style.getPositiveBranchText(),
                            restConnector.getKey() * scale,
                            (rhombusLeftConnector.getValue() - style.getTextPadding()) * scale);

                }
                restContainer.update(gc, drawRestPoint, scale);
                gc.setStroke(style.getStrokeColor());
                gc.setLineWidth(style.getStrokeWidthDefault() * scale);
                selectionOverflow &= !restContainer.isMouseInElement(drawRestPoint);

                bottomRestConnector = restContainer.getModel().getBottomConnector(drawRestPoint);
            } else {
                double restShoulderLen = centerBranch.getModel().getDistanceToRightBound() + style.getDecisionBlocksPadding()
                        - rhombusWidth / scale / 2;

                Pair<Double, Double> rhombusRightConnector = model.getRightRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusRightConnector.getKey() + restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + style.getElementsSpacing());
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (restConnector.getKey() - rhombusRightConnector.getKey() < style.getMinDecisionShoulderLen()) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() + (style.getMinDecisionShoulderLen() - restConnector.getKey() + rhombusRightConnector.getKey()),
                            drawRestPoint.getValue());
                    restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                }
                gc.setLineWidth(style.getConnectorsWidth() * scale);
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
                    Dimension2D restTextSize = Utils.computeTextWidth(basicFont, style.getNegativeBranchText());
                    gc.setFill(style.getFontColor());
                    gc.fillText(style.getNegativeBranchText(),
                            (restConnector.getKey() - restTextSize.getWidth() / scale) * scale,
                            (rhombusRightConnector.getValue() - style.getTextPadding()) * scale);
                } else {
                    Dimension2D restTextSize = Utils.computeTextWidth(basicFont, style.getPositiveBranchText());
                    gc.setFill(style.getFontColor());
                    gc.fillText(style.getPositiveBranchText(),
                            (restConnector.getKey() - restTextSize.getWidth() / scale) * scale,
                            (rhombusRightConnector.getValue() - style.getTextPadding()) * scale);

                }
                restContainer.update(gc, drawRestPoint, scale);
                gc.setStroke(style.getStrokeColor());
                gc.setLineWidth(style.getStrokeWidthDefault() * scale);
                selectionOverflow &= !restContainer.isMouseInElement(drawRestPoint);

                bottomRestConnector = restContainer.getModel().getBottomConnector(drawRestPoint);
            }

            double totalHeight = rhombusHeight / scale + 2 * style.getElementsSpacing() +
                    Math.max(centerBranch.getModel().getSize().getHeight(), restContainer.getModel().getSize().getHeight());


            gc.setLineWidth(style.getConnectorsWidth() * scale);
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

            gc.setLineWidth(style.getConnectorsWidth() * scale);
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
            double totalHeight = rhombusHeight / scale + 2 * style.getElementsSpacing() +
                    Math.max(negativeSize.getHeight(), positiveSize.getHeight());

            if (model.negativeBranch == BDDecisionModel.Branch.LEFT) {
                double negativeShoulderLen = style.getDecisionBlocksPadding() + negativeSize.getWidth() - rhombusWidth / scale / 2;
                double positiveShoulderLen = style.getDecisionBlocksPadding() - rhombusWidth / scale / 2;

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusLeftConnector.getKey() - negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + style.getElementsSpacing());
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (rhombusLeftConnector.getKey() - negConnector.getKey() < style.getMinDecisionShoulderLen()) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() - (style.getMinDecisionShoulderLen() - rhombusLeftConnector.getKey() + negConnector.getKey()),
                            drawNegativePoint.getValue());
                    negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                }

                gc.setLineWidth(style.getConnectorsWidth() * scale);
                gc.strokePolyline(
                        new double[]{rhombusLeftConnector.getKey() * scale,
                                negConnector.getKey() * scale,
                                negConnector.getKey() * scale},
                        new double[]{rhombusLeftConnector.getValue() * scale,
                                rhombusLeftConnector.getValue() * scale,
                                negConnector.getValue() * scale},
                        3);
                gc.setFill(style.getFontColor());
                gc.fillText(style.getNegativeBranchText(),
                        negConnector.getKey() * scale,
                        (rhombusLeftConnector.getValue() - style.getTextPadding()) * scale);
                model.negative.update(gc, drawNegativePoint, scale);
                gc.setStroke(style.getStrokeColor());
                gc.setLineWidth(style.getStrokeWidthDefault() * scale);
                selectionOverflow &= !model.negative.isMouseInElement(drawNegativePoint);

                Pair<Double, Double> bottomNegConnector = model.negative.getModel().getBottomConnector(drawNegativePoint);

                gc.setLineWidth(style.getConnectorsWidth() * scale);
                gc.strokePolyline(
                        new double[]{bottomNegConnector.getKey() * scale,
                                bottomNegConnector.getKey() * scale,
                                model.getBottomRhombusConnector(drawPoint).getKey() * scale},
                        new double[]{bottomNegConnector.getValue() * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale},
                        3);

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusRightConnector.getKey() + positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + style.getElementsSpacing());
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (posConnector.getKey() - rhombusRightConnector.getKey() < style.getMinDecisionShoulderLen()) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() + (style.getMinDecisionShoulderLen() - (posConnector.getKey() - rhombusRightConnector.getKey())),
                            drawPositivePoint.getValue());
                    posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                }

                gc.setLineWidth(style.getConnectorsWidth() * scale);
                gc.strokePolyline(
                        new double[]{rhombusRightConnector.getKey() * scale,
                                posConnector.getKey() * scale,
                                posConnector.getKey() * scale},
                        new double[]{rhombusRightConnector.getValue() * scale,
                                rhombusRightConnector.getValue() * scale,
                                posConnector.getValue() * scale},
                        3);
                gc.setFill(style.getFontColor());
                gc.fillText(style.getPositiveBranchText(),
                        (posConnector.getKey() - Utils.computeTextWidth(basicFont, style.getPositiveBranchText()).getWidth() / scale) * scale,
                        (rhombusRightConnector.getValue() - style.getTextPadding()) * scale);
                model.positive.update(gc, drawPositivePoint, scale);
                gc.setStroke(style.getStrokeColor());
                gc.setLineWidth(style.getStrokeWidthDefault() * scale);
                selectionOverflow &= !model.positive.isMouseInElement(drawPositivePoint);

                Pair<Double, Double> bottomPosConnector = model.positive.getModel().getBottomConnector(drawPositivePoint);
                gc.setLineWidth(style.getConnectorsWidth() * scale);
                gc.strokePolyline(
                        new double[]{bottomPosConnector.getKey() * scale,
                                bottomPosConnector.getKey() * scale,
                                model.getBottomRhombusConnector(drawPoint).getKey() * scale},
                        new double[]{bottomPosConnector.getValue() * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale},
                        3);
            } else {
                double positiveShoulderLen = style.getDecisionBlocksPadding() + positiveSize.getWidth() - rhombusWidth / scale / 2;
                double negativeShoulderLen = style.getDecisionBlocksPadding() - rhombusWidth / scale / 2;

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusLeftConnector.getKey() - positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + style.getElementsSpacing());
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (rhombusLeftConnector.getKey() - posConnector.getKey() < style.getMinDecisionShoulderLen()) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() - (style.getMinDecisionShoulderLen() - rhombusLeftConnector.getKey() + posConnector.getKey()),
                            drawPositivePoint.getValue());
                    posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                }
                gc.setLineWidth(style.getConnectorsWidth() * scale);
                gc.strokePolyline(
                        new double[]{rhombusLeftConnector.getKey() * scale,
                                posConnector.getKey() * scale,
                                posConnector.getKey() * scale},
                        new double[]{rhombusLeftConnector.getValue() * scale,
                                rhombusLeftConnector.getValue() * scale,
                                posConnector.getValue() * scale},
                        3);
                model.positive.update(gc, drawPositivePoint, scale);
                gc.setStroke(style.getStrokeColor());
                gc.setLineWidth(style.getStrokeWidthDefault() * scale);
                selectionOverflow &= !model.positive.isMouseInElement(drawPositivePoint);
                gc.setFill(style.getFontColor());
                gc.fillText(style.getPositiveBranchText(),
                        posConnector.getKey() * scale,
                        (rhombusLeftConnector.getValue() - style.getTextPadding()) * scale);

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusRightConnector.getKey() + negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + style.getElementsSpacing());
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (negConnector.getKey() - rhombusRightConnector.getKey() < style.getMinDecisionShoulderLen()) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() + (style.getMinDecisionShoulderLen() - (negConnector.getKey() - rhombusRightConnector.getKey())),
                            drawNegativePoint.getValue());
                    negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                }
                gc.setLineWidth(style.getConnectorsWidth() * scale);
                gc.strokePolyline(
                        new double[]{rhombusRightConnector.getKey() * scale,
                                negConnector.getKey() * scale,
                                negConnector.getKey() * scale},
                        new double[]{rhombusRightConnector.getValue() * scale,
                                rhombusRightConnector.getValue() * scale,
                                negConnector.getValue() * scale},
                        3);
                gc.setFill(style.getFontColor());
                gc.fillText(style.getNegativeBranchText(),
                        (negConnector.getKey() - Utils.computeTextWidth(basicFont, style.getNegativeBranchText()).getWidth() / scale) * scale,
                        (rhombusRightConnector.getValue() - style.getTextPadding()) * scale);
                model.negative.update(gc, drawNegativePoint, scale);
                gc.setStroke(style.getStrokeColor());
                gc.setLineWidth(style.getStrokeWidthDefault() * scale);
                selectionOverflow &= !model.negative.isMouseInElement(drawNegativePoint);

                Pair<Double, Double> bottomNegConnector = model.negative.getModel().getBottomConnector(drawNegativePoint);
                Pair<Double, Double> bottomPosConnector = model.positive.getModel().getBottomConnector(drawPositivePoint);
                gc.setLineWidth(style.getConnectorsWidth() * scale);
                gc.strokePolyline(
                        new double[]{bottomNegConnector.getKey() * scale,
                                bottomNegConnector.getKey() * scale,
                                model.getBottomRhombusConnector(drawPoint).getKey() * scale},
                        new double[]{bottomNegConnector.getValue() * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale,
                                (model.getTopRhombusConnector(drawPoint).getValue() + totalHeight) * scale},
                        3);

                gc.setLineWidth(style.getConnectorsWidth() * scale);
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
        if (isViewport) {
            if (selectionOverflow && isDragmode /*&& Utils.isPointInBounds(new Pair<>(DiagramBlockModel.canvasMousePosX, DiagramBlockModel.canvasMousePosY),
                    drawPoint, model.getRhombusSize())*/) {
                drawPoint = new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());
                drawDragNDropForeground(gc, drawPoint, model.getSize(), scale, style);
            } else if (selected) {
                drawPoint = new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());
                drawSelectBorder(gc, drawPoint, model.getSize(), scale, style);
            } else if (selectionOverflow) {
                drawPoint = new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());
                drawOverflowBorder(gc, drawPoint, model.getSize(), scale, style);
            }
        }
    }
}
