package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
    public void repaint(GraphicsContext gc, Pair<Double, Double> drawPoint, double scale) {
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
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * scale);

        for (BDElementController element : model.elements) {
            Pair<Double, Double> drawElementPoint = new Pair<>(
                    topConnector.getKey() - element.getModel().getDistanceToLeftBound(),
                    currentLevel);
            element.update(drawElementPoint);
            if (bottomConnector != null) {
                Pair<Double, Double> topElementConnector = element.getModel().getTopConnector(drawElementPoint);
                gc.strokeLine((bottomConnector.getKey()) * scale,
                        (bottomConnector.getValue()) * scale,
                        (topElementConnector.getKey()) * scale,
                        (topElementConnector.getValue()) * scale);
            }
            bottomConnector = element.getModel().getBottomConnector(drawElementPoint);
            currentLevel += element.getModel().getSize().getHeight() + DiagramBlockModel.ELEMENTS_SPACING;
        }
    }
}
