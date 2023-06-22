package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.scene.Node;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;

import java.util.ArrayList;
import java.util.List;

public abstract class BDElementController implements History.Cloneable<BDElementController> {
    public DiagramBlockController context;
    protected String exportIdentifier;
    public boolean selected  = false;
    public Container parentContainer = null;
    protected List<Node> controllings = new ArrayList<>();

    public static BDElementController fromString(DiagramBlockController context, String str) {
        if (str.equals(BDTerminatorController.EXPORT_IDENTIFIER)) return new BDTerminatorController(context, "");
        if (str.equals(BDProcessController.EXPORT_IDENTIFIER)) return new BDProcessController(context, "");
        if (str.equals(BDDataController.EXPORT_IDENTIFIER)) return new BDDataController(context, "");
        if (str.equals(BDDecisionController.EXPORT_IDENTIFIER)) return new BDDecisionController(context, new BDContainerController(context), BDDecisionModel.Branch.LEFT,
                new BDContainerController(context), BDDecisionModel.Branch.RIGHT, "");
        if (str.equals(BDCycleFixedController.EXPORT_IDENTIFIER)) return new BDCycleFixedController(context, new BDContainerController(context), "");
        if (str.equals(BDPreProcessController.EXPORT_IDENTIFIER)) return new BDPreProcessController(context, "");
        if (str.equals(BDCycleNotFixedController.EXPORT_IDENTIFIER)) return new BDCycleNotFixedController(context, new BDContainerController(context), "");
        return null;
    }

    public BDElementController(DiagramBlockController context, String exportIdentifier) {
        this.exportIdentifier = exportIdentifier;
        this.context = context;
    }

    public BDElementController(DiagramBlockController context, JSONObject object) {
        this.exportIdentifier = object.getString("type");
        this.context = context;
    }

    public static BDElementController fromJSONObject(DiagramBlockController context, JSONObject jsonObject) {
        String str = jsonObject.getString("type");
        if (str.equals(BDTerminatorController.EXPORT_IDENTIFIER)) return new BDTerminatorController(context, jsonObject);
        if (str.equals(BDProcessController.EXPORT_IDENTIFIER)) return new BDProcessController(context, jsonObject);
        if (str.equals(BDDataController.EXPORT_IDENTIFIER)) return new BDDataController(context, jsonObject);
        if (str.equals(BDDecisionController.EXPORT_IDENTIFIER)) return new BDDecisionController(context, jsonObject);
        if (str.equals(BDCycleFixedController.EXPORT_IDENTIFIER)) return new BDCycleFixedController(context, jsonObject);
        if (str.equals(BDPreProcessController.EXPORT_IDENTIFIER)) return new BDPreProcessController(context, jsonObject);
        if (str.equals(BDCycleNotFixedController.EXPORT_IDENTIFIER)) return new BDCycleNotFixedController(context, jsonObject);
        if (str.equals(BDContainerController.EXPORT_IDENTIFIER)) return new BDContainerController(context, jsonObject);
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
