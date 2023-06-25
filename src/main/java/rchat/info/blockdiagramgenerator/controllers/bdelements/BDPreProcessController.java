package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDPreProcessModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.bdelements.BDPreProcessView;

import java.util.ArrayList;
import java.util.List;

public class BDPreProcessController extends BDElementController implements TextEditable {
    public static String EXPORT_IDENTIFIER = "bd_element_preprocess";
    public BDPreProcessModel model;
    public BDPreProcessView view;
    public BDPreProcessController(DiagramBlockController context, String content) {
        super(context, EXPORT_IDENTIFIER);

        this.model = new BDPreProcessModel(content);
        this.view = new BDPreProcessView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDPreProcessController(DiagramBlockController context, String content, boolean selected) {
        super(context, EXPORT_IDENTIFIER);

        this.model = new BDPreProcessModel(content);
        this.view = new BDPreProcessView(this.model);
        this.selected = selected;
        this.setControls();
        recalculateSizes();
    }

    public BDPreProcessController(DiagramBlockController context, JSONObject object) {
        super(context, object);

        this.model = new BDPreProcessModel(object.getString("data"));
        this.view = new BDPreProcessView(this.model);
        this.setControls();
        recalculateSizes();
    }

    @Override
    public JSONObject exportToJSON() {
        JSONObject base = super.exportToJSON();

        base.put("data", this.model.data);

        return base;
    }
    @Override
    public void setControls() {
        this.controllings = new ArrayList<>();
        Label header = new Label(Main.rb.getString("text"));
        controllings.add(header);
        TextArea area = new TextArea(getText());
        area.textProperty().addListener((observable, oldValue, newValue) -> {
            setText(newValue);
            if (context.model.onDataUpdate != null) context.model.onDataUpdate.run();
        });
        controllings.add(area);
        controllings.add(new Separator());
    }

    @Override
    public void update(AbstractPainter gc, Pair<Double, Double> position, double scale) {
        view.repaint(gc, position, context.model.basicFont, isMouseInElement(position), selected, context.isViewportMode(),
                context.isDragMode(), scale, context.getCurrentStyle());
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
        Dimension2D size = new Dimension2D(Math.max(maxLineLen + 2 * context.getCurrentStyle().getTextPadding() + 0.3 * textHeight, textHeight * 2),
                textHeight);
        double leftBound = size.getWidth() / 2;
        model.setMeasurements(size, leftBound, leftBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }

    @Override
    public BDElementController clone() {
        return new BDPreProcessController(context, getModel().data, this.selected);
    }

    @Override
    public BDElementController clone(DiagramBlockController newContext) {
        return new BDPreProcessController(newContext, getModel().data, this.selected);
    }

    @Override
    public String getText() {
        return getModel().data;
    }

    @Override
    public void setText(String text) {
        this.model.data = text;
        recalculateSizes();
    }

    @Override
    public void replace(BDElementController replacer) {
        if (parentContainer != null) {
            if (replacer instanceof BDPreProcessController) return;
            replacer.setParentContainer(parentContainer);
            replacer.getModel().data = getModel().data;
            replacer.setControls();
            parentContainer.replaceInContainer(this, replacer);
        }
    }
}
