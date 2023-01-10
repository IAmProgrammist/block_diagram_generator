package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDPreProcessModel;
import rchat.info.blockdiagramgenerator.views.bdelements.BDPreProcessView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.onDataUpdate;

public class BDPreProcessController extends BDElementController implements TextEditable {
    public BDPreProcessModel model;
    public BDPreProcessView view;
    private String content;
    public BDPreProcessController(String content) {
        this.model = new BDPreProcessModel(content);
        this.view = new BDPreProcessView(this.model);
        this.content = content;
        this.setControls();
        recalculateSizes();
    }

    public BDPreProcessController(String content, boolean selected) {
        this.model = new BDPreProcessModel(content);
        this.view = new BDPreProcessView(this.model);
        this.content = content;
        this.selected = selected;
        this.setControls();
        recalculateSizes();
    }
    @Override
    public void setControls() {
        Text header = new Text(Main.rb.getString("text"));
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
    public void update(GraphicsContext gc, Pair<Double, Double> position, double scale) {
        view.repaint(gc, position, isMouseInElement(position), selected, scale);
    }

    @Override
    public void recalculateSizes() {
        double maxLineLen = 0;
        Font basicFont = new Font(DiagramBlockModel.FONT_BASIC_NAME, DiagramBlockModel.FONT_BASIC_SIZE);
        double textHeight = this.model.data.size() == 0 ? Utils.computeTextWidth(basicFont, "").getHeight() : 0;
        for (String line : this.model.data) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            if (d.getWidth() > maxLineLen) {
                maxLineLen = d.getWidth();
            }
            textHeight += d.getHeight() + DiagramBlockModel.LINE_SPACING;
        }
        textHeight += 2 * DiagramBlockModel.TEXT_PADDING;
        textHeight -= DiagramBlockModel.LINE_SPACING;
        Dimension2D size = new Dimension2D(Math.max(maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING + 0.3 * textHeight, textHeight * 2),
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
        return new BDPreProcessController(content, this.selected);
    }

    @Override
    public String getText() {
        return content;
    }

    @Override
    public void setText(String text) {
        this.model.data = Arrays.asList(text.split("\n"));
        this.content = text;
        recalculateSizes();
    }
}
