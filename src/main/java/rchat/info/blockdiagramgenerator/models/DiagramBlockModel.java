package rchat.info.blockdiagramgenerator.models;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import rchat.info.blockdiagramgenerator.History;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDElementController;

public class DiagramBlockModel implements History.Cloneable<DiagramBlockModel> {
    public static final double DEFAULT_PPI = 72;
    // Текст для положительных веток в циклах и условии
    public static String POSITIVE_BRANCH_TEXT = "+";
    // Текст для отрицательных веток в циклах и условии
    public static String NEGATIVE_BRANCH_TEXT = "-";
    // Задержка для сохранения положения на рабочем пространстве в истории проекта
    public static long SAVE_STATE_DELAY = 1000;
    // Длина линии в прерывающейся линии
    public static double DASH_LINE_WIDTH_LINE = 15;
    // Длина пропуска в прерывающейся линии
    public static double DASH_LINE_WIDTH_SPACE = 30;
    // TODO: текстовые ресурсы вынести в локализационный файл
    // Текст для элемента блок схемы "Добавить"
    public static String TEXT_ADD_ELEMENT = "Добавить...";
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
    public static String FONT_BASIC_NAME = "Times New Roman";
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
    public static final boolean IS_DEBUG_MODE_ENABLED = true;
    public static final boolean DEBUG_DRAW_BORDERS = false;
    public static final boolean IS_DEBUG_SHOW_FPS = true;
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


    @Override
    public DiagramBlockModel clone() {
        return new DiagramBlockModel(startX, startY, posX,
                posY, root.clone(), canvasScale);
    }
}
