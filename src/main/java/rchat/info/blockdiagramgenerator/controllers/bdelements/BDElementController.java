package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BDElementController implements History.Cloneable<BDElementController> {
    public boolean selected  = false;
    public Container parentContainer = null;
    protected List<Node> controllings = new ArrayList<>();

    public static BDElementController fromString(String str) {
        if (str.equals("bd_element_terminator")) return new BDTerminatorController("");
        if (str.equals("bd_element_process")) return new BDProcessController("");
        if (str.equals("bd_element_data")) return new BDDataController("");
        if (str.equals("bd_element_decision")) return new BDDecisionController(new BDContainerController(), BDDecisionModel.Branch.LEFT,
                new BDContainerController(), BDDecisionModel.Branch.RIGHT, "");
        if (str.equals("bd_element_modifier")) return new BDCycleFixedController(new BDContainerController(), "");
        if (str.equals("bd_element_preprocess")) return new BDPreProcessController("");
        if (str.equals("bd_element_cyclenotfixed")) return new BDCycleNotFixedController(new BDContainerController(), "");
        return null;
    }

    public void setParentContainer(Container container) {
        this.parentContainer = container;
    }

    public abstract void update(GraphicsContext gc, Pair<Double, Double> position,
                                double scale);

    public boolean isMouseInElement(Pair<Double, Double> position) {
        return Utils.isPointInBounds(
                new Pair<>(DiagramBlockModel.canvasMousePosX, DiagramBlockModel.canvasMousePosY), position,
                getModel().getSize());
    }

    public BDElementController select(Pair<Double, Double> position) {
        if (isMouseInElement(position)) {
            this.selected = true;
            return this;
        }

        this.selected = false;
        return null;
    }
    public void remove() {
        if (parentContainer != null)
            parentContainer.removeFromContainer(this);
    }

    public BDElementController getSelected() {
        if (this.selected) return this;
        return null;
    }
    public abstract void replace(BDElementController replacer);

    public abstract void recalculateSizes();

    public abstract BDElementModel getModel();

    public abstract BDElementController clone();
    public abstract void setControls();
    public List<Node> getControls() {
        return this.controllings;
    }
}
