package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.views.bdelements.BDDecisionView;

public class BDDecisionController extends BDElementController {
    public DiagramBlockController context;
    public BDDecisionModel model;
    public BDDecisionView view;
    public BDDecisionController(DiagramBlockController context, BDContainerController positive, BDDecisionModel.Branch positiveBranch,
                                BDContainerController negative, BDDecisionModel.Branch negativeBranch,
                                String content) {
        this.context = context;
        this.model = new BDDecisionModel(positive, positiveBranch, negative, negativeBranch, content);
        this.view = new BDDecisionView(this.model);
        recalculateSizes();
    }

    @Override
    public void update(Pair<Double, Double> position) {
        view.repaint(context.canvas.getGraphicsContext2D(), position, context.model.canvasScale);
    }

    @Override
    public void recalculateSizes() {
        double maxLineLen = 0;
        Font basicFont = new Font(DiagramBlockModel.FONT_BASIC_NAME, DiagramBlockModel.FONT_BASIC_SIZE);
        double textHeight = this.model.data.size() == 0 ? Utils.computeTextWidth(basicFont, "").getHeight() : 0;
        for (String line : this.model.data) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            if (d.getWidth() > maxLineLen) {
                maxLineLen = d.getWidth();
            }
            textHeight += d.getHeight() + DiagramBlockModel.LINE_SPACING;
        }
        textHeight -= DiagramBlockModel.LINE_SPACING;
        double textWidth = maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING;
        double diag = textHeight + textWidth / 2;

        Dimension2D rhombusSize = new Dimension2D(diag * 2, diag);
        Dimension2D rhombusTextSize = new Dimension2D(textWidth, textHeight);
        this.model.rhombusSize = rhombusSize;
        this.model.rhombusTextSize = rhombusTextSize;
        double rhombusWidth = rhombusSize.getWidth();
        Pair<Double, Double> drawPoint = new Pair<>(0.0, 0.0);
        double leftPoint;
        double leftBound;
        double rightPoint;
        double rightBound;
        Pair<Double, Double> topConnector = model.getTopRhombusConnector(drawPoint);

        if (model.positiveBranch == BDDecisionModel.Branch.CENTER || model.negativeBranch == BDDecisionModel.Branch.CENTER) {
            BDContainerController centerBranch;
            BDContainerController restContainer;
            BDDecisionModel.Branch restBranch;
            if (model.positiveBranch == BDDecisionModel.Branch.CENTER) {
                centerBranch = model.positive;
                restContainer = model.negative;
                restBranch = model.negativeBranch;
            } else {
                centerBranch = model.negative;
                restContainer = model.positive;
                restBranch = model.positiveBranch;
            }
            Pair<Double, Double> bottomRhombusConnector = model.getBottomRhombusConnector(drawPoint);
            Pair<Double, Double> fakeDrawPoint = centerBranch.getModel().getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING));
            Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                    bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING);
            leftPoint = Math.min(trueCenterDrawPoint.getKey(), model.getLeftRhombusConnector(drawPoint).getKey());
            rightPoint = Math.max(trueCenterDrawPoint.getKey() + centerBranch.getModel().getSize().getWidth(),
                    model.getRightRhombusConnector(drawPoint).getKey());

            if (restBranch == BDDecisionModel.Branch.LEFT) {
                double restShoulderLen = centerBranch.getModel().getDistanceToLeftBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        + restContainer.getModel().getSize().getWidth() - rhombusWidth / 2;

                Pair<Double, Double> rhombusLeftConnector = model.getLeftRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusLeftConnector.getKey() - restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (rhombusLeftConnector.getKey() - restConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + restConnector.getKey()),
                            drawRestPoint.getValue());
                }

                leftPoint = drawRestPoint.getKey();
            } else {
                double restShoulderLen = centerBranch.getModel().getDistanceToRightBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        - rhombusWidth / 2;

                Pair<Double, Double> rhombusRightConnector = model.getRightRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusRightConnector.getKey() + restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (restConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - restConnector.getKey() + rhombusRightConnector.getKey()),
                            drawRestPoint.getValue());
                }

                rightPoint = drawRestPoint.getKey() + restContainer.getModel().getSize().getWidth();
            }

            // Left bound
            leftBound = Math.abs(topConnector.getKey() - leftPoint);
            // Right bound
            rightBound = Math.abs(topConnector.getKey() - rightPoint);
        } else {
            Dimension2D negativeSize = model.negative.getModel().getSize();
            Dimension2D positiveSize = model.positive.getModel().getSize();
            Pair<Double, Double> rhombusLeftConnector = model.getLeftRhombusConnector(drawPoint);
            Pair<Double, Double> rhombusRightConnector = model.getRightRhombusConnector(drawPoint);

            if (model.negativeBranch == BDDecisionModel.Branch.LEFT) {
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + negativeSize.getWidth() - rhombusWidth / 2;
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / 2;

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusLeftConnector.getKey() - negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (rhombusLeftConnector.getKey() - negConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + negConnector.getKey()),
                            drawNegativePoint.getValue());
                }
                leftPoint = drawNegativePoint.getKey();

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusRightConnector.getKey() + positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (posConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (posConnector.getKey() - rhombusRightConnector.getKey())),
                            drawPositivePoint.getValue());
                }
                rightPoint = drawPositivePoint.getKey() + model.positive.getModel().getSize().getWidth();
                // Left bound
                leftBound = Math.abs(topConnector.getKey() - drawNegativePoint.getKey());
                // Right bound
                rightBound = Math.abs(topConnector.getKey() - (drawPositivePoint.getKey() + model.positive.getModel().getSize().getWidth()));
            } else {
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + positiveSize.getWidth() - rhombusWidth / 2;
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / 2;

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusLeftConnector.getKey() - positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (rhombusLeftConnector.getKey() - posConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + posConnector.getKey()),
                            drawPositivePoint.getValue());
                }
                leftPoint = drawPositivePoint.getKey();

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusRightConnector.getKey() + negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (negConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (negConnector.getKey() - rhombusRightConnector.getKey())),
                            drawNegativePoint.getValue());
                }
                rightPoint = drawNegativePoint.getKey() + model.negative.getModel().getSize().getWidth();
                // Left bound
                leftBound = Math.abs(topConnector.getKey() - drawPositivePoint.getKey());
                // Right bound
                rightBound = Math.abs(topConnector.getKey() - (drawNegativePoint.getKey() + model.negative.getModel().getSize().getWidth()));
            }
        }
        Dimension2D size = new Dimension2D(Math.abs(rightPoint - leftPoint),
                rhombusSize.getHeight() + 2 * DiagramBlockModel.ELEMENTS_SPACING + Math.max(model.negative.getModel().getSize().getHeight(), model.positive.getModel().getSize().getHeight()));
        model.setMeasurements(size, leftBound, rightBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }
}
