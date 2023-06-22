package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleNotFixedModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.basicFont;

public class BDCycleNotFixedView extends BDElementView {

    protected BDCycleNotFixedModel model;

    public BDCycleNotFixedView(BDCycleNotFixedModel model) {
        this.model = model;
    }

    @Override
    public void repaint(AbstractPainter gc, Pair<Double, Double> drawPoint,
                        boolean selectionOverflow, boolean selected, double scale, Style style) {
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
                gc.strokeRect((drawPoint.getKey()) * scale, (drawPoint.getValue()) * scale,
                        model.getSize().getWidth() * scale, model.getSize().getHeight() * scale);
            }
        }
        drawPoint = new Pair<>(drawPoint.getKey() + (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());
        gc.setFill(style.getBdBackgroundColor());
        gc.fillPolygon(new double[]{(drawPoint.getKey()) * scale,
                        (drawPoint.getKey()) * scale + rhombusWidth / 2,
                        (drawPoint.getKey()) * scale + rhombusWidth,
                        (drawPoint.getKey()) * scale + rhombusWidth / 2},
                new double[]{(drawPoint.getValue()) * scale + rhombusHeight / 2,
                        (drawPoint.getValue()) * scale + rhombusHeight,
                        (drawPoint.getValue()) * scale + rhombusHeight / 2,
                        (drawPoint.getValue()) * scale}, 4);
        gc.setStroke(style.getStrokeColor());
        gc.setLineWidth(style.getStrokeWidthDefault() * scale);
        gc.strokePolygon(new double[]{(drawPoint.getKey()) * scale,
                        (drawPoint.getKey()) * scale + rhombusWidth / 2,
                        (drawPoint.getKey()) * scale + rhombusWidth,
                        (drawPoint.getKey()) * scale + rhombusWidth / 2},
                new double[]{(drawPoint.getValue()) * scale + rhombusHeight / 2,
                        (drawPoint.getValue()) * scale + rhombusHeight,
                        (drawPoint.getValue()) * scale + rhombusHeight / 2,
                        (drawPoint.getValue()) * scale}, 4);

        gc.setFont(basicFont);
        //TODO: This is so bad!
        double currentLevel = (rhombusHeight - textHeight) / 2 - 3 * style.getLineSpacing() * scale;
        for (String line : this.model.getDataLines()) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            currentLevel += d.getHeight();
            gc.setFill(style.getFontColor());
            gc.fillText(line, (rhombusWidth - d.getWidth()) / 2 + ((drawPoint.getKey()) * scale), (drawPoint.getValue()) * scale + currentLevel);
            currentLevel += style.getLineSpacing() * scale;
        }


        Pair<Double, Double> bottomRhombusConnector = model.getBottomRhombusConnector(drawPoint);
        Pair<Double, Double> topRhombusConnector = model.getTopRhombusConnector(drawPoint);
        Pair<Double, Double> rightRhombusConnector = model.getRightRhombusConnector(drawPoint);
        Pair<Double, Double> fakeDrawPoint = model.body.getModel().getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + style.getElementsSpacing()));
        Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                bottomRhombusConnector.getValue() + style.getElementsSpacing());
        model.body.update(gc, trueCenterDrawPoint, scale);
        gc.setStroke(style.getStrokeColor());
        selectionOverflow &= !model.body.isMouseInElement(trueCenterDrawPoint);
        gc.setLineWidth(style.getConnectorsWidth() * scale);
        gc.strokeLine((bottomRhombusConnector.getKey()) * scale,
                (bottomRhombusConnector.getValue()) * scale,
                (bottomRhombusConnector.getKey()) * scale,
                (bottomRhombusConnector.getValue() + style.getElementsSpacing()) * scale);
        Dimension2D centerBranchTextSize = Utils.computeTextWidth(basicFont, style.getPositiveBranchText());
        gc.setFill(style.getFontColor());
        gc.fillText(style.getPositiveBranchText(),
                (bottomRhombusConnector.getKey() - centerBranchTextSize.getWidth() / scale - style.getLineSpacing()) * scale,
                (bottomRhombusConnector.getValue() + centerBranchTextSize.getHeight() / scale) * scale);

        double leftLineOffset = Math.max(Math.max(rhombusWidth / scale / 2, model.body.getModel().getDistanceToLeftBound())
                + style.getDecisionBlocksPadding(), style.getMinDecisionShoulderLen());
        Pair<Double, Double> bottomBodyConnector = model.body.getModel().getBottomConnector(trueCenterDrawPoint);

        gc.setStroke(style.getStrokeColor());
        gc.setLineWidth(style.getConnectorsWidth() * scale);
        gc.strokePolyline(new double[]{(bottomBodyConnector.getKey()) * scale,
                        (bottomBodyConnector.getKey()) * scale,
                        (bottomBodyConnector.getKey() - leftLineOffset) * scale,
                        (bottomBodyConnector.getKey() - leftLineOffset) * scale,
                        (bottomBodyConnector.getKey()) * scale},
                new double[]{(bottomBodyConnector.getValue()) * scale,
                        (bottomBodyConnector.getValue() + style.getElementsSpacing()) * scale,
                        (bottomBodyConnector.getValue() + style.getElementsSpacing()) * scale,
                        (topRhombusConnector.getValue() - style.getElementsSpacing() / 2) * scale,
                        (topRhombusConnector.getValue() - style.getElementsSpacing() / 2) * scale}, 5);
        double rightLineOffset = Math.max(Math.max(rhombusWidth / scale / 2, model.body.getModel().getDistanceToRightBound()) - rhombusWidth / scale / 2
                + style.getDecisionBlocksPadding(), style.getMinDecisionShoulderLen());
        double bottomPoint = rhombusHeight / scale / 2 + 2 * style.getElementsSpacing() + model.body.getModel().getSize().getHeight() + style.getDecisionBlocksPadding();

        textSize = Utils.computeTextWidth(basicFont, style.getNegativeBranchText());
        gc.setFill(style.getFontColor());
        gc.fillText(style.getNegativeBranchText(), (rightRhombusConnector.getKey() + rightLineOffset - textSize.getWidth() / scale) * scale,
                (rightRhombusConnector.getValue() - style.getTextPadding()) * scale);

        gc.setLineWidth(style.getConnectorsWidth() * scale);
        gc.strokePolyline(new double[]{
                        rightRhombusConnector.getKey() * scale,
                        (rightRhombusConnector.getKey() + rightLineOffset) * scale,
                        (rightRhombusConnector.getKey() + rightLineOffset) * scale,
                        bottomBodyConnector.getKey() * scale},
                new double[]{
                        rightRhombusConnector.getValue() * scale,
                        rightRhombusConnector.getValue() * scale,
                        (rightRhombusConnector.getValue() + bottomPoint) * scale,
                        (rightRhombusConnector.getValue() + bottomPoint) * scale}, 4);

        if (DiagramBlockModel.VIEWPORT_MODE) {
            if (selectionOverflow && DiagramBlockModel.dragMode && Utils.isPointInBounds(new Pair<>(DiagramBlockModel.canvasMousePosX, DiagramBlockModel.canvasMousePosY),
                    drawPoint, model.getRhombusSize())) {
                drawPoint = new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());
                drawDragNDropForeground(gc, drawPoint, model.getSize(), scale, style);
            } else if (selected) {
                drawPoint = new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());
                drawSelectBorder(gc, drawPoint, model.getSize(), scale, style);
            } else if (selectionOverflow && !DiagramBlockModel.dragMode) {
                drawPoint = new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / scale / 2), drawPoint.getValue());
                drawOverflowBorder(gc, drawPoint, model.getSize(), scale, style);
            }
        }
    }
}
