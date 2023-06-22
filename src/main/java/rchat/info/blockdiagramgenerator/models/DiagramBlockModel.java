package rchat.info.blockdiagramgenerator.models;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDElementController;

public class DiagramBlockModel implements History.Cloneable<DiagramBlockModel> {
    public static double DEFAULT_PPI = 72;
    // Множитель изменения размера canvas, чем он больше, тем быстрее уменьшается/увеличивается
    // размер canvas
    public static double CANVAS_RESCALE_FACTOR = 1.003;
    // Позиция мышки в canvas (x)
    public static double mousePosX = 0;
    // Позиция мышки в canvas (y)
    public static double mousePosY = 0;
    public static double canvasWidth = 600;
    public static double canvasHeight = 400;
    public static Font basicFont;
    public static boolean VIEWPORT_MODE = true;
    public static boolean dragMode = false;
    public double startX = 0;
    public double startY = 0;
    public double posX = 0;
    public double posY = 0;

    public BDContainerController root;
    public BDElementController selected;
    public double canvasScale = 1.0;
    public static double canvasMousePosX = 0;
    public static double canvasMousePosY = 0;
    public static Runnable onDataUpdate;

    public DiagramBlockModel(double canvasScale) {
        this.canvasScale = canvasScale;
    }

    public DiagramBlockModel(double startX, double startY, double posX, double posY, BDContainerController root, double canvasScale) {
        this.startX = startX;
        this.startY = startY;
        this.posX = posX;
        this.posY = posY;
        this.root = root;
        this.selected = root.getSelected();
        this.canvasScale = canvasScale;
    }

    public static DiagramBlockModel initDefault() {
        return new DiagramBlockModel(1.0);
    }

    @Override
    public DiagramBlockModel clone() {
        return new DiagramBlockModel(startX, startY, posX,
                posY, root.clone(), canvasScale);
    }
}
