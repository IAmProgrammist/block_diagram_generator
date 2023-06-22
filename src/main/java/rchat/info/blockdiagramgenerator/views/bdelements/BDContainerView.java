package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDElementController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDContainerModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

public class BDContainerView extends BDElementView {
    protected BDContainerModel model;

    public BDContainerView(BDContainerModel model) {
        this.model = model;
    }

    @Override
    public void repaint(AbstractPainter gc, Pair<Double, Double> drawPoint, Font basicFont,
                        boolean selectionOverflow, boolean isViewport, boolean isDragmode, boolean selected, double scale, Style style) {
        repaint(gc, drawPoint, selectionOverflow, selected, isViewport, isDragmode, scale, style, new Pair<>(0.0, 0.0));
    }

    @Override
    public void drawOverflowBorder(AbstractPainter gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale, Style style) {
        gc.setStroke(style.getOverflowSelectionColor());
        gc.setLineWidth(style.getSelectionBorderWidth());
        gc.strokeRect((drawPoint.getKey() - style.getSelectionBorderWidth() - style.getContainerOverflowPadding()) * scale,
                (drawPoint.getValue() - style.getSelectionBorderWidth() - style.getContainerOverflowPadding()) * scale,
                (size.getWidth() + 2 * style.getSelectionBorderWidth() + 2 * style.getContainerOverflowPadding()) * scale,
                (size.getHeight() + 2 * style.getSelectionBorderWidth() + 2 * style.getContainerOverflowPadding()) * scale);
    }

    public void drawSelectBorder(AbstractPainter gc, Pair<Double, Double> drawPoint, Dimension2D size, double scale, Style style) {
        gc.setStroke(style.getSelectedColor());
        gc.setLineWidth(style.getSelectionBorderWidth());
        gc.strokeRect((drawPoint.getKey() - style.getSelectionBorderWidth() - style.getContainerOverflowPadding()) * scale,
                (drawPoint.getValue() - style.getSelectionBorderWidth() - style.getContainerOverflowPadding()) * scale,
                (size.getWidth() + 2 * style.getSelectionBorderWidth() + 2 * style.getContainerOverflowPadding()) * scale,
                (size.getHeight() + 2 * style.getSelectionBorderWidth() + 2 * style.getContainerOverflowPadding()) * scale);
    }

    public void repaint(AbstractPainter gc, Pair<Double, Double> drawPoint,
                        boolean selectionOverflow, boolean selected, boolean isViewport, boolean isDragmode, double scale, Style style, Pair<Double, Double> mousePosition) {
        double currentLevel = drawPoint.getValue();
        Pair<Double, Double> bottomConnector = null;
        Pair<Double, Double> topConnector = model.getTopConnector(drawPoint);
        if (style.isDebugModeEnabled()) {
            if (style.isDebugDrawBorders()) {
                gc.setStroke(style.getDebugBorderColor());
                gc.setLineWidth(style.getConnectorsWidth() * scale);
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
                gc.setStroke(style.getStrokeColor());
                gc.setLineWidth(style.getConnectorsWidth() * scale);
                gc.strokeLine((bottomConnector.getKey()) * scale,
                        (bottomConnector.getValue()) * scale,
                        (topElementConnector.getKey()) * scale,
                        (topElementConnector.getValue()) * scale);
            }
            bottomConnector = element.getModel().getBottomConnector(drawElementPoint);
            currentLevel += element.getModel().getSize().getHeight() + style.getElementsSpacing();
        }
        if (selectionOverflow && isDragmode && !isViewport) {
            for (int i = 0; i < model.elementYBorders.size() - 1; i++) {
                Dimension2D dndCurrentSize = new Dimension2D(model.getSize().getWidth(),
                        model.elementYBorders.get(i + 1).getKey() -
                                model.elementYBorders.get(i).getKey());
                Pair<Double, Double> dndCurrentPos = new Pair<>(drawPoint.getKey(), drawPoint.getValue() + model.elementYBorders.get(i).getKey());
                if (Utils.isPointInBounds(new Pair<>(mousePosition.getKey(), mousePosition.getValue()),
                        dndCurrentPos, dndCurrentSize)) {
                    // I know that this is terrible but i need it!
                    model.lastVisitedDragModePos = i;
                    drawDragNDropForeground(gc,
                            new Pair<>(drawPoint.getKey() + model.getDistanceToLeftBound() -
                                    Math.min(style.getMaxBdContainerDragndropWidth() / 2,
                                            Math.min(model.getDistanceToLeftBound(), model.getDistanceToRightBound())
                                                    - style.getMaxBdContainerDragndropWidthMargin()),
                                    drawPoint.getValue() + model.elementYBorders.get(i).getValue()),
                            new Dimension2D(2 * Math.min(style.getMaxBdContainerDragndropWidth() / 2,
                                    Math.min(model.getDistanceToLeftBound(), model.getDistanceToRightBound())
                                            - style.getMaxBdContainerDragndropWidthMargin()),
                                    style.getElementsSpacing()), scale, style);
                    break;
                }
            }
        }
        if (isViewport) {
            if (selected) {
                drawSelectBorder(gc, drawPoint, model.getSize(), scale, style);
            } else if (selectionOverflow && !isDragmode) {
                drawOverflowBorder(gc, drawPoint, model.getSize(), scale, style);
            }
        }
    }
}
