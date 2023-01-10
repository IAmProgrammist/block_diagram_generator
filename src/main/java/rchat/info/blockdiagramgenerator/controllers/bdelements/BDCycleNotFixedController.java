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
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleNotFixedModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.views.bdelements.BDCycleNotFixedView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.onDataUpdate;

public class BDCycleNotFixedController extends BDElementController implements TextEditable, Container {
    public BDCycleNotFixedModel model;
    public BDCycleNotFixedView view;
    private String content;

    public BDCycleNotFixedController(BDContainerController body, String content) {
        this.model = new BDCycleNotFixedModel(content, body);
        this.view = new BDCycleNotFixedView(this.model);
        this.model.body.setParentContainer(this);
        this.content = content;
        this.setControls();
        recalculateSizes();
    }

    public BDCycleNotFixedController(BDContainerController body, String content, boolean selected) {
        super();
        this.model = new BDCycleNotFixedModel(content, body);
        this.view = new BDCycleNotFixedView(this.model);
        this.model.body.setParentContainer(this);
        this.content = content;
        this.selected = selected;
    }

    @Override
    public void update(GraphicsContext gc, Pair<Double, Double> position, double scale) {
        view.repaint(gc, position, isMouseInElement(position), selected, scale);
    }

    @Override
    public BDElementController select(Pair<Double, Double> drawPoint) {
        BDElementController selected;
        Dimension2D rhombusSize = model.getRhombusSize();
        double rhombusWidth = rhombusSize.getWidth();

        drawPoint = new Pair<>(drawPoint.getKey() + (model.getDistanceToLeftBound() - rhombusWidth / 2), drawPoint.getValue());

        Pair<Double, Double> bottomRhombusConnector = model.getBottomRhombusConnector(drawPoint);
        Pair<Double, Double> fakeDrawPoint = model.body.getModel().getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING));
        Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING);
        selected = model.body.select(trueCenterDrawPoint);

        if (selected != null) {
            this.selected = false;
            return selected;
        }

        return super.select(new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / 2), drawPoint.getValue()));
    }

    @Override
    public void recalculateSizes() {
        this.model.body.recalculateSizes();
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
        textHeight -= DiagramBlockModel.LINE_SPACING;
        double textWidth = maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING;
        double diag = textHeight + textWidth / 2;

        Dimension2D rhombusSize = new Dimension2D(diag * 2, diag);
        Dimension2D rhombusTextSize = new Dimension2D(textWidth, textHeight);
        this.model.rhombusSize = rhombusSize;
        this.model.rhombusTextSize = rhombusTextSize;
        double rhombusWidth = rhombusSize.getWidth();
        double rhombusHeight = rhombusSize.getHeight();

        double leftLineOffset = Math.max(Math.max(rhombusWidth / 2, model.body.getModel().getDistanceToLeftBound())
                + DiagramBlockModel.DECISION_BLOCKS_PADDING, DiagramBlockModel.MIN_DECISION_SHOULDER_LEN);

        double leftBound = leftLineOffset;
        double rightLineOffset = Math.max(Math.max(rhombusWidth / 2, model.body.getModel().getDistanceToRightBound())
                + DiagramBlockModel.DECISION_BLOCKS_PADDING, DiagramBlockModel.MIN_DECISION_SHOULDER_LEN);
        double bottomPoint = rhombusHeight + 2 * DiagramBlockModel.ELEMENTS_SPACING + model.body.getModel().getSize().getHeight() + DiagramBlockModel.DECISION_BLOCKS_PADDING;
        double rightBound = rightLineOffset;
        Dimension2D size = new Dimension2D(leftBound + rightBound, bottomPoint);
        model.setMeasurements(size, leftBound, rightBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }

    @Override
    public BDElementController getSelected() {
        if (model.body.getSelected() != null) return model.body;
        return super.getSelected();
    }

    @Override
    public BDElementController clone() {
        return new BDCycleNotFixedController(model.body.clone(), this.content, this.selected);
    }

    @Override
    public void setControls() {
        Text header = new Text(Main.rb.getString("text"));
        controllings.add(header);
        TextArea area = new TextArea(getText());
        controllings.add(area);
        area.textProperty().addListener((observable, oldValue, newValue) -> {
            setText(newValue);
            if (DiagramBlockModel.onDataUpdate != null) onDataUpdate.run();
        });
        controllings.add(new Separator());
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

    @Override
    public void removeFromContainer(BDElementController bdElementController) {
        if (model.body == bdElementController) {
            model.body = null;
            model.body = new BDContainerController();
            recalculateSizes();
        }
    }
}
