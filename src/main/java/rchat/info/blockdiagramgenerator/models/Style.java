package rchat.info.blockdiagramgenerator.models;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.scene.paint.Color;
import org.json.JSONException;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDElementController;

public final class Style {

    public static void init() {
        DEFAULT_STYLE_NAME = "Default";
        DEFAULT_STYLE = new Style(DEFAULT_STYLE_NAME,
                "Courier New", Color.BLACK, Color.BLACK, Color.WHITE, new Color(0.9, 0.9, 0.9, 1), 50, 5,
                3, 30, 20, 70, 50, 50, 15,
                30, "+", "-", new Color(0.8, 0.8, 0.8, 1),
                Color.LIGHTGREEN, Color.DARKSEAGREEN, new Color(0.0, 1, 0.0, 0.3), 10, 1,
                3, 30, 300, 25,
                10, true, false, true, false,
                Color.PURPLE, 20
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
    private static final String styleNameKey = "styleName";
    private String positiveBranchText;
    private static final String positiveBranchTextKey = "positiveBranchText";
    private String negativeBranchText;
    private static final String negativeBranchTextKey = "negativeBranchText";
    private String fontBasicName;
    private static final String fontBasicNameKey = "fontBasicName";
    private double tileSize;
    private static final String tileSizeKey = "tileSize";
    private double tileStrokeWidthDefault;
    private static final String tileStrokeWidthDefaultKey = "tileStrokeWidthDefault";
    private double selectionBorderWidth;
    private static final String selectionBorderWidthKey = "selectionBorderWidth";
    private double containerOverflowPadding;
    private static final String containerOverflowPaddingKey = "containerOverflowPadding";
    private double maxBdContainerDragndropWidth;
    private static final String maxBdContainerDragndropWidthKey = "maxBdContainerDragndropWidth";
    private double maxBdContainerDragndropWidthMargin;
    private static final String maxBdContainerDragndropWidthMarginKey = "maxBdContainerDragndropWidthMargin";
    private double fontBasicSize;
    private static final String fontBasicSizeKey = "fontBasicSize";
    private double strokeWidthDefault;
    private static final String strokeWidthDefaultKey = "strokeWidthDefault";
    private double connectorsWidth;
    private static final String connectorsWidthKey = "connectorsWidth";
    private double textPadding;
    private static final String textPaddingKey = "textPadding";
    private double lineSpacing;
    private static final String lineSpacingKey = "lineSpacing";
    private double elementsSpacing;
    private static final String elementsSpacingKey = "elementsSpacing";
    private double decisionBlocksPadding;
    private static final String decisionBlocksPaddingKey = "decisionBlocksPadding";
    private double minDecisionShoulderLen;
    private static final String minDecisionShoulderLenKey = "minDecisionShoulderLen";
    private double dashLineWidthLine;
    private static final String dashLineWidthLineKey = "dashLineWidthLine";
    private double dashLineWidthSpace;
    private static final String dashLineWidthSpaceKey = "dashLineWidthSpace";
    private int tilesInTile;
    private static final String tilesInTileKey = "tilesInTile";
    private boolean isDebugModeEnabled;
    private static final String isDebugModeEnabledKey = "isDebugModeEnabled";
    private boolean isDebugShowFps;
    private static final String isDebugShowFpsKey = "isDebugShowFps";
    private boolean isDebugTikzIncludeComments;
    private static final String isDebugTikzIncludeCommentsKey = "isDebugTikzIncludeComments";
    private boolean debugDrawBorders;
    private static final String debugDrawBordersKey = "debugDrawBorders";
    private Color debugBorderColor;
    private static final String debugBorderColorKey = "debugBorderColor";
    private Color strokeColor;
    private static final String strokeColorKey = "strokeColor";
    private Color fontColor;
    private static final String fontColorKey = "fontColor";
    private Color bdBackgroundColor;
    private static final String bdBackgroundColorKey = "bdBackgroundColor";
    private Color backgroundColor;
    private static final String backgroundColorKey = "backgroundColor";
    private Color gridColor;
    private static final String gridColorKey = "gridColor";
    private Color selectedColor;
    private static final String selectedColorKey = "selectedColor";
    private Color overflowSelectionColor;
    private static final String overflowSelectionColorKey = "overflowSelectionColor";
    private Color dragndropForegroundColor;
    private static final String dragndropForegroundColorKey = "dragndropForegroundColor";
    private double branchNamePadding;
    private static final String branchNamePaddingKey = "branchNamePadding";

    private Style(String styleName, String fontBasicName, Color strokeColor, Color fontColor, Color bdBackgroundColor, Color backgroundColor, double fontBasicSize, double strokeWidthDefault, double connectorsWidth, double textPadding, double lineSpacing, double elementsSpacing, double decisionBlocksPadding, double minDecisionShoulderLen, double dashLineWidthLine, double dashLineWidthSpace, String positiveBranchText, String negativeBranchText, Color gridColor, Color selectedColor, Color overflowSelectionColor, Color dragndropForegroundColor, double tileSize, double tileStrokeWidthDefault, double selectionBorderWidth, double containerOverflowPadding, double maxBdContainerDragndropWidth, double maxBdContainerDragndropWidthMargin, int tilesInTile, boolean isDebugModeEnabled, boolean isDebugShowFps, boolean isDebugTikzIncludeComments, boolean debugDrawBorders, Color debugBorderColor, double branchNamePadding) {
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
        this.branchNamePadding = branchNamePadding;
    }

    private static void saveStyle(String styleName, Style style) {
        Preferences prefs = Preferences.userNodeForPackage(Style.class);
        Preferences styleNode = prefs.node("styles").node(styleName);

        styleNode.put(styleNameKey, style.styleName);

        styleNode.put(positiveBranchTextKey, style.positiveBranchText);
        styleNode.put(negativeBranchTextKey, style.negativeBranchText);
        styleNode.put(fontBasicNameKey, style.fontBasicName);

        styleNode.putDouble(tileSizeKey, style.tileSize);
        styleNode.putDouble(branchNamePaddingKey, style.branchNamePadding);
        styleNode.putDouble(tileStrokeWidthDefaultKey, style.tileStrokeWidthDefault);
        styleNode.putDouble(selectionBorderWidthKey, style.selectionBorderWidth);
        styleNode.putDouble(containerOverflowPaddingKey, style.containerOverflowPadding);
        styleNode.putDouble(maxBdContainerDragndropWidthKey, style.maxBdContainerDragndropWidth);
        styleNode.putDouble(maxBdContainerDragndropWidthMarginKey, style.maxBdContainerDragndropWidthMargin);
        styleNode.putDouble(fontBasicSizeKey, style.fontBasicSize);
        styleNode.putDouble(strokeWidthDefaultKey, style.strokeWidthDefault);
        styleNode.putDouble(connectorsWidthKey, style.connectorsWidth);
        styleNode.putDouble(textPaddingKey, style.textPadding);
        styleNode.putDouble(lineSpacingKey, style.lineSpacing);
        styleNode.putDouble(elementsSpacingKey, style.elementsSpacing);
        styleNode.putDouble(decisionBlocksPaddingKey, style.decisionBlocksPadding);
        styleNode.putDouble(minDecisionShoulderLenKey, style.minDecisionShoulderLen);
        styleNode.putDouble(dashLineWidthLineKey, style.dashLineWidthLine);
        styleNode.putDouble(dashLineWidthSpaceKey, style.dashLineWidthSpace);

        styleNode.putInt(tilesInTileKey, style.tilesInTile);

        styleNode.putBoolean(isDebugModeEnabledKey, style.isDebugModeEnabled);
        styleNode.putBoolean(isDebugShowFpsKey, style.isDebugShowFps);
        styleNode.putBoolean(isDebugTikzIncludeCommentsKey, style.isDebugTikzIncludeComments);
        styleNode.putBoolean(debugDrawBordersKey, style.debugDrawBorders);

        putColor(styleNode, debugBorderColorKey, style.debugBorderColor);
        putColor(styleNode, strokeColorKey, style.strokeColor);
        putColor(styleNode, fontColorKey, style.fontColor);
        putColor(styleNode, bdBackgroundColorKey, style.bdBackgroundColor);
        putColor(styleNode, gridColorKey, style.gridColor);
        putColor(styleNode, backgroundColorKey, style.backgroundColor);
        putColor(styleNode, selectedColorKey, style.selectedColor);
        putColor(styleNode, overflowSelectionColorKey, style.overflowSelectionColor);
        putColor(styleNode, dragndropForegroundColorKey, style.dragndropForegroundColor);
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

    public static Style fromFile(String styleName, File file) {
        if (file == null)
            return null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                StandardCharsets.UTF_8))) {
            String res = "";
            while (reader.ready()) {
                res += reader.readLine() + "\n";
            }

            JSONObject style = new JSONObject(res);

            Style fileStyle = new Style(styleName,
                    getJSON(style, fontBasicNameKey, DEFAULT_STYLE.fontBasicName),
                    getJSON(style, strokeColorKey, DEFAULT_STYLE.strokeColor),
                    getJSON(style, fontColorKey, DEFAULT_STYLE.fontColor),
                    getJSON(style, bdBackgroundColorKey, DEFAULT_STYLE.bdBackgroundColor),
                    getJSON(style, backgroundColorKey, DEFAULT_STYLE.backgroundColor),
                    getJSON(style, fontBasicSizeKey, DEFAULT_STYLE.fontBasicSize),
                    getJSON(style, strokeWidthDefaultKey, DEFAULT_STYLE.strokeWidthDefault),
                    getJSON(style, connectorsWidthKey, DEFAULT_STYLE.connectorsWidth),
                    getJSON(style, textPaddingKey, DEFAULT_STYLE.textPadding),
                    getJSON(style, lineSpacingKey, DEFAULT_STYLE.lineSpacing),
                    getJSON(style, elementsSpacingKey, DEFAULT_STYLE.elementsSpacing),
                    getJSON(style, decisionBlocksPaddingKey, DEFAULT_STYLE.decisionBlocksPadding),
                    getJSON(style, minDecisionShoulderLenKey, DEFAULT_STYLE.minDecisionShoulderLen),
                    getJSON(style, dashLineWidthLineKey, DEFAULT_STYLE.dashLineWidthLine),
                    getJSON(style, dashLineWidthSpaceKey, DEFAULT_STYLE.dashLineWidthSpace),
                    getJSON(style, positiveBranchTextKey, DEFAULT_STYLE.positiveBranchText),
                    getJSON(style, negativeBranchTextKey, DEFAULT_STYLE.negativeBranchText),
                    getJSON(style, gridColorKey, DEFAULT_STYLE.gridColor),
                    getJSON(style, selectedColorKey, DEFAULT_STYLE.selectedColor),
                    getJSON(style, overflowSelectionColorKey, DEFAULT_STYLE.overflowSelectionColor),
                    getJSON(style, dragndropForegroundColorKey, DEFAULT_STYLE.dragndropForegroundColor),
                    getJSON(style, tileSizeKey, DEFAULT_STYLE.tileSize),
                    getJSON(style, tileStrokeWidthDefaultKey, DEFAULT_STYLE.tileStrokeWidthDefault),
                    getJSON(style, selectionBorderWidthKey, DEFAULT_STYLE.selectionBorderWidth),
                    getJSON(style, containerOverflowPaddingKey, DEFAULT_STYLE.containerOverflowPadding),
                    getJSON(style, maxBdContainerDragndropWidthKey, DEFAULT_STYLE.maxBdContainerDragndropWidth),
                    getJSON(style, maxBdContainerDragndropWidthMarginKey, DEFAULT_STYLE.maxBdContainerDragndropWidthMargin),
                    getJSON(style, tilesInTileKey, DEFAULT_STYLE.tilesInTile),
                    getJSON(style, isDebugModeEnabledKey, DEFAULT_STYLE.isDebugModeEnabled),
                    getJSON(style, isDebugShowFpsKey, DEFAULT_STYLE.isDebugShowFps),
                    getJSON(style, isDebugTikzIncludeCommentsKey, DEFAULT_STYLE.isDebugTikzIncludeComments),
                    getJSON(style, debugDrawBordersKey, DEFAULT_STYLE.debugDrawBorders),
                    getJSON(style, debugBorderColorKey, DEFAULT_STYLE.debugBorderColor),
                    getJSON(style, branchNamePaddingKey, DEFAULT_STYLE.branchNamePadding));

            return createStyle(styleName, fileStyle);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static void toFile(Style saveStyle, File file) {
        if (file == null)
            return;

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
                StandardCharsets.UTF_8))) {

            JSONObject style = new JSONObject();

            putJSON(style, fontBasicNameKey, saveStyle.fontBasicName);
            putJSON(style, strokeColorKey, saveStyle.strokeColor);
            putJSON(style, fontColorKey, saveStyle.fontColor);
            putJSON(style, bdBackgroundColorKey, saveStyle.bdBackgroundColor);
            putJSON(style, backgroundColorKey, saveStyle.backgroundColor);
            putJSON(style, fontBasicSizeKey, saveStyle.fontBasicSize);
            putJSON(style, strokeWidthDefaultKey, saveStyle.strokeWidthDefault);
            putJSON(style, connectorsWidthKey, saveStyle.connectorsWidth);
            putJSON(style, textPaddingKey, saveStyle.textPadding);
            putJSON(style, lineSpacingKey, saveStyle.lineSpacing);
            putJSON(style, elementsSpacingKey, saveStyle.elementsSpacing);
            putJSON(style, decisionBlocksPaddingKey, saveStyle.decisionBlocksPadding);
            putJSON(style, minDecisionShoulderLenKey, saveStyle.minDecisionShoulderLen);
            putJSON(style, dashLineWidthLineKey, saveStyle.dashLineWidthLine);
            putJSON(style, dashLineWidthSpaceKey, saveStyle.dashLineWidthSpace);
            putJSON(style, positiveBranchTextKey, saveStyle.positiveBranchText);
            putJSON(style, negativeBranchTextKey, saveStyle.negativeBranchText);
            putJSON(style, gridColorKey, saveStyle.gridColor);
            putJSON(style, selectedColorKey, saveStyle.selectedColor);
            putJSON(style, overflowSelectionColorKey, saveStyle.overflowSelectionColor);
            putJSON(style, dragndropForegroundColorKey, saveStyle.dragndropForegroundColor);
            putJSON(style, tileSizeKey, saveStyle.tileSize);
            putJSON(style, tileStrokeWidthDefaultKey, saveStyle.tileStrokeWidthDefault);
            putJSON(style, selectionBorderWidthKey, saveStyle.selectionBorderWidth);
            putJSON(style, containerOverflowPaddingKey, saveStyle.containerOverflowPadding);
            putJSON(style, maxBdContainerDragndropWidthKey, saveStyle.maxBdContainerDragndropWidth);
            putJSON(style, maxBdContainerDragndropWidthMarginKey, saveStyle.maxBdContainerDragndropWidthMargin);
            putJSON(style, tilesInTileKey, saveStyle.tilesInTile);
            putJSON(style, isDebugModeEnabledKey, saveStyle.isDebugModeEnabled);
            putJSON(style, isDebugShowFpsKey, saveStyle.isDebugShowFps);
            putJSON(style, isDebugTikzIncludeCommentsKey, saveStyle.isDebugTikzIncludeComments);
            putJSON(style, debugDrawBordersKey, saveStyle.debugDrawBorders);
            putJSON(style, debugBorderColorKey, saveStyle.debugBorderColor);
            putJSON(style, branchNamePaddingKey, saveStyle.branchNamePadding);

            writer.write(style.toString());
        } catch (IOException ignored) {
        }
    }

    public static String getJSON(JSONObject object, String key, String defaultVal) {
        try {
            return object.getString(key);
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public static double getJSON(JSONObject object, String key, Double defaultVal) {
        try {
            return object.getDouble(key);
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public static int getJSON(JSONObject object, String key, Integer defaultVal) {
        try {
            return object.getInt(key);
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public static boolean getJSON(JSONObject object, String key, Boolean defaultVal) {
        try {
            return object.getBoolean(key);
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public static Color getJSON(JSONObject object, String key, Color defaultVal) {
        try {
            return new Color(
                    Math.min(1, Math.max(0, object.has(key + "R") ? object.getDouble(key + "R") : defaultVal.getRed())),
                    Math.min(1, Math.max(0, object.has(key + "G") ? object.getDouble(key + "G") : defaultVal.getRed())),
                    Math.min(1, Math.max(0, object.has(key + "B") ? object.getDouble(key + "B") : defaultVal.getRed())),
                    Math.min(1, Math.max(0, object.has(key + "A") ? object.getDouble(key + "A") : defaultVal.getRed())));
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public static void putJSON(JSONObject object, String key, String value) {
        try {
            object.put(key, value);
        } catch (JSONException ignored) {
        }
    }

    public static void putJSON(JSONObject object, String key, double value) {
        try {
            object.put(key, value);
        } catch (JSONException ignored) {
        }
    }

    public static void putJSON(JSONObject object, String key, int value) {
        try {
            object.put(key, value);
        } catch (JSONException ignored) {
        }
    }

    public static void putJSON(JSONObject object, String key, boolean value) {
        try {
            object.put(key, value);
        } catch (JSONException ignored) {
        }
    }

    public static void putJSON(JSONObject object, String key, Color value) {
        try {
            object.put(key + "R", value.getRed());
            object.put(key + "G", value.getGreen());
            object.put(key + "B", value.getBlue());
            object.put(key + "A", value.getOpacity());
        } catch (JSONException ignored) {
        }
    }

    public static void removeStyle(String styleName) {
        if (hasStyle(styleName)) {
            try {
                if (getCurrentStyle().getStyleName().equals(styleName))
                    setCurrentStyle(DEFAULT_STYLE);

                if (!DEFAULT_STYLE_NAME.equals(styleName))
                    Preferences.userNodeForPackage(Style.class).node("styles").node(styleName).removeNode();
            } catch (BackingStoreException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean setCurrentStyle(Style style) {
        if (hasStyle(style.getStyleName())) {
            Preferences.userNodeForPackage(Style.class).put("current", style.getStyleName());
            currentStyle = getStyle(style.getStyleName());
            return true;
        }

        return false;
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
            return new Style(styleName, style.get(fontBasicNameKey, DEFAULT_STYLE.fontBasicName),
                    getColor(style, strokeColorKey, DEFAULT_STYLE.strokeColor),
                    getColor(style, fontColorKey, DEFAULT_STYLE.fontColor),
                    getColor(style, bdBackgroundColorKey, DEFAULT_STYLE.bdBackgroundColor),
                    getColor(style, backgroundColorKey, DEFAULT_STYLE.backgroundColor),
                    style.getDouble(fontBasicSizeKey, DEFAULT_STYLE.fontBasicSize),
                    style.getDouble(strokeWidthDefaultKey, DEFAULT_STYLE.strokeWidthDefault),
                    style.getDouble(connectorsWidthKey, DEFAULT_STYLE.connectorsWidth),
                    style.getDouble(textPaddingKey, DEFAULT_STYLE.textPadding),
                    style.getDouble(lineSpacingKey, DEFAULT_STYLE.lineSpacing),
                    style.getDouble(elementsSpacingKey, DEFAULT_STYLE.elementsSpacing),
                    style.getDouble(decisionBlocksPaddingKey, DEFAULT_STYLE.decisionBlocksPadding),
                    style.getDouble(minDecisionShoulderLenKey, DEFAULT_STYLE.minDecisionShoulderLen),
                    style.getDouble(dashLineWidthLineKey, DEFAULT_STYLE.dashLineWidthLine),
                    style.getDouble(dashLineWidthSpaceKey, DEFAULT_STYLE.dashLineWidthSpace),
                    style.get(positiveBranchTextKey, DEFAULT_STYLE.positiveBranchText),
                    style.get(negativeBranchTextKey, DEFAULT_STYLE.negativeBranchText),
                    getColor(style, gridColorKey, DEFAULT_STYLE.gridColor),
                    getColor(style, selectedColorKey, DEFAULT_STYLE.selectedColor),
                    getColor(style, overflowSelectionColorKey, DEFAULT_STYLE.overflowSelectionColor),
                    getColor(style, dragndropForegroundColorKey, DEFAULT_STYLE.dragndropForegroundColor),
                    style.getDouble(tileSizeKey, DEFAULT_STYLE.tileSize),
                    style.getDouble(tileStrokeWidthDefaultKey, DEFAULT_STYLE.tileStrokeWidthDefault),
                    style.getDouble(selectionBorderWidthKey, DEFAULT_STYLE.selectionBorderWidth),
                    style.getDouble(containerOverflowPaddingKey, DEFAULT_STYLE.containerOverflowPadding),
                    style.getDouble(maxBdContainerDragndropWidthKey, DEFAULT_STYLE.maxBdContainerDragndropWidth),
                    style.getDouble(maxBdContainerDragndropWidthMarginKey, DEFAULT_STYLE.maxBdContainerDragndropWidthMargin),
                    style.getInt(tilesInTileKey, DEFAULT_STYLE.tilesInTile),
                    style.getBoolean(isDebugModeEnabledKey, DEFAULT_STYLE.isDebugModeEnabled),
                    style.getBoolean(isDebugShowFpsKey, DEFAULT_STYLE.isDebugShowFps),
                    style.getBoolean(isDebugTikzIncludeCommentsKey, DEFAULT_STYLE.isDebugTikzIncludeComments),
                    style.getBoolean(debugDrawBordersKey, DEFAULT_STYLE.debugDrawBorders),
                    getColor(style, debugBorderColorKey, DEFAULT_STYLE.debugBorderColor),
                    style.getDouble(branchNamePaddingKey, DEFAULT_STYLE.branchNamePadding));


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
        if (hasStyle(style) && !DEFAULT_STYLE_NAME.equals(style))
            Preferences.userNodeForPackage(Style.class).node("styles").node(style).put(key, value);
    }

    private static void update(String style, String key, double value) {
        if (hasStyle(style) && !DEFAULT_STYLE_NAME.equals(style))
            Preferences.userNodeForPackage(Style.class).node("styles").node(style).putDouble(key, value);
    }

    private static void update(String style, String key, int value) {
        if (hasStyle(style) && !DEFAULT_STYLE_NAME.equals(style))
            Preferences.userNodeForPackage(Style.class).node("styles").node(style).putInt(key, value);
    }

    private static void update(String style, String key, boolean value) {
        if (hasStyle(style) && !DEFAULT_STYLE_NAME.equals(style))
            Preferences.userNodeForPackage(Style.class).node("styles").node(style).putBoolean(key, value);
    }

    private static void update(String style, String key, Color value) {
        if (hasStyle(style) && !DEFAULT_STYLE_NAME.equals(style))
            putColor(Preferences.userNodeForPackage(Style.class).node("styles").node(style), key, value);
    }

    public void setPositiveBranchText(String positiveBranchText) {
        update(this.styleName, positiveBranchTextKey, positiveBranchText);
        this.positiveBranchText = positiveBranchText;
    }

    public void setNegativeBranchText(String negativeBranchText) {
        update(this.styleName, negativeBranchTextKey, negativeBranchText);
        this.negativeBranchText = negativeBranchText;
    }

    public void setFontBasicName(String fontBasicName) {
        update(this.styleName, fontBasicNameKey, fontBasicName);
        this.fontBasicName = fontBasicName;
    }

    public void setTileSize(double tileSize) {
        update(this.styleName, tileSizeKey, tileSize);
        this.tileSize = tileSize;
    }

    public void setTileStrokeWidthDefault(double tileStrokeWidthDefault) {
        update(this.styleName, tileStrokeWidthDefaultKey, tileStrokeWidthDefault);
        this.tileStrokeWidthDefault = tileStrokeWidthDefault;
    }

    public void setSelectionBorderWidth(double selectionBorderWidth) {
        update(this.styleName, selectionBorderWidthKey, selectionBorderWidth);
        this.selectionBorderWidth = selectionBorderWidth;
    }

    public void setContainerOverflowPadding(double containerOverflowPadding) {
        update(this.styleName, containerOverflowPaddingKey, containerOverflowPadding);
        this.containerOverflowPadding = containerOverflowPadding;
    }

    public void setMaxBdContainerDragndropWidth(double maxBdContainerDragndropWidth) {
        update(this.styleName, maxBdContainerDragndropWidthKey, maxBdContainerDragndropWidth);
        this.maxBdContainerDragndropWidth = maxBdContainerDragndropWidth;
    }

    public void setMaxBdContainerDragndropWidthMargin(double maxBdContainerDragndropWidthMargin) {
        update(this.styleName, maxBdContainerDragndropWidthMarginKey, maxBdContainerDragndropWidthMargin);
        this.maxBdContainerDragndropWidthMargin = maxBdContainerDragndropWidthMargin;
    }

    public void setFontBasicSize(double fontBasicSize) {
        update(this.styleName, fontBasicSizeKey, fontBasicSize);
        this.fontBasicSize = fontBasicSize;
    }

    public void setStrokeWidthDefault(double strokeWidthDefault) {
        update(this.styleName, strokeWidthDefaultKey, strokeWidthDefault);
        this.strokeWidthDefault = strokeWidthDefault;
    }

    public void setConnectorsWidth(double connectorsWidth) {
        update(this.styleName, connectorsWidthKey, connectorsWidth);
        this.connectorsWidth = connectorsWidth;
    }

    public void setTextPadding(double textPadding) {
        update(this.styleName, textPaddingKey, textPadding);
        this.textPadding = textPadding;
    }

    public void setLineSpacing(double lineSpacing) {
        update(this.styleName, lineSpacingKey, lineSpacing);
        this.lineSpacing = lineSpacing;
    }

    public void setElementsSpacing(double elementsSpacing) {
        update(this.styleName, elementsSpacingKey, elementsSpacing);
        this.elementsSpacing = elementsSpacing;
    }

    public void setDecisionBlocksPadding(double decisionBlocksPadding) {
        update(this.styleName, decisionBlocksPaddingKey, decisionBlocksPadding);
        this.decisionBlocksPadding = decisionBlocksPadding;
    }

    public void setMinDecisionShoulderLen(double minDecisionShoulderLen) {
        update(this.styleName, minDecisionShoulderLenKey, minDecisionShoulderLen);
        this.minDecisionShoulderLen = minDecisionShoulderLen;
    }

    public void setDashLineWidthLine(double dashLineWidthLine) {
        update(this.styleName, dashLineWidthLineKey, dashLineWidthLine);
        this.dashLineWidthLine = dashLineWidthLine;
    }

    public void setDashLineWidthSpace(double dashLineWidthSpace) {
        update(this.styleName, dashLineWidthSpaceKey, dashLineWidthSpace);
        this.dashLineWidthSpace = dashLineWidthSpace;
    }

    public void setTilesInTile(int tilesInTile) {
        update(this.styleName, tilesInTileKey, tilesInTile);
        this.tilesInTile = tilesInTile;
    }

    public void setDebugModeEnabled(boolean debugModeEnabled) {
        update(this.styleName, isDebugModeEnabledKey, debugModeEnabled);
        isDebugModeEnabled = debugModeEnabled;
    }

    public void setDebugShowFps(boolean debugShowFps) {
        update(this.styleName, isDebugShowFpsKey, debugShowFps);
        isDebugShowFps = debugShowFps;
    }

    public void setDebugTikzIncludeComments(boolean debugTikzIncludeComments) {
        update(this.styleName, isDebugTikzIncludeCommentsKey, debugTikzIncludeComments);
        isDebugTikzIncludeComments = debugTikzIncludeComments;
    }

    public void setDebugDrawBorders(boolean debugDrawBorders) {
        update(this.styleName, debugDrawBordersKey, debugDrawBorders);
        this.debugDrawBorders = debugDrawBorders;
    }

    public void setDebugBorderColor(Color debugBorderColor) {
        update(this.styleName, debugBorderColorKey, debugBorderColor);
        this.debugBorderColor = debugBorderColor;
    }

    public void setStrokeColor(Color strokeColor) {
        update(this.styleName, strokeColorKey, strokeColor);
        this.strokeColor = strokeColor;
    }

    public void setFontColor(Color fontColor) {
        update(this.styleName, fontColorKey, fontColor);
        this.fontColor = fontColor;
    }

    public void setBdBackgroundColor(Color bdBackgroundColor) {
        update(this.styleName, bdBackgroundColorKey, bdBackgroundColor);
        this.bdBackgroundColor = bdBackgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        update(this.styleName, backgroundColorKey, backgroundColor);
        this.backgroundColor = backgroundColor;
    }

    public void setGridColor(Color gridColor) {
        update(this.styleName, gridColorKey, gridColor);
        this.gridColor = gridColor;
    }

    public void setSelectedColor(Color selectedColor) {
        update(this.styleName, selectedColorKey, selectedColor);
        this.selectedColor = selectedColor;
    }

    public void setOverflowSelectionColor(Color overflowSelectionColor) {
        update(this.styleName, overflowSelectionColorKey, overflowSelectionColor);
        this.overflowSelectionColor = overflowSelectionColor;
    }

    public void setDragndropForegroundColor(Color dragndropForegroundColor) {
        update(this.styleName, dragndropForegroundColorKey, dragndropForegroundColor);
        this.dragndropForegroundColor = dragndropForegroundColor;
    }

    public void setBranchNamePadding(double branchNamePadding) {
        update(this.styleName, branchNamePaddingKey, branchNamePadding);
        this.branchNamePadding = branchNamePadding;
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

    public double getBranchNamePadding() {
        return branchNamePadding;
    }
}
