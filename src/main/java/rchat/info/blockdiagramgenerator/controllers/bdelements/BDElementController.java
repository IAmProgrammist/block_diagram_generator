package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BDElementController implements History.Cloneable<BDElementController> {
    protected String exportIdentifier;
    public boolean selected  = false;
    public Container parentContainer = null;
    protected List<Node> controllings = new ArrayList<>();

    public static BDElementController fromString(String str) {
        if (str.equals(BDTerminatorController.EXPORT_IDENTIFIER)) return new BDTerminatorController("");
        if (str.equals(BDProcessController.EXPORT_IDENTIFIER)) return new BDProcessController("");
        if (str.equals(BDDataController.EXPORT_IDENTIFIER)) return new BDDataController("");
        if (str.equals(BDDecisionController.EXPORT_IDENTIFIER)) return new BDDecisionController(new BDContainerController(), BDDecisionModel.Branch.LEFT,
                new BDContainerController(), BDDecisionModel.Branch.RIGHT, "");
        if (str.equals(BDCycleFixedController.EXPORT_IDENTIFIER)) return new BDCycleFixedController(new BDContainerController(), "");
        if (str.equals(BDPreProcessController.EXPORT_IDENTIFIER)) return new BDPreProcessController("");
        if (str.equals(BDCycleNotFixedController.EXPORT_IDENTIFIER)) return new BDCycleNotFixedController(new BDContainerController(), "");
        return null;
    }

    public BDElementController(String exportIdentifier) {
        this.exportIdentifier = exportIdentifier;
    }

    public BDElementController(JSONObject object) {
        this.exportIdentifier = object.getString("type");
    }

    public static BDElementController fromJSONObject(JSONObject jsonObject) {
        String str = jsonObject.getString("type");
        if (str.equals(BDTerminatorController.EXPORT_IDENTIFIER)) return new BDTerminatorController(jsonObject);
        if (str.equals(BDProcessController.EXPORT_IDENTIFIER)) return new BDProcessController(jsonObject);
        if (str.equals(BDDataController.EXPORT_IDENTIFIER)) return new BDDataController(jsonObject);
        if (str.equals(BDDecisionController.EXPORT_IDENTIFIER)) return new BDDecisionController(jsonObject);
        if (str.equals(BDCycleFixedController.EXPORT_IDENTIFIER)) return new BDCycleFixedController(jsonObject);
        if (str.equals(BDPreProcessController.EXPORT_IDENTIFIER)) return new BDPreProcessController(jsonObject);
        if (str.equals(BDCycleNotFixedController.EXPORT_IDENTIFIER)) return new BDCycleNotFixedController(jsonObject);
        if (str.equals(BDContainerController.EXPORT_IDENTIFIER)) return new BDContainerController(jsonObject);
        return null;
    }

    public JSONObject exportToJSON() {
        JSONObject object = new JSONObject();

        object.put("type", this.exportIdentifier);

        return object;
    }

    public void setParentContainer(Container container) {
        this.parentContainer = container;
    }

    public abstract void update(AbstractPainter gc, Pair<Double, Double> position,
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
