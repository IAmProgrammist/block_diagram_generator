package rchat.info.blockdiagramgenerator.model;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.DiagramBlockController;
import rchat.info.blockdiagramgenerator.DiagramBlockModel;

import java.util.*;

public class BDContainer extends BDElement implements Collection<BDElement> {
    private List<BDElement> elements = new ArrayList<>();

    public BDContainer(DiagramBlockController context) {
        super(context, Collections.emptyList());
        recalculateSizes();
    }

    public BDContainer(DiagramBlockController context, BDElement... elements) {
        super(context, Collections.emptyList());
        this.elements.addAll(Arrays.asList(elements));
        recalculateSizes();
    }

    @Override
    public void drawElement(Pair<Double, Double> drawPoint) {
        double currentLevel = drawPoint.getValue();
        Pair<Double, Double> bottomConnector = null;
        Pair<Double, Double> topConnector = getTopConnector(drawPoint);
        if (DiagramBlockModel.IS_DEBUG_MODE_ENABLED) {
            if (DiagramBlockModel.DEBUG_DRAW_BORDERS) {
                GraphicsContext gc = context.canvas.getGraphicsContext2D();
                gc.setStroke(DiagramBlockModel.DEBUG_BORDER_COLOR);
                gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * context.model.canvasScale);
                gc.strokeRect((drawPoint.getKey() + context.model.posX) * context.model.canvasScale, (drawPoint.getValue() + context.model.posY) * context.model.canvasScale,
                        getSize().getWidth() * context.model.canvasScale, getSize().getHeight() * context.model.canvasScale);
            }
        }
        GraphicsContext gc = context.canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(DiagramBlockModel.CONNECTORS_WIDTH * context.model.canvasScale);

        for (BDElement element : elements) {
            Pair<Double, Double> drawElementPoint = new Pair<>(
                    topConnector.getKey() - element.getDistanceToLeftBound(),
                    currentLevel);
            element.drawElement(drawElementPoint);
            if (bottomConnector != null) {
                Pair<Double, Double> topElementConnector = element.getTopConnector(drawElementPoint);
                gc.strokeLine((bottomConnector.getKey() + context.model.posX) * context.model.canvasScale,
                        (bottomConnector.getValue() + context.model.posY) * context.model.canvasScale,
                        (topElementConnector.getKey() + context.model.posX) * context.model.canvasScale,
                        (topElementConnector.getValue() + context.model.posY) * context.model.canvasScale);
            }
            bottomConnector = element.getBottomConnector(drawElementPoint);
            currentLevel += element.getSize().getHeight() + DiagramBlockModel.ELEMENTS_SPACING;
        }
    }

    // TODO: Rework!
    @Override
    public Pair<Double, Double> getLeftConnector(Pair<Double, Double> pos) {
        throw new RuntimeException("Left connector is not available in container!");
    }

    // TODO: Rework as well!
    @Override
    public Pair<Double, Double> getRightConnector(Pair<Double, Double> pos) {
        throw new RuntimeException("Right connector is not available in container!");
    }

    @Override
    public Pair<Double, Double> getTopConnector(Pair<Double, Double> pos) {
        double maxLeftBound = 0;
        for (BDElement element : elements) {
            double currLeftBound;
            if ((currLeftBound = element.getDistanceToLeftBound()) > maxLeftBound) {
                maxLeftBound = currLeftBound;
            }
        }

        return new Pair<>(pos.getKey() + maxLeftBound, pos.getValue());
    }

    @Override
    public Pair<Double, Double> getBottomConnector(Pair<Double, Double> pos) {
        if (elements.size() > 0) {
            double maxLeftBound = 0;
            double height = 0;
            for (BDElement element : elements) {
                Dimension2D elementSize = element.getSize();
                double currLeftBound;
                if ((currLeftBound = element.getDistanceToLeftBound()) > maxLeftBound) {
                    maxLeftBound = currLeftBound;
                }
                height += elementSize.getHeight() + DiagramBlockModel.ELEMENTS_SPACING;
            }

            height -= DiagramBlockModel.ELEMENTS_SPACING;
            return new Pair<>(pos.getKey() + maxLeftBound, pos.getValue() + height);
        }
        return pos;
    }

    @Override
    public void recalculateSizes() {
        if (elements.size() == 0) {
            recalculateSizes(new Dimension2D(0, 0), 0, 0);
            return;
        }
        double maxLeftBound = 0;
        double maxRightBound = 0;
        double height = 0;
        for (BDElement element : elements) {
            Dimension2D elementSize = element.getSize();
            double currLeftBound;
            double currRightBound;
            if ((currLeftBound = element.getDistanceToLeftBound()) > maxLeftBound) {
                maxLeftBound = currLeftBound;
            }
            if ((currRightBound = element.getDistanceToRightBound()) > maxRightBound) {
                maxRightBound = currRightBound;
            }
            height += elementSize.getHeight() + DiagramBlockModel.ELEMENTS_SPACING;
        }

        height -= DiagramBlockModel.ELEMENTS_SPACING;

        Dimension2D size = new Dimension2D(maxLeftBound + maxRightBound, height);
        recalculateSizes(size, maxLeftBound, maxRightBound);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @Override
    public Iterator<BDElement> iterator() {
        return elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return elements.toArray(ts);
    }

    @Override
    public boolean add(BDElement bdElement) {
        boolean res = elements.add(bdElement);
        recalculateSizes();
        return res;
    }

    @Override
    public boolean remove(Object o) {
        boolean res = elements.remove(o);
        recalculateSizes();
        return res;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return elements.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends BDElement> collection) {
        boolean res = elements.addAll(collection);
        recalculateSizes();
        return res;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean res = elements.removeAll(collection);
        recalculateSizes();
        return res;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean res = elements.retainAll(collection);
        recalculateSizes();
        return res;
    }

    @Override
    public void clear() {
        elements.clear();
        recalculateSizes();
    }
}
