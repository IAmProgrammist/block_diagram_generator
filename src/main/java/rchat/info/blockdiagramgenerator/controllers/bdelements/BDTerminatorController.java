package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDTerminatorModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.bdelements.BDTerminatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.onDataUpdate;

public class BDTerminatorController extends BDElementController implements TextEditable {
    public static String EXPORT_IDENTIFIER = "bd_element_terminator";
    public BDTerminatorModel model;
    public BDTerminatorView view;

    public BDTerminatorController(String content) {
        super(EXPORT_IDENTIFIER);

        this.model = new BDTerminatorModel(content);
        this.view = new BDTerminatorView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDTerminatorController(JSONObject object) {
        super(object);

        this.model = new BDTerminatorModel(object.getString("data"));
        this.view = new BDTerminatorView(this.model);
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
            if (DiagramBlockModel.onDataUpdate != null) onDataUpdate.run();
        });
        controllings.add(area);
        controllings.add(new Separator());
    }

    public BDTerminatorController(String content, boolean selected) {
        super(EXPORT_IDENTIFIER);

        this.model = new BDTerminatorModel(content);
        this.view = new BDTerminatorView(this.model);
        this.selected = selected;
        recalculateSizes();
    }

    @Override
    public void update(AbstractPainter gc, Pair<Double, Double> position, double scale) {
        view.repaint(gc, position, isMouseInElement(position), selected, scale);
    }

    @Override
    public void recalculateSizes() {
        double maxLineLen = 0;
        Font basicFont = new Font(DiagramBlockModel.FONT_BASIC_NAME, DiagramBlockModel.FONT_BASIC_SIZE);
        List<String> dataLines = getModel().getDataLines();
        double textHeight = dataLines.size() == 0 ? Utils.computeTextWidth(basicFont, "").getHeight() : 0;
        for (String line : dataLines) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            if (d.getWidth() > maxLineLen) {
                maxLineLen = d.getWidth();
            }
            textHeight += d.getHeight() + DiagramBlockModel.LINE_SPACING;
        }
        textHeight += 2 * DiagramBlockModel.TEXT_PADDING;
        textHeight -= DiagramBlockModel.LINE_SPACING;
        Dimension2D size = new Dimension2D(Math.max(maxLineLen + textHeight, textHeight * 4), textHeight);
        double leftBound = size.getWidth() / 2;
        model.setMeasurements(size, leftBound, leftBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }

    @Override
    public BDElementController clone() {
        return new BDTerminatorController(getModel().data, this.selected);
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
            if (replacer instanceof BDTerminatorController) return;
            replacer.setParentContainer(parentContainer);
            replacer.getModel().data = getModel().data;
            replacer.setControls();
            parentContainer.replaceInContainer(this, replacer);
        }
    }
}
