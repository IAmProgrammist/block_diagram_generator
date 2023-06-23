package rchat.info.blockdiagramgenerator.models;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.scene.paint.Color;

public final class Style {

    public static void init() {
        DEFAULT_STYLE_NAME = "Default";
        DEFAULT_STYLE = new Style(DEFAULT_STYLE_NAME,
                "Calibri", Color.BLACK, Color.BLACK, Color.WHITE, new Color(0.9, 0.9, 0.9, 1), 50, 5,
                3, 30, 3, 70, 50, 50, 15,
                30, "+", "-", new Color(0.8, 0.8, 0.8, 1),
                Color.LIGHTGREEN, Color.DARKSEAGREEN, new Color(0.0, 1, 0.0, 0.3), 10, 1,
                3, 30, 300, 25,
                10, false, false, false, false,
                Color.PURPLE
        );

        saveStyle(DEFAULT_STYLE_NAME, DEFAULT_STYLE);

        Preferences prefs = Preferences.userNodeForPackage(Style.class);
        String styleName;
        if ((styleName = prefs.get("current", null)) == null) {
            prefs.put("current", DEFAULT_STYLE_NAME);
            currentStyle = DEFAULT_STYLE;
        } else
            currentStyle = getStyle(styleName);
    }

    private static Style currentStyle;
    public static String DEFAULT_STYLE_NAME;
    private static Style DEFAULT_STYLE;
    private final String styleName;
    private String positiveBranchText;
    private String negativeBranchText;
    private String fontBasicName;
    private double tileSize;
    private double tileStrokeWidthDefault;
    private double selectionBorderWidth;
    private double containerOverflowPadding;
    private double maxBdContainerDragndropWidth;
    private double maxBdContainerDragndropWidthMargin;
    private double fontBasicSize;
    private double strokeWidthDefault;
    private double connectorsWidth;
    private double textPadding;
    private double lineSpacing;
    private double elementsSpacing;
    private double decisionBlocksPadding;
    private double minDecisionShoulderLen;
    private double dashLineWidthLine;
    private double dashLineWidthSpace;
    private int tilesInTile;
    private boolean isDebugModeEnabled;
    private boolean isDebugShowFps;
    private boolean isDebugTikzIncludeComments;
    private boolean debugDrawBorders;
    private Color debugBorderColor;
    private Color strokeColor;
    private Color fontColor;
    private Color bdBackgroundColor;
    private Color backgroundColor;
    private Color gridColor;
    private Color selectedColor;
    private Color overflowSelectionColor;
    private Color dragndropForegroundColor;

    private Style(String styleName, String fontBasicName, Color strokeColor, Color fontColor, Color bdBackgroundColor, Color backgroundColor, double fontBasicSize, double strokeWidthDefault, double connectorsWidth, double textPadding, double lineSpacing, double elementsSpacing, double decisionBlocksPadding, double minDecisionShoulderLen, double dashLineWidthLine, double dashLineWidthSpace, String positiveBranchText, String negativeBranchText, Color gridColor, Color selectedColor, Color overflowSelectionColor, Color dragndropForegroundColor, double tileSize, double tileStrokeWidthDefault, double selectionBorderWidth, double containerOverflowPadding, double maxBdContainerDragndropWidth, double maxBdContainerDragndropWidthMargin, int tilesInTile, boolean isDebugModeEnabled, boolean isDebugShowFps, boolean isDebugTikzIncludeComments, boolean debugDrawBorders, Color debugBorderColor) {
        this.styleName = styleName;
        this.fontBasicName = fontBasicName;
        this.strokeColor = strokeColor;
        this.fontColor = fontColor;
        this.bdBackgroundColor = bdBackgroundColor;
        this.backgroundColor = backgroundColor;
        this.fontBasicSize = fontBasicSize;
        this.strokeWidthDefault = strokeWidthDefault;
        this.connectorsWidth = connectorsWidth;
        this.textPadding = textPadding;
        this.lineSpacing = lineSpacing;
        this.elementsSpacing = elementsSpacing;
        this.decisionBlocksPadding = decisionBlocksPadding;
        this.minDecisionShoulderLen = minDecisionShoulderLen;
        this.dashLineWidthLine = dashLineWidthLine;
        this.dashLineWidthSpace = dashLineWidthSpace;
        this.positiveBranchText = positiveBranchText;
        this.negativeBranchText = negativeBranchText;
        this.gridColor = gridColor;
        this.selectedColor = selectedColor;
        this.overflowSelectionColor = overflowSelectionColor;
        this.dragndropForegroundColor = dragndropForegroundColor;
        this.tileSize = tileSize;
        this.tileStrokeWidthDefault = tileStrokeWidthDefault;
        this.selectionBorderWidth = selectionBorderWidth;
        this.containerOverflowPadding = containerOverflowPadding;
        this.maxBdContainerDragndropWidth = maxBdContainerDragndropWidth;
        this.maxBdContainerDragndropWidthMargin = maxBdContainerDragndropWidthMargin;
        this.tilesInTile = tilesInTile;
        this.isDebugModeEnabled = isDebugModeEnabled;
        this.isDebugShowFps = isDebugShowFps;
        this.isDebugTikzIncludeComments = isDebugTikzIncludeComments;
        this.debugDrawBorders = debugDrawBorders;
        this.debugBorderColor = debugBorderColor;
    }

    private static void saveStyle(String styleName, Style style) {
        Preferences prefs = Preferences.userNodeForPackage(Style.class);
        Preferences styleNode = prefs.node("styles").node(styleName);

        styleNode.put("styleName", style.styleName);

        styleNode.put("positiveBranchText", style.positiveBranchText);
        styleNode.put("negativeBranchText", style.negativeBranchText);
        styleNode.put("fontBasicName", style.fontBasicName);

        styleNode.putDouble("tileSize", style.tileSize);
        styleNode.putDouble("tileStrokeWidthDefault", style.tileStrokeWidthDefault);
        styleNode.putDouble("selectionBorderWidth", style.selectionBorderWidth);
        styleNode.putDouble("containerOverflowPadding", style.containerOverflowPadding);
        styleNode.putDouble("maxBdContainerDragndropWidth", style.maxBdContainerDragndropWidth);
        styleNode.putDouble("maxBdContainerDragndropWidthMargin", style.maxBdContainerDragndropWidthMargin);
        styleNode.putDouble("fontBasicSize", style.fontBasicSize);
        styleNode.putDouble("strokeWidthDefault", style.strokeWidthDefault);
        styleNode.putDouble("connectorsWidth", style.connectorsWidth);
        styleNode.putDouble("textPadding", style.textPadding);
        styleNode.putDouble("lineSpacing", style.lineSpacing);
        styleNode.putDouble("elementsSpacing", style.elementsSpacing);
        styleNode.putDouble("decisionBlocksPadding", style.decisionBlocksPadding);
        styleNode.putDouble("minDecisionShoulderLen", style.minDecisionShoulderLen);
        styleNode.putDouble("dashLineWidthLine", style.dashLineWidthLine);
        styleNode.putDouble("dashLineWidthSpace", style.dashLineWidthSpace);

        styleNode.putInt("dashLineWidthSpace", style.tilesInTile);

        styleNode.putBoolean("isDebugModeEnabled", style.isDebugModeEnabled);
        styleNode.putBoolean("isDebugShowFps", style.isDebugShowFps);
        styleNode.putBoolean("isDebugTikzIncludeComments", style.isDebugTikzIncludeComments);
        styleNode.putBoolean("debugDrawBorders", style.debugDrawBorders);

        styleNode.putBoolean("isDebugModeEnabled", style.isDebugModeEnabled);
        styleNode.putBoolean("isDebugShowFps", style.isDebugShowFps);
        styleNode.putBoolean("isDebugTikzIncludeComments", style.isDebugTikzIncludeComments);
        styleNode.putBoolean("debugDrawBorders", style.debugDrawBorders);

        putColor(styleNode, "debugBorderColor", style.debugBorderColor);
        putColor(styleNode, "strokeColor", style.strokeColor);
        putColor(styleNode, "fontColor", style.fontColor);
        putColor(styleNode, "bdBackgroundColor", style.bdBackgroundColor);
        putColor(styleNode, "gridColor", style.gridColor);
        putColor(styleNode, "backgroundColor", style.backgroundColor);
        putColor(styleNode, "selectedColor", style.selectedColor);
        putColor(styleNode, "overflowSelectionColor", style.overflowSelectionColor);
        putColor(styleNode, "dragndropForegroundColor", style.dragndropForegroundColor);
    }

    private static Color getColor(Preferences node, String key, Color defaultColor) {
        return new Color(
                Math.min(1, Math.max(0, node.getDouble(key + "R", defaultColor.getRed()))),
                Math.min(1, Math.max(0, node.getDouble(key + "G", defaultColor.getGreen()))),
                Math.min(1, Math.max(0, node.getDouble(key + "B", defaultColor.getBlue()))),
                Math.min(1, Math.max(0, node.getDouble(key + "A", defaultColor.getOpacity()))));
    }

    private static void putColor(Preferences node, String key, Color color) {
        node.putDouble(key + "R", Math.min(1, Math.max(0, color.getRed())));
        node.putDouble(key + "G", Math.min(1, Math.max(0, color.getGreen())));
        node.putDouble(key + "B", Math.min(1, Math.max(0, color.getBlue())));
        node.putDouble(key + "A", Math.min(1, Math.max(0, color.getOpacity())));
    }

    public static Style createStyle(String newStyleName, Style cStyle) {
        if (!hasStyle(newStyleName)) {
            saveStyle(newStyleName, cStyle);
            return getStyle(newStyleName);
        }

        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!hasStyle(newStyleName + " (" + i + ")")) {
                saveStyle(newStyleName + " (" + i + ")", cStyle);
                return getStyle(newStyleName + " (" + i + ")");
            }
        }

        return null;
    }

    public static void removeStyle(String styleName) {
        if (hasStyle(styleName)) {
            try {
                Preferences.userNodeForPackage(Style.class).node("styles").node(styleName).removeNode();
            } catch (BackingStoreException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getStyleName() {
        return styleName;
    }

    public static Style getCurrentStyle() {
        if (Style.currentStyle != null) {
            return Style.currentStyle;
        }

        Preferences prefs = Preferences.userNodeForPackage(Style.class);
        String theme = prefs.get("current", null);
        if (theme == null) {
            saveStyle(DEFAULT_STYLE_NAME, Style.DEFAULT_STYLE);
            prefs.put("current", DEFAULT_STYLE_NAME);
            Style.currentStyle = Style.DEFAULT_STYLE;
            return Style.currentStyle;
        } else {
            Preferences styleList = prefs.node("styles");
            try {
                for (String styleName : styleList.childrenNames()) {
                    if (styleName.equals(theme)) {
                        return getStyle(styleName);
                    }
                }

                saveStyle(DEFAULT_STYLE_NAME, Style.DEFAULT_STYLE);
                prefs.put("current", DEFAULT_STYLE_NAME);
                Style.currentStyle = Style.DEFAULT_STYLE;
                return Style.currentStyle;
            } catch (Exception e) {
                saveStyle(DEFAULT_STYLE_NAME, Style.DEFAULT_STYLE);
                prefs.put("current", DEFAULT_STYLE_NAME);
                Style.currentStyle = Style.DEFAULT_STYLE;
                return Style.currentStyle;
            }
        }
    }

    public static String[] getStyles() {
        try {
            return Preferences.userNodeForPackage(Style.class).node("styles").childrenNames();
        } catch (BackingStoreException e) {
            return new String[]{};
        }
    }

    public static Style getStyle(String styleName) {
        if (hasStyle(styleName)) {
            Preferences style = Preferences.userNodeForPackage(Style.class).node("styles").node(styleName);
            return new Style(styleName, style.get("fontBasicName", DEFAULT_STYLE.fontBasicName),
                    getColor(style, "strokeColor", DEFAULT_STYLE.strokeColor),
                    getColor(style, "fontColor", DEFAULT_STYLE.fontColor),
                    getColor(style, "bdBackgroundColor", DEFAULT_STYLE.bdBackgroundColor),
                    getColor(style, "backgroundColor", DEFAULT_STYLE.backgroundColor),
                    style.getDouble("fontBasicSize", DEFAULT_STYLE.fontBasicSize),
                    style.getDouble("strokeWidthDefault", DEFAULT_STYLE.strokeWidthDefault),
                    style.getDouble("strokeWidthDefault", DEFAULT_STYLE.strokeWidthDefault),
                    style.getDouble("textPadding", DEFAULT_STYLE.textPadding),
                    style.getDouble("lineSpacing", DEFAULT_STYLE.lineSpacing),
                    style.getDouble("elementsSpacing", DEFAULT_STYLE.elementsSpacing),
                    style.getDouble("decisionBlocksPadding", DEFAULT_STYLE.decisionBlocksPadding),
                    style.getDouble("minDecisionShoulderLen", DEFAULT_STYLE.minDecisionShoulderLen),
                    style.getDouble("dashLineWidthLine", DEFAULT_STYLE.dashLineWidthLine),
                    style.getDouble("dashLineWidthSpace", DEFAULT_STYLE.dashLineWidthSpace),
                    style.get("positiveBranchText", DEFAULT_STYLE.positiveBranchText),
                    style.get("negativeBranchText", DEFAULT_STYLE.negativeBranchText),
                    getColor(style, "gridColor", DEFAULT_STYLE.gridColor),
                    getColor(style, "selectedColor", DEFAULT_STYLE.selectedColor),
                    getColor(style, "overflowSelectionColor", DEFAULT_STYLE.overflowSelectionColor),
                    getColor(style, "dragndropForegroundColor", DEFAULT_STYLE.dragndropForegroundColor),
                    style.getDouble("tileSize", DEFAULT_STYLE.tileSize),
                    style.getDouble("tileStrokeWidthDefault", DEFAULT_STYLE.tileStrokeWidthDefault),
                    style.getDouble("selectionBorderWidth", DEFAULT_STYLE.selectionBorderWidth),
                    style.getDouble("containerOverflowPadding", DEFAULT_STYLE.containerOverflowPadding),
                    style.getDouble("maxBdContainerDragndropWidth", DEFAULT_STYLE.maxBdContainerDragndropWidth),
                    style.getDouble("maxBdContainerDragndropWidthMargin", DEFAULT_STYLE.maxBdContainerDragndropWidthMargin),
                    style.getInt("tilesInTile", DEFAULT_STYLE.tilesInTile),
                    style.getBoolean("isDebugModeEnabled", DEFAULT_STYLE.isDebugModeEnabled),
                    style.getBoolean("isDebugShowFps", DEFAULT_STYLE.isDebugShowFps),
                    style.getBoolean("isDebugTikzIncludeComments", DEFAULT_STYLE.isDebugTikzIncludeComments),
                    style.getBoolean("debugDrawBorders", DEFAULT_STYLE.debugDrawBorders),
                    getColor(style, "debugBorderColor", DEFAULT_STYLE.debugBorderColor));


        }

        return null;
    }

    private static boolean hasStyle(String styleName) {
        try {
            return Preferences.userNodeForPackage(Style.class).node("styles").nodeExists(styleName);
        } catch (Exception e) {
            return false;
        }
    }

    private static void update(String style, String key, String value) {
        if (hasStyle(style))
            Preferences.userNodeForPackage(Style.class).node("styles").node(style).put(key, value);
    }

    private static void update(String style, String key, double value) {
        if (hasStyle(style))
            Preferences.userNodeForPackage(Style.class).node("styles").node(style).putDouble(key, value);
    }

    private static void update(String style, String key, int value) {
        if (hasStyle(style))
            Preferences.userNodeForPackage(Style.class).node("styles").node(style).putInt(key, value);
    }

    private static void update(String style, String key, boolean value) {
        if (hasStyle(style))
            Preferences.userNodeForPackage(Style.class).node("styles").node(style).putBoolean(key, value);
    }

    private static void update(String style, String key, Color value) {
        if (hasStyle(style))
            putColor(Preferences.userNodeForPackage(Style.class).node("styles").node(style), key, value);
    }

    public void setPositiveBranchText(String positiveBranchText) {
        update(this.styleName, "positiveBranchText", positiveBranchText);
        this.positiveBranchText = positiveBranchText;
    }

    public void setNegativeBranchText(String negativeBranchText) {
        update(this.styleName, "negativeBranchText", negativeBranchText);
        this.negativeBranchText = negativeBranchText;
    }

    public void setFontBasicName(String fontBasicName) {
        update(this.styleName, "fontBasicName", fontBasicName);
        this.fontBasicName = fontBasicName;
    }

    public void setTileSize(double tileSize) {
        update(this.styleName, "tileSize", tileSize);
        this.tileSize = tileSize;
    }

    public void setTileStrokeWidthDefault(double tileStrokeWidthDefault) {
        update(this.styleName, "tileStrokeWidthDefault", tileStrokeWidthDefault);
        this.tileStrokeWidthDefault = tileStrokeWidthDefault;
    }

    public void setSelectionBorderWidth(double selectionBorderWidth) {
        update(this.styleName, "selectionBorderWidth", selectionBorderWidth);
        this.selectionBorderWidth = selectionBorderWidth;
    }

    public void setContainerOverflowPadding(double containerOverflowPadding) {
        update(this.styleName, "containerOverflowPadding", containerOverflowPadding);
        this.containerOverflowPadding = containerOverflowPadding;
    }

    public void setMaxBdContainerDragndropWidth(double maxBdContainerDragndropWidth) {
        update(this.styleName, "maxBdContainerDragndropWidth", maxBdContainerDragndropWidth);
        this.maxBdContainerDragndropWidth = maxBdContainerDragndropWidth;
    }

    public void setMaxBdContainerDragndropWidthMargin(double maxBdContainerDragndropWidthMargin) {
        update(this.styleName, "maxBdContainerDragndropWidthMargin", maxBdContainerDragndropWidthMargin);
        this.maxBdContainerDragndropWidthMargin = maxBdContainerDragndropWidthMargin;
    }

    public void setFontBasicSize(double fontBasicSize) {
        update(this.styleName, "fontBasicSize", fontBasicSize);
        this.fontBasicSize = fontBasicSize;
    }

    public void setStrokeWidthDefault(double strokeWidthDefault) {
        update(this.styleName, "strokeWidthDefault", strokeWidthDefault);
        this.strokeWidthDefault = strokeWidthDefault;
    }

    public void setConnectorsWidth(double connectorsWidth) {
        update(this.styleName, "connectorsWidth", connectorsWidth);
        this.connectorsWidth = connectorsWidth;
    }

    public void setTextPadding(double textPadding) {
        update(this.styleName, "textPadding", textPadding);
        this.textPadding = textPadding;
    }

    public void setLineSpacing(double lineSpacing) {
        update(this.styleName, "lineSpacing", lineSpacing);
        this.lineSpacing = lineSpacing;
    }

    public void setElementsSpacing(double elementsSpacing) {
        update(this.styleName, "elementsSpacing", elementsSpacing);
        this.elementsSpacing = elementsSpacing;
    }

    public void setDecisionBlocksPadding(double decisionBlocksPadding) {
        update(this.styleName, "decisionBlocksPadding", decisionBlocksPadding);
        this.decisionBlocksPadding = decisionBlocksPadding;
    }

    public void setMinDecisionShoulderLen(double minDecisionShoulderLen) {
        update(this.styleName, "minDecisionShoulderLen", minDecisionShoulderLen);
        this.minDecisionShoulderLen = minDecisionShoulderLen;
    }

    public void setDashLineWidthLine(double dashLineWidthLine) {
        update(this.styleName, "dashLineWidthLine", dashLineWidthLine);
        this.dashLineWidthLine = dashLineWidthLine;
    }

    public void setDashLineWidthSpace(double dashLineWidthSpace) {
        update(this.styleName, "dashLineWidthSpace", dashLineWidthSpace);
        this.dashLineWidthSpace = dashLineWidthSpace;
    }

    public void setTilesInTile(int tilesInTile) {
        update(this.styleName, "tilesInTile", tilesInTile);
        this.tilesInTile = tilesInTile;
    }

    public void setDebugModeEnabled(boolean debugModeEnabled) {
        update(this.styleName, "debugModeEnabled", debugModeEnabled);
        isDebugModeEnabled = debugModeEnabled;
    }

    public void setDebugShowFps(boolean debugShowFps) {
        update(this.styleName, "debugShowFps", debugShowFps);
        isDebugShowFps = debugShowFps;
    }

    public void setDebugTikzIncludeComments(boolean debugTikzIncludeComments) {
        update(this.styleName, "debugTikzIncludeComments", debugTikzIncludeComments);
        isDebugTikzIncludeComments = debugTikzIncludeComments;
    }

    public void setDebugDrawBorders(boolean debugDrawBorders) {
        update(this.styleName, "debugDrawBorders", debugDrawBorders);
        this.debugDrawBorders = debugDrawBorders;
    }

    public void setDebugBorderColor(Color debugBorderColor) {
        update(this.styleName, "debugBorderColor", debugBorderColor);
        this.debugBorderColor = debugBorderColor;
    }

    public void setStrokeColor(Color strokeColor) {
        update(this.styleName, "strokeColor", strokeColor);
        this.strokeColor = strokeColor;
    }

    public void setFontColor(Color fontColor) {
        update(this.styleName, "fontColor", fontColor);
        this.fontColor = fontColor;
    }

    public void setBdBackgroundColor(Color bdBackgroundColor) {
        update(this.styleName, "bdBackgroundColor", bdBackgroundColor);
        this.bdBackgroundColor = bdBackgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        update(this.styleName, "backgroundColor", backgroundColor);
        this.backgroundColor = backgroundColor;
    }

    public void setGridColor(Color gridColor) {
        update(this.styleName, "gridColor", gridColor);
        this.gridColor = gridColor;
    }

    public void setSelectedColor(Color selectedColor) {
        update(this.styleName, "selectedColor", selectedColor);
        this.selectedColor = selectedColor;
    }

    public void setOverflowSelectionColor(Color overflowSelectionColor) {
        update(this.styleName, "overflowSelectionColor", overflowSelectionColor);
        this.overflowSelectionColor = overflowSelectionColor;
    }

    public void setDragndropForegroundColor(Color dragndropForegroundColor) {
        update(this.styleName, "dragndropForegroundColor", dragndropForegroundColor);
        this.dragndropForegroundColor = dragndropForegroundColor;
    }

    public String getPositiveBranchText() {
        return positiveBranchText;
    }

    public String getNegativeBranchText() {
        return negativeBranchText;
    }

    public String getFontBasicName() {
        return fontBasicName;
    }

    public double getTileSize() {
        return tileSize;
    }

    public double getTileStrokeWidthDefault() {
        return tileStrokeWidthDefault;
    }

    public double getSelectionBorderWidth() {
        return selectionBorderWidth;
    }

    public double getContainerOverflowPadding() {
        return containerOverflowPadding;
    }

    public double getMaxBdContainerDragndropWidth() {
        return maxBdContainerDragndropWidth;
    }

    public double getMaxBdContainerDragndropWidthMargin() {
        return maxBdContainerDragndropWidthMargin;
    }

    public double getFontBasicSize() {
        return fontBasicSize;
    }

    public double getStrokeWidthDefault() {
        return strokeWidthDefault;
    }

    public double getConnectorsWidth() {
        return connectorsWidth;
    }

    public double getTextPadding() {
        return textPadding;
    }

    public double getLineSpacing() {
        return lineSpacing;
    }

    public double getElementsSpacing() {
        return elementsSpacing;
    }

    public double getDecisionBlocksPadding() {
        return decisionBlocksPadding;
    }

    public double getMinDecisionShoulderLen() {
        return minDecisionShoulderLen;
    }

    public double getDashLineWidthLine() {
        return dashLineWidthLine;
    }

    public double getDashLineWidthSpace() {
        return dashLineWidthSpace;
    }

    public int getTilesInTile() {
        return tilesInTile;
    }

    public boolean isDebugModeEnabled() {
        return isDebugModeEnabled;
    }

    public boolean isDebugShowFps() {
        return isDebugShowFps;
    }

    public boolean isDebugTikzIncludeComments() {
        return isDebugTikzIncludeComments;
    }

    public boolean isDebugDrawBorders() {
        return debugDrawBorders;
    }

    public Color getDebugBorderColor() {
        return debugBorderColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public Color getBdBackgroundColor() {
        return bdBackgroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public Color getOverflowSelectionColor() {
        return overflowSelectionColor;
    }

    public Color getDragndropForegroundColor() {
        return dragndropForegroundColor;
    }
}
