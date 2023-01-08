package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDContainerModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.views.bdelements.BDContainerView;

import java.util.Collection;
import java.util.Iterator;

public class BDContainerController extends BDElementController implements Collection<BDElementController> {
    public DiagramBlockController context;
    public BDContainerModel model;
    public BDContainerView view;

    public BDContainerController(DiagramBlockController context) {
        this.context = context;
        this.model = new BDContainerModel();
        this.view = new BDContainerView(this.model);
        recalculateSizes();
    }

    public BDContainerController(DiagramBlockController context, BDElementController...elements) {
        this.context = context;
        this.model = new BDContainerModel(elements);
        this.view = new BDContainerView(this.model);
        recalculateSizes();
    }
    @Override
    public void update(Pair<Double, Double> position) {
        view.repaint(context.canvas.getGraphicsContext2D(), position, context.model.canvasScale);
    }

    @Override
    public void recalculateSizes() {
        if (model.elements.size() == 0) {
            model.setMeasurements(new Dimension2D(0, 0), 0, 0);
            return;
        }
        double maxLeftBound = 0;
        double maxRightBound = 0;
        double height = 0;
        for (BDElementController element : model.elements) {
            Dimension2D elementSize = element.getModel().getSize();
            double currLeftBound;
            double currRightBound;
            if ((currLeftBound = element.getModel().getDistanceToLeftBound()) > maxLeftBound) {
                maxLeftBound = currLeftBound;
            }
            if ((currRightBound = element.getModel().getDistanceToRightBound()) > maxRightBound) {
                maxRightBound = currRightBound;
            }
            height += elementSize.getHeight() + DiagramBlockModel.ELEMENTS_SPACING;
        }

        height -= DiagramBlockModel.ELEMENTS_SPACING;

        Dimension2D size = new Dimension2D(maxLeftBound + maxRightBound, height);
        model.setMeasurements(size, maxLeftBound, maxRightBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }

    @Override
    public int size() {
        return model.elements.size();
    }

    @Override
    public boolean isEmpty() {
        return model.elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return model.elements.contains(o);
    }

    @Override
    public Iterator<BDElementController> iterator() {
        return model.elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return model.elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return model.elements.toArray(ts);
    }

    @Override
    public boolean add(BDElementController bdElement) {
        boolean res = model.elements.add(bdElement);
        recalculateSizes();
        return res;
    }

    @Override
    public boolean remove(Object o) {
        boolean res = model.elements.remove(o);
        recalculateSizes();
        return res;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return model.elements.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends BDElementController> collection) {
        boolean res = model.elements.addAll(collection);
        recalculateSizes();
        return res;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean res = model.elements.removeAll(collection);
        recalculateSizes();
        return res;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean res = model.elements.retainAll(collection);
        recalculateSizes();
        return res;
    }

    @Override
    public void clear() {
        model.elements.clear();
        recalculateSizes();
    }
}
