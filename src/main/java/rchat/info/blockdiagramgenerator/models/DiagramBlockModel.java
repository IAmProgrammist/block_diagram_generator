package rchat.info.blockdiagramgenerator.models;

import javafx.scene.paint.Color;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;

public class DiagramBlockModel {
    public static final String POSITIVE_BRANCH_TEXT = "+";
    public static final String NEGATIVE_BRANCH_TEXT = "-";
    public static double mousePosX = 0;
    public static double mousePosY = 0;
    public static final double CANVAS_RESCALE_FACTOR = 1.003;
    public static final double TILE_SIZE = 10;
    public static final double FONT_BASIC_SIZE = 50;
    public static final double STROKE_WIDTH_DEFAULT = 5;
    public static final double TILE_STROKE_WIDTH_DEFAULT = 1;
    public static final double TILES_IN_TILE = 5;
    public static final String FONT_BASIC_NAME = "Monospace";
    public static final double LINE_SPACING = 3;
    public static final double ELEMENTS_SPACING = 70;
    public static final double CONNECTORS_WIDTH = 3;
    public static final double TEXT_PADDING = 30;
    // FPS limiter is 60. Probably gotta add posibility to change this number
    public static final long FPS_LIMITER = 16;
    public static final double MIN_DECISION_SHOULDER_LEN = 50;
    public static final double DECISION_BLOCKS_PADDING = 50;
    public static final Color DEBUG_BORDER_COLOR = Color.PURPLE;
    public static final boolean IS_DEBUG_MODE_ENABLED = false;
    public static final boolean DEBUG_DRAW_BORDERS = true;
    public static double canvasWidth = 600;
    public static double canvasHeight = 400;
    public double startX = 0;
    public double startY = 0;
    public double posX = 0;
    public double posY = 0;
    public BDContainerController root;
    public double canvasScale;

    public DiagramBlockModel(double canvasScale) {
        this.canvasScale = canvasScale;
    }
    public DiagramBlockModel(DiagramBlockModel cloneObject) {
        this.startX = cloneObject.startX;
        this.startY = cloneObject.startY;
        this.posX = cloneObject.posX;
        this.posY = cloneObject.posY;
        this.canvasScale = cloneObject.canvasScale;
        this.root = cloneObject.root;
    }

    public static DiagramBlockModel initDefault() {
        return new DiagramBlockModel(1.0);
    }
}
