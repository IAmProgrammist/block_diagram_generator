package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDProcessModel;
import rchat.info.blockdiagramgenerator.models.bdelements.BDTerminatorModel;
import rchat.info.blockdiagramgenerator.views.bdelements.BDProcessView;
import rchat.info.blockdiagramgenerator.views.bdelements.BDTerminatorView;

public class BDProcessController extends BDElementController {
    public DiagramBlockController context;
    public BDProcessModel model;
    public BDProcessView view;
    public BDProcessController(DiagramBlockController context, String content) {
        this.context = context;
        this.model = new BDProcessModel(content);
        this.view = new BDProcessView(this.model);
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
        textHeight += 2 * DiagramBlockModel.TEXT_PADDING;
        textHeight -= DiagramBlockModel.LINE_SPACING;
        Dimension2D size = new Dimension2D(Math.max(maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING, textHeight * 2),
                textHeight);
        double leftBound = size.getWidth() / 2;
        model.setMeasurements(size, leftBound, leftBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }
}
