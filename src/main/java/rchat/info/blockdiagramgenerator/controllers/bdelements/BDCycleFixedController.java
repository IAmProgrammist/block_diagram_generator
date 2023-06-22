package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleFixedModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleNotFixedModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDDecisionModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.bdelements.BDCycleFixedView;
import rchat.info.blockdiagramgenerator.views.bdelements.BDCycleNotFixedView;

import java.util.*;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.onDataUpdate;

public class BDCycleFixedController extends BDElementController implements TextEditable, Container {
    public static String EXPORT_IDENTIFIER = "bd_element_modifier";
    public BDCycleFixedModel model;
    public BDCycleFixedView view;

    public BDCycleFixedController(DiagramBlockController context, BDContainerController body, String content) {
        super(context, EXPORT_IDENTIFIER);

        this.model = new BDCycleFixedModel(content, body);
        this.model.body.setParentContainer(this);
        this.view = new BDCycleFixedView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDCycleFixedController(DiagramBlockController context, BDContainerController body, String content, boolean selected) {
        super(context, EXPORT_IDENTIFIER);

        this.model = new BDCycleFixedModel(content, body);
        this.model.body.setParentContainer(this);
        this.view = new BDCycleFixedView(this.model);
        this.selected = selected;
        this.setControls();
        recalculateSizes();
    }

    public BDCycleFixedController(DiagramBlockController context, JSONObject object) {
        super(context, object);
        JSONObject data = object.getJSONObject("data");

        BDContainerController body = new BDContainerController(context, data.getJSONObject("body"));

        this.model = new BDCycleFixedModel(data.getString("data"), body);
        this.model.body.setParentContainer(this);
        this.view = new BDCycleFixedView(this.model);
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
        double a = Math.max(textHeight, textWidth);

        Dimension2D rhombusSize = new Dimension2D(a * 2, a);
        Dimension2D rhombusTextSize = new Dimension2D(textWidth, textHeight);
        this.model.rhombusSize = rhombusSize;
        this.model.rhombusTextSize = rhombusTextSize;
        double rhombusWidth = rhombusSize.getWidth();
        double rhombusHeight = rhombusSize.getHeight();

        double leftLineOffset = Math.max(Math.max(rhombusWidth / 2, model.body.getModel().getDistanceToLeftBound())
                + context.getCurrentStyle().getDecisionBlocksPadding(), context.getCurrentStyle().getMinDecisionShoulderLen());

        double leftBound = leftLineOffset;
        double rightLineOffset = Math.max(Math.max(rhombusWidth / 2, model.body.getModel().getDistanceToRightBound())
                + context.getCurrentStyle().getDecisionBlocksPadding(), context.getCurrentStyle().getMinDecisionShoulderLen());
        double bottomPoint = rhombusHeight + 2 * context.getCurrentStyle().getElementsSpacing() + model.body.getModel().getSize().getHeight() + context.getCurrentStyle().getDecisionBlocksPadding();
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
        return new BDCycleFixedController(context, model.body.clone(), this.model.data, this.selected);
    }

    @Override
    public BDElementController clone(DiagramBlockController newContext) {
        return new BDCycleFixedController(newContext, model.body.clone(), this.model.data, this.selected);
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
            model.body = new BDContainerController(context);
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
