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
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDProcessModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.bdelements.BDProcessView;

import java.util.ArrayList;
import java.util.List;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.onDataUpdate;

public class BDProcessController extends BDElementController implements TextEditable {
    public static String EXPORT_IDENTIFIER = "bd_element_process";
    public BDProcessModel model;
    public BDProcessView view;

    public BDProcessController(DiagramBlockController context, String content) {
        super(context, EXPORT_IDENTIFIER);

        this.model = new BDProcessModel(content);
        this.view = new BDProcessView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDProcessController(DiagramBlockController context, JSONObject object) {
        super(context, object);

        this.model = new BDProcessModel(object.getString("data"));
        this.view = new BDProcessView(this.model);
        this.setControls();
        recalculateSizes();
    }

    @Override
    public JSONObject exportToJSON() {
        JSONObject base = super.exportToJSON();

        base.put("data", this.model.data);

        return base;
    }

    public BDProcessController(DiagramBlockController context, String content, boolean selected) {
        super(context, EXPORT_IDENTIFIER);

        this.model = new BDProcessModel(content);
        this.view = new BDProcessView(this.model);
        this.selected = selected;
        this.setControls();
        recalculateSizes();
    }

    @Override
    public void setControls() {
        this.controllings = new ArrayList<>();
        Label header = new Label(Main.rb.getString("text"));
        controllings.add(header);
        TextArea area = new TextArea(getText());
        area.textProperty().addListener((observable, oldValue, newValue) -> {
            setText(newValue);
            if (DiagramBlockModel.onDataUpdate != null) onDataUpdate.run();
        });
        controllings.add(area);
        controllings.add(new Separator());
    }

    @Override
    public void update(AbstractPainter gc, Pair<Double, Double> position, double scale) {
        view.repaint(gc, position, isMouseInElement(position), selected, scale, context.getCurrentStyle());
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
    public BDElementController clone() {
        return new BDProcessController(context, getModel().data, this.selected);
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
            if (replacer instanceof BDProcessController) return;
            replacer.setParentContainer(parentContainer);
            replacer.getModel().data = getModel().data;
            replacer.setControls();
            parentContainer.replaceInContainer(this, replacer);
        }
    }
}
