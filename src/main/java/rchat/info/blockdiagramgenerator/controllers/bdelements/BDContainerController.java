package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDContainerModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleFixedModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.bdelements.BDContainerView;
import rchat.info.blockdiagramgenerator.views.bdelements.BDCycleFixedView;

import java.util.*;
import java.util.stream.Collectors;

public class BDContainerController extends BDElementController implements Collection<BDElementController>, Container {
    public static String EXPORT_IDENTIFIER = "bd_element_container";
    public BDContainerModel model;
    public BDContainerView view;

    public BDContainerController(DiagramBlockController context) {
        super(context, EXPORT_IDENTIFIER);

        if (context.isViewportMode()) {
            this.model = new BDContainerModel(new BDAddElementController(context));
        } else {
            this.model = new BDContainerModel();
        }
        for (BDElementController element : this.model.elements)
            element.setParentContainer(this);
        this.view = new BDContainerView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDContainerController(DiagramBlockController context, BDElementController... elements) {
        super(context, EXPORT_IDENTIFIER);

        elements = Arrays.stream(elements)
                .filter(el -> !(el instanceof BDAddElementController))
                .collect(Collectors.toList()).toArray(new BDElementController[0]);
        if (elements.length == 0 && context.isViewportMode()) {
            this.model = new BDContainerModel(new BDAddElementController(context));
        } else {
            this.model = new BDContainerModel(elements);
        }
        for (BDElementController element : this.model.elements)
            element.setParentContainer(this);
        this.view = new BDContainerView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDContainerController(DiagramBlockController context, boolean selected, BDElementController... elements) {
        super(context, EXPORT_IDENTIFIER);

        elements = Arrays.stream(elements)
                .filter(el -> !(el instanceof BDAddElementController))
                .collect(Collectors.toList()).toArray(new BDElementController[0]);
        if (elements.length == 0 && context.isViewportMode()) {
            this.model = new BDContainerModel(new BDAddElementController(context));
        } else {
            this.model = new BDContainerModel(elements);
        }
        for (BDElementController element : this.model.elements)
            element.setParentContainer(this);
        this.selected = selected;
        this.view = new BDContainerView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDContainerController(DiagramBlockController context, JSONObject object) {
        super(context, object);
        List<BDElementController> elements = new ArrayList<>();
        JSONArray elementsArray = object.getJSONArray("data");

        for (int i = 0; i < elementsArray.length(); i++)
            elements.add(BDElementController.fromJSONObject(context, elementsArray.getJSONObject(i)));

        BDElementController[] elementsAr = elements.stream()
                .filter(el -> !(el == null || el instanceof BDAddElementController))
                .collect(Collectors.toList()).toArray(new BDElementController[0]);
        if (elementsAr.length == 0 && context.isViewportMode()) {
            this.model = new BDContainerModel(new BDAddElementController(context));
        } else {
            this.model = new BDContainerModel(elementsAr);
        }
        for (BDElementController element : this.model.elements)
            element.setParentContainer(this);
        this.view = new BDContainerView(this.model);
        this.setControls();
        recalculateSizes();
    }

    @Override
    public JSONObject exportToJSON() {
        JSONObject base = super.exportToJSON();

        JSONArray data = new JSONArray();

        for (BDElementController element : this.model.elements) {
            JSONObject obj = element.exportToJSON();
            if (obj != null)
                data.put(obj);
        }

        base.put("data", data);

        return base;
    }

    @Override
    public void update(AbstractPainter gc, Pair<Double, Double> position, double scale) {
        view.repaint(gc, position, isMouseInElement(position), selected, context.isViewportMode(), context.isDragMode(),
                scale, this.context.getCurrentStyle(), new Pair<>(this.context.canvasMousePosX(),
                this.context.canvasMousePosY()));
    }

    // We're doing this because there is may be only one element in container, so hitbox for this only element and
    // container would be the same. So we add paddings to container
    @Override
    public boolean isMouseInElement(Pair<Double, Double> position) {
        Dimension2D fixedSize = model.getSize();
        fixedSize = new Dimension2D(fixedSize.getWidth() + 2 * context.getCurrentStyle().getContainerOverflowPadding(),
                fixedSize.getHeight() + 2 * context.getCurrentStyle().getContainerOverflowPadding());
        Pair<Double, Double> fixedPosition = new Pair<>(position.getKey() - context.getCurrentStyle().getContainerOverflowPadding(),
                position.getValue() - context.getCurrentStyle().getContainerOverflowPadding());
        return Utils.isPointInBounds(
                new Pair<>(context.canvasMousePosX(), context.canvasMousePosY()), fixedPosition,
                fixedSize);
    }

    @Override
    public BDElementController select(Pair<Double, Double> drawPoint) {
        BDElementController selected = null;
        Pair<Double, Double> topConnector = model.getTopConnector(drawPoint);
        double currentLevel = drawPoint.getValue();

        for (BDElementController element : model.elements) {
            Pair<Double, Double> drawElementPoint = new Pair<>(
                    topConnector.getKey() - element.getModel().getDistanceToLeftBound(),
                    currentLevel);
            selected = element.select(drawElementPoint);
            if (selected != null) break;
            currentLevel += element.getModel().getSize().getHeight() + context.getCurrentStyle().getElementsSpacing();
        }

        if (selected != null) {
            this.selected = false;
            return selected;
        }
        if (isMouseInElement(drawPoint)) {
            return this;
        }
        return null;
    }

    @Override
    public BDElementController getSelected() {
        for (BDElementController cont : this.model.elements) {
            if (cont.getSelected() != null) return cont;
        }
        return super.getSelected();
    }

    @Override
    public void replace(BDElementController replacer) {
        if (this.model.lastVisitedDragModePos != null) {
            replacer.setParentContainer(this);
            replacer.setControls();
            this.model.elements.add(this.model.lastVisitedDragModePos, replacer);
            this.model.elements = this.model.elements.stream()
                    .filter(bdElementController -> !(bdElementController instanceof BDAddElementController))
                    .collect(Collectors.toList());
            this.model.lastVisitedDragModePos = null;
            DiagramBlockModel.onDataUpdate.run();
        }
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
        List<Pair<Double, Double>> dragNDropYBounds = new ArrayList<>();
        dragNDropYBounds.add(new Pair<>(height - context.getCurrentStyle().getContainerOverflowPadding(), -context.getCurrentStyle().getElementsSpacing()));
        for (BDElementController element : model.elements) {
            element.recalculateSizes();
            Dimension2D elementSize = element.getModel().getSize();
            double currLeftBound;
            double currRightBound;
            if ((currLeftBound = element.getModel().getDistanceToLeftBound()) > maxLeftBound) {
                maxLeftBound = currLeftBound;
            }
            if ((currRightBound = element.getModel().getDistanceToRightBound()) > maxRightBound) {
                maxRightBound = currRightBound;
            }
            height += elementSize.getHeight() + context.getCurrentStyle().getElementsSpacing();
            dragNDropYBounds.add(new Pair<>(height - context.getCurrentStyle().getElementsSpacing() - elementSize.getHeight() / 2,
                    height - context.getCurrentStyle().getElementsSpacing()));
        }

        height -= context.getCurrentStyle().getElementsSpacing();
        dragNDropYBounds.add(new Pair<>(height + context.getCurrentStyle().getContainerOverflowPadding(),
                height + context.getCurrentStyle().getElementsSpacing()));

        Dimension2D size = new Dimension2D(maxLeftBound + maxRightBound, height);
        model.elementYBorders = dragNDropYBounds;
        model.height = height;
        model.maxLeftBound = maxLeftBound;
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

    @Override
    public BDContainerController clone() {
        BDElementController[] elements = new BDElementController[size()];
        int i = 0;
        for (BDElementController controller : this) {
            elements[i++] = controller.clone();
        }
        return new BDContainerController(context, this.selected, elements);
    }

    @Override
    public BDContainerController clone(DiagramBlockController newContext) {
        BDElementController[] elements = new BDElementController[size()];
        int i = 0;
        for (BDElementController controller : this) {
            elements[i++] = controller.clone(newContext);
        }
        return new BDContainerController(newContext, this.selected, elements);
    }

    @Override
    public void setControls() {
        this.controllings = Collections.emptyList();
    }

    @Override
    public void removeFromContainer(BDElementController bdElementController) {
        model.elements.remove(bdElementController);
        if (model.elements.size() == 0 && context.isViewportMode()) {
            model.elements.add(new BDAddElementController(context));
            model.elements.get(0).setParentContainer(this);
        }
    }

    @Override
    public void replaceInContainer(BDElementController replacing, BDElementController replacer) {
        model.elements.set(model.elements.indexOf(replacing), replacer);
        DiagramBlockModel.onDataUpdate.run();
    }
}
