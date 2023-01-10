package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDElementController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDContainerModel;

public class BDContainerView extends BDElementView {
    protected BDContainerModel model;
    public BDContainerView(BDContainerModel model) {
        this.model = model;
    }
    @Override
    public void repaint(GraphicsContext gc, Pair<Double, Double> drawPoint,
                        boolean selectionOverflow, boolean selected, double scale) {
        double currentLevel = drawPoint.getValue();
        Pair<Double, Double> bottomConnector = null;
        Pair<Double, Double> topConnector = model.getTopConnector(drawPoint);
        if (DiagramBlockModel.IS_DEBUG_MODE_ENABLED) {
            if (DiagramBlockModel.DEBUG_DRAW_BORDERS) {
                gc.setStroke(DiagramBlockModel.DEBUG_BORDER_COLOR);
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokeRect((drawPoint.getKey()) * scale, (drawPoint.getValue()) * scale,
                        model.getSize().getWidth() * scale, model.getSize().getHeight() * scale);
            }
        }

        for (BDElementController element : model.elements) {
            Pair<Double, Double> drawElementPoint = new Pair<>(
                    topConnector.getKey() - element.getModel().getDistanceToLeftBound(),
                    currentLevel);
            element.update(gc, drawElementPoint, scale);
            selectionOverflow &= !element.isMouseInElement(drawElementPoint);
            if (bottomConnector != null) {
                Pair<Double, Double> topElementConnector = element.getModel().getTopConnector(drawElementPoint);
                gc.setStroke(DiagramBlockModel.STROKE_COLOR);
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);
                gc.strokeLine((bottomConnector.getKey()) * scale,
                        (bottomConnector.getValue()) * scale,
                        (topElementConnector.getKey()) * scale,
                        (topElementConnector.getValue()) * scale);
            }
            bottomConnector = element.getModel().getBottomConnector(drawElementPoint);
            currentLevel += element.getModel().getSize().getHeight() + DiagramBlockModel.ELEMENTS_SPACING;
        }

        if (selected) {
            drawSelectBorder(gc, drawPoint, model.getSize(), scale);
        } else if (selectionOverflow) {
            drawOverflowBorder(gc, drawPoint, model.getSize(), scale);
        }

    }

    @Override
    public void drawOverflowBorder(GraphicsContext gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale) {
        gc.setStroke(DiagramBlockModel.OVERFLOW_SELECTION_COLOR);
        gc.setLineWidth(DiagramBlockModel.SELECTION_BORDER_WIDTH);
        gc.strokeRect((drawPoint.getKey() - DiagramBlockModel.SELECTION_BORDER_WIDTH - DiagramBlockModel.CONTAINER_OVERFLOW_PADDING) * scale,
                (drawPoint.getValue() - DiagramBlockModel.SELECTION_BORDER_WIDTH - DiagramBlockModel.CONTAINER_OVERFLOW_PADDING) * scale,
                (size.getWidth() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH + 2 * DiagramBlockModel.CONTAINER_OVERFLOW_PADDING) * scale,
                (size.getHeight() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH + 2 * DiagramBlockModel.CONTAINER_OVERFLOW_PADDING) * scale);
    }
    public void drawSelectBorder(GraphicsContext gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale) {
        gc.setStroke(DiagramBlockModel.SELECTED_COLOR);
        gc.setLineWidth(DiagramBlockModel.SELECTION_BORDER_WIDTH);
        gc.strokeRect((drawPoint.getKey() - DiagramBlockModel.SELECTION_BORDER_WIDTH - DiagramBlockModel.CONTAINER_OVERFLOW_PADDING) * scale,
                (drawPoint.getValue() - DiagramBlockModel.SELECTION_BORDER_WIDTH - DiagramBlockModel.CONTAINER_OVERFLOW_PADDING) * scale,
                (size.getWidth() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH + 2 * DiagramBlockModel.CONTAINER_OVERFLOW_PADDING) * scale,
                (size.getHeight() + 2 * DiagramBlockModel.SELECTION_BORDER_WIDTH + 2 * DiagramBlockModel.CONTAINER_OVERFLOW_PADDING) * scale);
    }

}
