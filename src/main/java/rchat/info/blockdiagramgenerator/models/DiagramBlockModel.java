package rchat.info.blockdiagramgenerator.models;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDElementController;

public class DiagramBlockModel implements History.Cloneable<DiagramBlockModel> {
    public static double DEFAULT_PPI = 72;
    // Множитель изменения размера canvas, чем он больше, тем быстрее уменьшается/увеличивается
    // размер canvas
    public static double CANVAS_RESCALE_FACTOR = 1.003;
    public Font basicFont = Font.getDefault();
    public double posX = 0;
    public double posY = 0;

    public BDContainerController root;
    public BDElementController selected;
    public double canvasScale = 1.0;
    public Runnable onDataUpdate;

    public DiagramBlockModel() {

    }

    public DiagramBlockModel(double posX, double posY, BDContainerController root, double canvasScale, Runnable onDataUpdate) {
        this.posX = posX;
        this.posY = posY;
        this.root = root;
        this.selected = root.getSelected();
        this.canvasScale = canvasScale;
        this.onDataUpdate = onDataUpdate;
    }

    public DiagramBlockModel(BDContainerController root) {
        this.root = root;
        this.selected = root.getSelected();
    }

    public static DiagramBlockModel initDefault() {
        return new DiagramBlockModel();
    }

    @Override
    public DiagramBlockModel clone() {
        return new DiagramBlockModel(posX,
                posY, root.clone(), canvasScale, onDataUpdate);
    }

    @Override
    public DiagramBlockModel clone(DiagramBlockController newContext) {
        return new DiagramBlockModel(posX,
                posY, root.clone(newContext), canvasScale, onDataUpdate);
    }
}
