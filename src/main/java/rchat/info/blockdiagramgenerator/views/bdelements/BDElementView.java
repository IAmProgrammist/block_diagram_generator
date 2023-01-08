package rchat.info.blockdiagramgenerator.views.bdelements;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;

public abstract class BDElementView {
    public abstract void repaint(GraphicsContext gc, Pair<Double, Double> drawPoint, double scale);
}
