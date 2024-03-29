package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleFixedModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleNotFixedModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.bdelements.BDCycleNotFixedView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BDCycleNotFixedController extends BDElementController implements TextEditable, Container {
    public static String EXPORT_IDENTIFIER = "bd_element_cyclenotfixed";
    public BDCycleNotFixedModel model;
    public BDCycleNotFixedView view;

    public BDCycleNotFixedController(DiagramBlockController context, BDContainerController body, String content) {
        super(context, EXPORT_IDENTIFIER);

        this.model = new BDCycleNotFixedModel(content, body);
        this.view = new BDCycleNotFixedView(this.model);
        this.model.body.setParentContainer(this);
        this.setControls();
        recalculateSizes();
    }

    public BDCycleNotFixedController(DiagramBlockController context, BDContainerController body, String content, boolean selected) {
        super(context, EXPORT_IDENTIFIER);

        this.model = new BDCycleNotFixedModel(content, body);
        this.view = new BDCycleNotFixedView(this.model);
        this.model.body.setParentContainer(this);
        this.selected = selected;
        this.setControls();
        recalculateSizes();
    }

    public BDCycleNotFixedController(DiagramBlockController context, JSONObject object) {
        super(context, object);
        JSONObject data = object.getJSONObject("data");

        BDContainerController body = new BDContainerController(context, data.getJSONObject("body"));

        this.model = new BDCycleNotFixedModel(data.getString("data"), body);
        this.model.body.setParentContainer(this);
        this.view = new BDCycleNotFixedView(this.model);
        this.setControls();
        recalculateSizes();
    }

    @Override
    public JSONObject exportToJSON() {
        JSONObject base = super.exportToJSON();

        JSONObject branches = new JSONObject();
        branches.put("body", this.model.body.exportToJSON());
        branches.put("data", this.model.data);

        base.put("data", branches);

        return base;
    }

    @Override
    public void update(AbstractPainter gc, Pair<Double, Double> position, double scale) {
        view.repaint(gc, position, context.model.basicFont, isMouseInElement(position), selected, context.isViewportMode(),
                context.isDragMode(), scale, context.getCurrentStyle());
    }

    @Override
    public BDElementController select(Pair<Double, Double> drawPoint) {
        BDElementController selected;
        Dimension2D rhombusSize = model.getRhombusSize();
        double rhombusWidth = rhombusSize.getWidth();

        drawPoint = new Pair<>(drawPoint.getKey() + (model.getDistanceToLeftBound() - rhombusWidth / 2), drawPoint.getValue());

        Pair<Double, Double> bottomRhombusConnector = model.getBottomRhombusConnector(drawPoint);
        Pair<Double, Double> fakeDrawPoint = model.body.getModel().getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + context.getCurrentStyle().getElementsSpacing()));
        Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                bottomRhombusConnector.getValue() + context.getCurrentStyle().getElementsSpacing());
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
        textHeight -= context.getCurrentStyle().getLineSpacing();
        double textWidth = maxLineLen + 2 * context.getCurrentStyle().getTextPadding();
        textHeight += 2 * context.getCurrentStyle().getTextPadding();
        double diag = textHeight + textWidth / 2;

        Dimension2D rhombusSize = new Dimension2D(diag * 2, diag);
        Dimension2D rhombusTextSize = new Dimension2D(textWidth, textHeight);
        this.model.rhombusSize = rhombusSize;
        this.model.rhombusTextSize = rhombusTextSize;
        double rhombusWidth = rhombusSize.getWidth();
        double rhombusHeight = rhombusSize.getHeight();

        double leftLineOffset = Math.max(Math.max(rhombusWidth / 2, model.body.getModel().getDistanceToLeftBound())
                + context.getCurrentStyle().getDecisionBlocksPadding(), context.getCurrentStyle().getMinDecisionShoulderLen());

        double rightLineOffset = Math.max(Math.max(rhombusWidth / 2, model.body.getModel().getDistanceToRightBound())
                + context.getCurrentStyle().getDecisionBlocksPadding(), context.getCurrentStyle().getMinDecisionShoulderLen());
        double bottomPoint = rhombusHeight + 2 * context.getCurrentStyle().getElementsSpacing() + model.body.getModel().getSize().getHeight() + context.getCurrentStyle().getDecisionBlocksPadding();
        Dimension2D size = new Dimension2D(leftLineOffset + rightLineOffset, bottomPoint);
        model.setMeasurements(size, leftLineOffset, rightLineOffset);
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
    public void replace(BDElementController replacer) {
        if (parentContainer != null) {
            if (replacer instanceof BDCycleNotFixedController) return;
            replacer.setParentContainer(parentContainer);
            if (!(replacer instanceof BDDecisionController) && !(replacer instanceof BDCycleFixedController)) {
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
            if (replacer instanceof BDCycleFixedController) {
                replacer.getModel().data = model.data;
                ((BDCycleFixedModel) replacer.getModel()).body = model.body;
            }
            replacer.setControls();
            parentContainer.replaceInContainer(this, replacer);
        }
    }

    @Override
    public BDElementController clone() {
        return new BDCycleNotFixedController(context, model.body.clone(), this.model.data, this.selected);
    }

    @Override
    public BDElementController clone(DiagramBlockController newContext) {
        return new BDCycleNotFixedController(newContext, model.body.clone(newContext), this.model.data, this.selected);
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
            if (context.model.onDataUpdate != null) context.model.onDataUpdate.run();
        });
        controllings.add(new Separator());
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
    public void removeFromContainer(BDElementController bdElementController) {
        if (model.body == bdElementController) {
            model.body = null;
            model.body = new BDContainerController(context);
            recalculateSizes();
        }
    }

    @Override
    public void replaceInContainer(BDElementController replacing, BDElementController replacer) {
        if (model.body == replacing && replacer instanceof BDContainerController) {
            model.body = (BDContainerController) replacer;
            context.model.onDataUpdate.run();
        }
    }
}
