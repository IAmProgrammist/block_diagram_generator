package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDCycleNotFixedModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.views.bdelements.BDCycleNotFixedView;

public class BDCycleNotFixedController extends BDElementController {
    public DiagramBlockController context;
    public BDCycleNotFixedModel model;
    public BDCycleNotFixedView view;
    public BDCycleNotFixedController(DiagramBlockController context, BDContainerController body, String content) {
        this.context = context;
        this.model = new BDCycleNotFixedModel(content, body);
        this.view = new BDCycleNotFixedView(this.model);
        recalculateSizes();
    }

    @Override
    public void update(Pair<Double, Double> position) {
        view.repaint(context.canvas.getGraphicsContext2D(), position, context.model.canvasScale);
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
        textHeight -= DiagramBlockModel.LINE_SPACING;
        double textWidth = maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING;
        double diag = textHeight + textWidth / 2;

        Pair<Double, Double> drawPoint = new Pair<>(0.0, 0.0);
        Dimension2D rhombusSize = new Dimension2D(diag * 2, diag);
        Dimension2D rhombusTextSize = new Dimension2D(textWidth, textHeight);
        this.model.rhombusSize = rhombusSize;
        this.model.rhombusTextSize = rhombusTextSize;
        Pair<Double, Double> topConnector = model.getTopConnector(drawPoint);
        double rhombusWidth = rhombusSize.getWidth();
        double rhombusHeight = rhombusSize.getHeight();

        double leftLineOffset = Math.max(Math.max(rhombusWidth / 2, model.body.getModel().getDistanceToLeftBound())
                + DiagramBlockModel.DECISION_BLOCKS_PADDING, DiagramBlockModel.MIN_DECISION_SHOULDER_LEN);

        double leftBound = Math.abs(topConnector.getKey() - leftLineOffset);
        double rightLineOffset = Math.max(Math.max(rhombusWidth / 2, model.body.getModel().getDistanceToRightBound())
                + DiagramBlockModel.DECISION_BLOCKS_PADDING, DiagramBlockModel.MIN_DECISION_SHOULDER_LEN);
        double bottomPoint = rhombusHeight + 2 * DiagramBlockModel.ELEMENTS_SPACING + model.body.getModel().getSize().getHeight() + DiagramBlockModel.DECISION_BLOCKS_PADDING;
        double rightBound = Math.abs(topConnector.getKey() - rightLineOffset);
        Dimension2D size = new Dimension2D(Math.abs(leftBound + rightBound), bottomPoint);
        model.setMeasurements(size, leftBound, rightBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }
}
