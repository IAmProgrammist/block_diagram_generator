package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleFixedModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleNotFixedModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.bdelements.BDCycleFixedView;

import java.util.*;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.onDataUpdate;

public class BDCycleFixedController extends BDElementController implements TextEditable, Container {
    public BDCycleFixedModel model;
    public BDCycleFixedView view;

    public BDCycleFixedController(BDContainerController body, String content) {
        this.model = new BDCycleFixedModel(content, body);
        this.model.body.setParentContainer(this);
        this.view = new BDCycleFixedView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDCycleFixedController(BDContainerController body, String content, boolean selected) {
        this.model = new BDCycleFixedModel(content, body);
        this.model.body.setParentContainer(this);
        this.view = new BDCycleFixedView(this.model);
        this.selected = selected;
        this.setControls();
        recalculateSizes();
    }

    @Override
    public void update(AbstractPainter gc, Pair<Double, Double> position, double scale) {
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
        List<String> dataLines = getModel().getDataLines();
        double textHeight = dataLines.size() == 0 ? Utils.computeTextWidth(basicFont, "").getHeight() : 0;
        for (String line : dataLines) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            if (d.getWidth() > maxLineLen) {
                maxLineLen = d.getWidth();
            }
            textHeight += d.getHeight() + DiagramBlockModel.LINE_SPACING;
        }
        textHeight -= DiagramBlockModel.LINE_SPACING;
        double textWidth = maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING;
        textHeight += 2 * DiagramBlockModel.TEXT_PADDING;
        double a = Math.max(textHeight, textWidth);

        Dimension2D rhombusSize = new Dimension2D(a * 2, a);
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
        Dimension2D size = new Dimension2D(Math.abs(leftBound + rightBound), bottomPoint);
        model.setMeasurements(size, leftBound, rightBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }

    @Override
    public BDElementController clone() {
        return new BDCycleFixedController(model.body.clone(), this.model.data, this.selected);
    }

    @Override
    public void setControls() {
        this.controllings = new ArrayList<>();
        Label header = new Label(Main.rb.getString("text"));

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
    public BDElementController getSelected() {
        if (model.body.getSelected() != null) return model.body;
        return super.getSelected();
    }

    @Override
    public void replace(BDElementController replacer) {
        if (parentContainer != null) {
            if (replacer instanceof BDCycleFixedController) return;
            replacer.setParentContainer(parentContainer);
            if (!(replacer instanceof BDDecisionController) && !(replacer instanceof BDCycleNotFixedController)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, Main.rb.getString("alert_data_narrowing_content"),
                        ButtonType.APPLY, ButtonType.CANCEL);
                alert.setTitle(Main.rb.getString("alert_data_narrowing_title"));
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.APPLY) {
                    replacer.getModel().data = model.data;
                    replacer.setControls();
                    parentContainer.replaceInContainer(this, replacer);
                }

            }
            if (replacer instanceof BDDecisionController) {
                replacer.getModel().data = model.data;
                ((BDDecisionModel) replacer.getModel()).setPositive(model.body);
            }
            if (replacer instanceof BDCycleNotFixedController) {
                replacer.getModel().data = model.data;
                ((BDCycleNotFixedModel) replacer.getModel()).body = model.body;
            }
            replacer.setControls();
            parentContainer.replaceInContainer(this, replacer);
        }
    }

    @Override
    public String getText() {
        return model.data;
    }

    @Override
    public void setText(String text) {
        this.model.data = text;
        recalculateSizes();
    }

    @Override
    public void removeFromContainer(BDElementController bdElementController) {
        if (model.body == bdElementController) {
            model.body = null;
            model.body = new BDContainerController();
        }
    }

    @Override
    public void replaceInContainer(BDElementController replacing, BDElementController replacer) {
        if (model.body == replacing && replacer instanceof BDContainerController) {
            model.body = (BDContainerController) replacer;
            onDataUpdate.run();
        }
    }
}
