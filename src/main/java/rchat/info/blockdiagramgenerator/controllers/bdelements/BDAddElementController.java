package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDAddElementModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.bdelements.BDAddElementView;

import java.util.Collections;
import java.util.List;

public class BDAddElementController extends BDElementController {

    public BDAddElementModel model;
    public BDAddElementView view;

    public BDAddElementController(DiagramBlockController context) {
        super(context, "");

        this.model = new BDAddElementModel();
        this.view = new BDAddElementView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDAddElementController(DiagramBlockController context, boolean selected) {
        super(context, "");

        this.model = new BDAddElementModel();
        this.view = new BDAddElementView(this.model);
        this.selected = selected;
        this.setControls();
        recalculateSizes();
    }

    @Override
    public JSONObject exportToJSON() {
        return null;
    }

    @Override
    public void update(AbstractPainter gc, Pair<Double, Double> position, double scale) {
        view.repaint(gc, position, context.model.basicFont, isMouseInElement(position), selected, context.isViewportMode(),
                context.isDragMode(), scale, context.getCurrentStyle());
    }

    // You can't remove BDAddElement! Why would you do that?
    @Override
    public void remove() {
    }

    @Override
    public void replace(BDElementController replacer) {
        if (parentContainer != null) {
            replacer.setParentContainer(parentContainer);
            parentContainer.replaceInContainer(this, replacer);
        }
    }

    @Override
    public void recalculateSizes() {
        double maxLineLen = 0;
        Font basicFont = new Font(context.getCurrentStyle().getFontBasicName(), context.getCurrentStyle().getFontBasicSize());
        List<String> dataLines = getModel().getDataLines();
        double textHeight = dataLines.size() == 0 ? Utils.computeTextWidth(basicFont, "").getHeight() : 0;
        for (String line : dataLines) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            if (d.getWidth() > maxLineLen) {
                maxLineLen = d.getWidth();
            }
            textHeight += d.getHeight() + context.getCurrentStyle().getLineSpacing();
        }
        textHeight += 2 * context.getCurrentStyle().getTextPadding();
        textHeight -= context.getCurrentStyle().getLineSpacing();
        Dimension2D size = new Dimension2D(Math.max(maxLineLen + 2 * context.getCurrentStyle().getTextPadding(), textHeight * 2),
                textHeight);
        double leftBound = size.getWidth() / 2;
        model.setMeasurements(size, leftBound, leftBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }

    @Override
    public BDAddElementController clone() {
        return new BDAddElementController(context, selected);
    }

    @Override
    public BDElementController clone(DiagramBlockController newContext) {
        return new BDAddElementController(newContext, selected);
    }

    @Override
    public void setControls() {
        this.controllings = Collections.emptyList();
    }

    @Override
    public BDElementController select(Pair<Double, Double> position) {
        if (isMouseInElement(position)) {
            return this;
        }
        return null;
    }
}
