package rchat.info.blockdiagramgenerator.models;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDElementController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DiagramBlockModel implements History.Cloneable<DiagramBlockModel> {
    public static double DEFAULT_PPI = 72;
    public static boolean IS_DEBUG_TIKZ_INCLUDE_COMMENTS = false;
    // Текст для положительных веток в циклах и условии
    public static String POSITIVE_BRANCH_TEXT = "+";
    // Текст для отрицательных веток в циклах и условии
    public static String NEGATIVE_BRANCH_TEXT = "-";
    // Длина линии в прерывающейся линии
    public static double DASH_LINE_WIDTH_LINE = 15;
    // Длина пропуска в прерывающейся линии
    public static double DASH_LINE_WIDTH_SPACE = 30;
    // Множитель изменения размера canvas, чем он больше, тем быстрее уменьшается/увеличивается
    // размер canvas
    public static double CANVAS_RESCALE_FACTOR = 1.003;
    // Ширина/высота плитки во viewport в пикселях при canvasScale = 1.0
    public static double TILE_SIZE = 10;
    // Размер текста при canvasScale = 1.0
    public static double FONT_BASIC_SIZE = 50;
    // Ширина границы блок схем при canvasScale = 1.0
    public static double STROKE_WIDTH_DEFAULT = 5;
    // Ширина плиток сетки во viewport в пикселях
    public static double TILE_STROKE_WIDTH_DEFAULT = 1;
    // Ширина границы выбранного элемента
    public static double SELECTION_BORDER_WIDTH = 3;
    // Количество плиток для объединения их в одну плитку
    public static double TILES_IN_TILE = 10;
    // Название шрифта
    public static String FONT_BASIC_NAME = "Calibri";
    // Расстояние между линиями в многострочном тексте
    public static double LINE_SPACING = 3;
    // Расстояние между элементами блок схем
    public static double ELEMENTS_SPACING = 70;
    // Ширина соединителей элементов блок схем
    public static double CONNECTORS_WIDTH = 3;
    // Отступ текста
    public static double TEXT_PADDING = 30;
    // Минимальное время которое должно пройти для следующей отрисовки canvas с прошлой отрисовки в миллисекундах
    public static long FPS_LIMITER = 16;
    // Минимальная длина плеча в циклах, решениях
    public static double MIN_DECISION_SHOULDER_LEN = 50;
    //
    public static double DECISION_BLOCKS_PADDING = 50;
    public static Color DEBUG_BORDER_COLOR = Color.PURPLE;
    public static Color OVERFLOW_SELECTION_COLOR = Color.DARKSEAGREEN;
    public static Color SELECTED_COLOR = Color.LIGHTGREEN;
    public static Color STROKE_COLOR = Color.BLACK;
    public static Color FONT_COLOR = Color.BLACK;
    public static Color GRID_COLOR = new Color(0.8, 0.8, 0.8, 1);
    public static Color BD_BACKGROUND_COLOR = new Color(1, 1, 1, 1);
    public static Color BACKGROUND_COLOR = new Color(0.9, 0.9, 0.9, 1);
    public static boolean IS_DEBUG_MODE_ENABLED = false;
    public static boolean DEBUG_DRAW_BORDERS = false;
    public static boolean IS_DEBUG_SHOW_FPS = false;
    public static double CONTAINER_OVERFLOW_PADDING = 30;
    public static Color DRAGNDROP_FOREGROUND_COLOR = new Color(0.0, 1, 0.0, 0.3);
    public static double MAX_BD_CONTAINER_DRAGNDROP_WIDTH = 300;
    public static double BD_CONTAINER_DRAGNDROP_WIDTH_MARGIN = 25;
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

    public void loadStyle(File loadFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(loadFile));

        if (properties.contains("IS_DEBUG_TIKZ_INCLUDE_COMMENTS"))
            IS_DEBUG_TIKZ_INCLUDE_COMMENTS = Boolean.parseBoolean(properties.getProperty("IS_DEBUG_TIKZ_INCLUDE_COMMENTS"));

        if (properties.contains("IS_DEBUG_MODE_ENABLED"))
            IS_DEBUG_MODE_ENABLED = Boolean.parseBoolean(properties.getProperty("IS_DEBUG_MODE_ENABLED"));

        if (properties.contains("DEBUG_DRAW_BORDERS"))
            DEBUG_DRAW_BORDERS = Boolean.parseBoolean(properties.getProperty("DEBUG_DRAW_BORDERS"));

        if (properties.contains("IS_DEBUG_SHOW_FPS"))
            IS_DEBUG_SHOW_FPS = Boolean.parseBoolean(properties.getProperty("IS_DEBUG_SHOW_FPS"));

        if (properties.contains("POSITIVE_BRANCH_TEXT"))
            POSITIVE_BRANCH_TEXT = properties.getProperty("POSITIVE_BRANCH_TEXT");

        if (properties.contains("NEGATIVE_BRANCH_TEXT"))
            NEGATIVE_BRANCH_TEXT = properties.getProperty("NEGATIVE_BRANCH_TEXT");

        if (properties.contains("DASH_LINE_WIDTH_LINE"))
            DASH_LINE_WIDTH_LINE = Double.parseDouble(properties.getProperty("DASH_LINE_WIDTH_LINE"));

        if (properties.contains("MAX_BD_CONTAINER_DRAGNDROP_WIDTH"))
            MAX_BD_CONTAINER_DRAGNDROP_WIDTH = Double.parseDouble(properties.getProperty("MAX_BD_CONTAINER_DRAGNDROP_WIDTH"));

        if (properties.contains("DASH_LINE_WIDTH_SPACE"))
            DASH_LINE_WIDTH_SPACE = Double.parseDouble(properties.getProperty("DASH_LINE_WIDTH_SPACE"));

        if (properties.contains("BD_CONTAINER_DRAGNDROP_WIDTH_MARGIN"))
            BD_CONTAINER_DRAGNDROP_WIDTH_MARGIN = Double.parseDouble(properties.getProperty("BD_CONTAINER_DRAGNDROP_WIDTH_MARGIN"));

        if (properties.contains("CONTAINER_OVERFLOW_PADDING"))
            CONTAINER_OVERFLOW_PADDING = Double.parseDouble(properties.getProperty("CONTAINER_OVERFLOW_PADDING"));

        if (properties.contains("CANVAS_RESCALE_FACTOR"))
            CANVAS_RESCALE_FACTOR = Double.parseDouble(properties.getProperty("CANVAS_RESCALE_FACTOR"));

        if (properties.contains("TILE_SIZE"))
            TILE_SIZE = Double.parseDouble(properties.getProperty("TILE_SIZE"));

        if (properties.contains("FONT_BASIC_SIZE"))
            FONT_BASIC_SIZE = Double.parseDouble(properties.getProperty("FONT_BASIC_SIZE"));

        if (properties.contains("STROKE_WIDTH_DEFAULT"))
            STROKE_WIDTH_DEFAULT = Double.parseDouble(properties.getProperty("STROKE_WIDTH_DEFAULT"));

        if (properties.contains("TILE_STROKE_WIDTH_DEFAULT"))
            TILE_STROKE_WIDTH_DEFAULT = Double.parseDouble(properties.getProperty("TILE_STROKE_WIDTH_DEFAULT"));

        if (properties.contains("SELECTION_BORDER_WIDTH"))
            SELECTION_BORDER_WIDTH = Double.parseDouble(properties.getProperty("SELECTION_BORDER_WIDTH"));

        if (properties.contains("TILES_IN_TILE"))
            TILES_IN_TILE = Double.parseDouble(properties.getProperty("TILES_IN_TILE"));

        if (properties.contains("FONT_BASIC_NAME"))
            FONT_BASIC_NAME = properties.getProperty("FONT_BASIC_NAME");

        if (properties.contains("LINE_SPACING"))
            LINE_SPACING = Double.parseDouble(properties.getProperty("LINE_SPACING"));

        if (properties.contains("ELEMENTS_SPACING"))
            ELEMENTS_SPACING = Double.parseDouble(properties.getProperty("ELEMENTS_SPACING"));

        if (properties.contains("CONNECTORS_WIDTH"))
            CONNECTORS_WIDTH = Double.parseDouble(properties.getProperty("CONNECTORS_WIDTH"));

        if (properties.contains("TEXT_PADDING"))
            TEXT_PADDING = Double.parseDouble(properties.getProperty("TEXT_PADDING"));

        if (properties.contains("MIN_DECISION_SHOULDER_LEN"))
            MIN_DECISION_SHOULDER_LEN = Double.parseDouble(properties.getProperty("MIN_DECISION_SHOULDER_LEN"));

        if (properties.contains("DECISION_BLOCKS_PADDING"))
            DECISION_BLOCKS_PADDING = Double.parseDouble(properties.getProperty("DECISION_BLOCKS_PADDING"));

        if (properties.contains("DEBUG_BORDER_COLOR_R") && properties.contains("DEBUG_BORDER_COLOR_G") && properties.contains("DEBUG_BORDER_COLOR_B") && properties.contains("DEBUG_BORDER_COLOR_A"))
            DEBUG_BORDER_COLOR = Color.color(Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("DEBUG_BORDER_COLOR_R")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("DEBUG_BORDER_COLOR_G")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("DEBUG_BORDER_COLOR_B")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("DEBUG_BORDER_COLOR_A")))));

        if (properties.contains("OVERFLOW_SELECTION_COLOR_R") && properties.contains("OVERFLOW_SELECTION_COLOR_G") && properties.contains("OVERFLOW_SELECTION_COLOR_B") && properties.contains("OVERFLOW_SELECTION_COLOR_A"))
            OVERFLOW_SELECTION_COLOR = Color.color(Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("OVERFLOW_SELECTION_COLOR_R")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("OVERFLOW_SELECTION_COLOR_G")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("OVERFLOW_SELECTION_COLOR_B")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("OVERFLOW_SELECTION_COLOR_A")))));

        if (properties.contains("SELECTED_COLOR_R") && properties.contains("SELECTED_COLOR_G") && properties.contains("SELECTED_COLOR_B") && properties.contains("SELECTED_COLOR_A"))
            SELECTED_COLOR = Color.color(Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("SELECTED_COLOR_R")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("SELECTED_COLOR_G")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("SELECTED_COLOR_B")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("SELECTED_COLOR_A")))));

        if (properties.contains("STROKE_COLOR_R") && properties.contains("STROKE_COLOR_G") && properties.contains("STROKE_COLOR_B") && properties.contains("STROKE_COLOR_A"))
            STROKE_COLOR = Color.color(Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("STROKE_COLOR_R")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("STROKE_COLOR_G")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("STROKE_COLOR_B")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("STROKE_COLOR_A")))));

        if (properties.contains("FONT_COLOR_R") && properties.contains("FONT_COLOR_G") && properties.contains("FONT_COLOR_B") && properties.contains("FONT_COLOR_A"))
            FONT_COLOR = Color.color(Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("FONT_COLOR_R")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("FONT_COLOR_G")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("FONT_COLOR_B")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("FONT_COLOR_A")))));

        if (properties.contains("GRID_COLOR_R") && properties.contains("GRID_COLOR_G") && properties.contains("GRID_COLOR_B") && properties.contains("GRID_COLOR_A"))
            GRID_COLOR = Color.color(Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("GRID_COLOR_R")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("GRID_COLOR_G")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("GRID_COLOR_B")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("GRID_COLOR_A")))));

        if (properties.contains("BD_BACKGROUND_COLOR_R") && properties.contains("BD_BACKGROUND_COLOR_G") && properties.contains("BD_BACKGROUND_COLOR_B") && properties.contains("BD_BACKGROUND_COLOR_A"))
            BD_BACKGROUND_COLOR = Color.color(Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("BD_BACKGROUND_COLOR_R")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("BD_BACKGROUND_COLOR_G")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("BD_BACKGROUND_COLOR_B")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("BD_BACKGROUND_COLOR_A")))));

        if (properties.contains("BACKGROUND_COLOR_R") && properties.contains("BACKGROUND_COLOR_G") && properties.contains("BACKGROUND_COLOR_B") && properties.contains("BACKGROUND_COLOR_A"))
            BACKGROUND_COLOR = Color.color(Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("BACKGROUND_COLOR_R")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("BACKGROUND_COLOR_G")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("BACKGROUND_COLOR_B")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("BACKGROUND_COLOR_A")))));

        if (properties.contains("DRAGNDROP_FOREGROUND_COLOR_R") && properties.contains("DRAGNDROP_FOREGROUND_COLOR_G") && properties.contains("DRAGNDROP_FOREGROUND_COLOR_B") && properties.contains("DRAGNDROP_FOREGROUND_COLOR_A"))
            DRAGNDROP_FOREGROUND_COLOR = Color.color(Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("DRAGNDROP_FOREGROUND_COLOR_R")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("DRAGNDROP_FOREGROUND_COLOR_G")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("DRAGNDROP_FOREGROUND_COLOR_B")))),
                    Math.max(0, Math.min(1, Double.parseDouble(properties.getProperty("DRAGNDROP_FOREGROUND_COLOR_A")))));
    }

    @Override
    public DiagramBlockModel clone() {
        return new DiagramBlockModel(startX, startY, posX,
                posY, root.clone(), canvasScale);
    }
}
