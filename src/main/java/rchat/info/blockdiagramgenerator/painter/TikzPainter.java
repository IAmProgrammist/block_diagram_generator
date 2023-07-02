package rchat.info.blockdiagramgenerator.painter;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TikzPainter extends AbstractPainter {
    private Map<Color, String> colors = new HashMap<>();
    private Map<Font, String> fonts = new HashMap<>();
    String id = randomName();
    private Color backgroundColor = null;
    double actualWidth = 0;
    List<TikzEntity> entities = new ArrayList<>();
    private static final double CM_IN_1_PT = 0.0352777778;

    private abstract class TikzEntity {
        public Color fill;
        public Color stroke;
        public double strokeWidth;
        public Font font;
        public TikzPainter context;

        private TikzEntity(TikzPainter context) {
            this.context = context;
            this.fill = context.fill;
            this.stroke = context.stroke;
            this.strokeWidth = context.strokeWidth;
            this.font = context.font;
        }

        public abstract String get(double scale);
    }

    private class TikzFillRect extends TikzEntity {
        double x, y, width, height;

        public TikzFillRect(TikzPainter context, double x, double y, double width, double height) {
            super(context);

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public String get(double scale) {
            return String.format(Locale.ENGLISH, "\\draw[fill=%s, draw=none, opacity=%f] (%f,%f) -- (%f,%f) -- (%f,%f) -- (%f,%f) -- cycle;\n",
                    this.context.getColor(this.fill), this.fill.getOpacity(),
                    x * scale, -y * scale, (x + width) * scale, -y * scale, (x + width) * scale, (-y - height) * scale, x * scale, (-y - height) * scale);
        }
    }

    private class TikzStrokeRect extends TikzEntity {
        double x, y, width, height;

        public TikzStrokeRect(TikzPainter context, double x, double y, double width, double height) {
            super(context);

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public String get(double scale) {
            return String.format(Locale.ENGLISH, "\\draw[opacity=%f, fill=none, line width=%fcm, %s] (%f,%f) -- (%f,%f) -- (%f,%f) -- (%f,%f) -- cycle;\n",
                    this.stroke.getOpacity(), this.strokeWidth * scale, this.context.getColor(this.stroke),
                    x * scale, -y * scale, (x + width) * scale, -y * scale, (x + width) * scale, (-y - height) * scale, x * scale, (-y - height) * scale);
        }
    }

    public static final double NORMAL_FONT_SIZE_TEX = 10;

    private class TikzFillText extends TikzEntity {

        double x, y;
        String text;

        public TikzFillText(TikzPainter context, String text, double x, double y) {
            super(context);

            this.x = x;
            this.y = y;
            this.text = text;
        }

        @Override
        public String get(double scale) {
            Font resizedFont = new Font(this.font.getName(), ((this.font.getSize() / CM_IN_1_PT) * scale) / NORMAL_FONT_SIZE_TEX);

            return String.format(Locale.ENGLISH, "\\verbatimfont{\\normalsize\\%s}\n\\node[opacity=%f, above right, %s] at(%f, %f) {\\verb|%s|};\n",
                    this.context.getFont(resizedFont), this.fill.getOpacity(), this.context.getColor(this.fill),
                    x * scale, -y * scale, text);
        }
    }

    private class TikzStrokeLine extends TikzEntity {
        double x1, y1, x2, y2;

        public TikzStrokeLine(TikzPainter context, double x1, double y1, double x2, double y2) {
            super(context);

            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
        }

        @Override
        public String get(double scale) {
            return String.format(Locale.ENGLISH, "\\draw[opacity=%f, fill=none, line width=%f cm, %s] (%f,%f) -- (%f,%f);\n",
                    this.stroke.getOpacity(), this.strokeWidth * scale, this.context.getColor(this.stroke),
                    x1 * scale, -y1 * scale, x2 * scale, -y2 * scale);
        }
    }

    private class TikzFillPolygon extends TikzEntity {
        double[] xPoints;
        double[] yPoints;
        int nPoints;

        public TikzFillPolygon(TikzPainter context, double[] xPoints, double[] yPoints, int nPoints) {
            super(context);

            this.xPoints = xPoints;
            this.yPoints = yPoints;
            this.nPoints = nPoints;
        }

        @Override
        public String get(double scale) {
            StringBuilder document = new StringBuilder();

            document.append(String.format(Locale.ENGLISH, "\\draw[opacity=%f, fill=%s, draw=none]",
                    this.fill.getOpacity(), this.context.getColor(this.fill)));

            for (int i = 0; i < nPoints; i++)
                document.append(String.format(Locale.ENGLISH, " (%f, %f) --", xPoints[i] * scale, -yPoints[i] * scale));

            document.append("cycle;\n");

            return document.toString();
        }
    }

    private class TikzStrokePolygon extends TikzEntity {
        double[] xPoints;
        double[] yPoints;
        int nPoints;

        public TikzStrokePolygon(TikzPainter context, double[] xPoints, double[] yPoints, int nPoints) {
            super(context);

            this.xPoints = xPoints;
            this.yPoints = yPoints;
            this.nPoints = nPoints;
        }

        @Override
        public String get(double scale) {
            StringBuilder document = new StringBuilder();

            document.append(String.format(Locale.ENGLISH, "\\draw[opacity=%f, fill=none, line width=%fcm, %s]",
                    this.stroke.getOpacity(), this.strokeWidth * scale, this.context.getColor(this.stroke)));

            for (int i = 0; i < nPoints; i++)
                document.append(String.format(Locale.ENGLISH, " (%f, %f) --", xPoints[i] * scale, -yPoints[i] * scale));

            document.append("cycle;\n");

            return document.toString();
        }
    }

    private class TikzStrokePolyline extends TikzEntity {
        double[] xPoints;
        double[] yPoints;
        int nPoints;

        public TikzStrokePolyline(TikzPainter context, double[] xPoints, double[] yPoints, int nPoints) {
            super(context);

            this.xPoints = xPoints;
            this.yPoints = yPoints;
            this.nPoints = nPoints;
        }

        @Override
        public String get(double scale) {
            StringBuilder document = new StringBuilder();

            document.append(String.format(Locale.ENGLISH, "\\draw[opacity=%f, fill=none, line width=%fcm, %s]",
                    this.stroke.getOpacity(), this.strokeWidth * scale, this.context.getColor(this.stroke)));

            for (int i = 0; i < nPoints; i++)
                document.append(String.format(Locale.ENGLISH, " (%f, %f) --", xPoints[i] * scale, -yPoints[i] * scale));

            if (nPoints != 0)
                document = new StringBuilder(document.substring(0, document.length() - 3));

            document.append(";\n");

            return document.toString();
        }
    }

    private class TikzFillRoundRect extends TikzEntity {
        double x, y, width, height, arc;

        public TikzFillRoundRect(TikzPainter context, double x, double y, double width, double height, double arc) {
            super(context);

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.arc = arc;
        }

        @Override
        public String get(double scale) {
            return String.format(Locale.ENGLISH, "\\draw[opacity=%f, rounded corners=%f cm, fill=%s, draw=none] (%f,%f) -- (%f,%f) -- (%f,%f) -- (%f,%f) -- cycle;\n",
                    this.fill.getOpacity(), scale * arc, this.context.getColor(this.fill),
                    x * scale, -y * scale, (x + width) * scale, -y * scale, (x + width) * scale, (-y - height) * scale, x * scale, (-y - height) * scale);
        }
    }

    private class TikzStrokeRoundRect extends TikzEntity {
        double x, y, width, height, arc;

        public TikzStrokeRoundRect(TikzPainter context, double x, double y, double width, double height, double arc) {
            super(context);

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.arc = arc;
        }

        @Override
        public String get(double scale) {
            return String.format(Locale.ENGLISH, "\\draw[opacity=%f, rounded corners=%f cm, fill=none, line width=%fcm, %s] (%f,%f) -- (%f,%f) -- (%f,%f) -- (%f,%f) -- cycle;\n",
                    this.stroke.getOpacity(), scale * arc, this.strokeWidth * scale, this.context.getColor(this.stroke),
                    x * scale, -y * scale, (x + width) * scale, -y * scale, (x + width) * scale, (-y - height) * scale, x * scale, (-y - height) * scale);
        }
    }

    private static String randomName() {
        StringBuilder result = new StringBuilder();

        for (int i = (int) (Math.random() * 3 + 5); i >= 0; i--) {
            result.append((char) ('A' + (int) (Math.random() * 20)));
        }

        return result.toString();
    }

    @Override
    public void fillRect(double x, double y, double width, double height) {
        actualWidth = Math.max(actualWidth, x + width);

        entities.add(new TikzFillRect(this, x, y, width, height));
    }

    // TODO: add dashing line support later
    @Override
    public void strokeRect(double x, double y, double width, double height) {
        actualWidth = Math.max(actualWidth, x + width);

        entities.add(new TikzStrokeRect(this, x, y, width, height));
    }

    @Override
    public void fillText(String text, double x, double y) {
        entities.add(new TikzFillText(this, text, x, y));
    }

    // TODO: add dashing line support later
    @Override
    public void strokeLine(double x1, double y1, double x2, double y2) {
        actualWidth = Math.max(actualWidth, Math.max(x1, x2));

        entities.add(new TikzStrokeLine(this, x1, y1, x2, y2));
    }

    @Override
    public void fillPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        this.entities.add(new TikzFillPolygon(this, xPoints, yPoints, nPoints));

        for (int i = 0; i < nPoints; i++)
            actualWidth = Math.max(actualWidth, xPoints[i]);
    }

    // TODO: add dashing line support later
    @Override
    public void strokePolygon(double[] xPoints, double[] yPoints, int nPoints) {
        this.entities.add(new TikzStrokePolygon(this, xPoints, yPoints, nPoints));

        for (int i = 0; i < nPoints; i++)
            actualWidth = Math.max(actualWidth, xPoints[i]);
    }

    // TODO: add dashing line support later
    @Override
    public void strokePolyline(double[] xPoints, double[] yPoints, int nPoints) {
        this.entities.add(new TikzStrokePolyline(this, xPoints, yPoints, nPoints));

        for (int i = 0; i < nPoints; i++)
            actualWidth = Math.max(actualWidth, xPoints[i]);
    }

    @Override
    public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        actualWidth = Math.max(actualWidth, x + width);

        entities.add(new TikzFillRoundRect(this, x, y, width, height, Math.min(arcWidth, arcHeight) / 2));
    }

    @Override
    public void strokeRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        actualWidth = Math.max(actualWidth, x + width);

        entities.add(new TikzStrokeRoundRect(this, x, y, width, height, Math.min(arcWidth, arcHeight) / 2));
    }

    @Override
    public void clearRect(double x, double y, double w, double h) {
        // Do nothing, I guess?
    }

    @Override
    public void drawBackground(Color backgroundColor) {
        this.backgroundColor = backgroundColor;

        saveColor(backgroundColor);
    }

    private void saveColor(Color p) {
        colors.put(p, "color" + id + randomName());
    }

    private String getColor(Color p) {
        if (!colors.containsKey(p))
            saveColor(p);

        return colors.get(p);
    }

    private void saveFont(Font p) {
        fonts.put(p, "font" + id + randomName());
    }

    private String getFont(Font p) {
        if (!fonts.containsKey(p))
            saveFont(p);

        return fonts.get(p);
    }

    @Override
    public void setFill(Color p) {
        super.setFill(p);
    }

    // TODO: add dashing line support later
    @Override
    public void setLineDashes(double... dashes) {
        super.setLineDashes(dashes);
    }

    @Override
    public void setStroke(Color p) {
        super.setStroke(p);
    }

    @Override
    public void setStrokeWidth(double lw) {
        super.setStrokeWidth(lw);
    }

    @Override
    public void setFont(Font f) {
        super.setFont(f);
    }

    @Override
    public void setLineWidth(double lineWidth) {
        super.setLineWidth(lineWidth);
    }

    // Passing width in centimeters
    public void save(File file, double maxWidth, boolean includeComments) {
        double scale = maxWidth / actualWidth;


        StringBuilder doc = new StringBuilder();

        for (TikzEntity entity : this.entities)
            doc.append(entity.get(scale));

        // TODO: добавить все зависимости
        StringBuilder result = new StringBuilder();
        if (includeComments)
            result.append("% Созданная блок схема работает только с компиляторами XeTeX и LuaTeX.\n");
        result.append("\\documentclass{article}\n\n");
        if (includeComments)
            result.append("% Необходимые зависимости\n");
        result.append("\\usepackage[utf8]{inputenc}\n" +
                "\\usepackage[english,russian]{babel}\n" +
                "\\usepackage{pgfplots}\n" +
                "\\usepackage{verbatim}\n" +
                "\\usetikzlibrary{positioning}\n" +
                "\\usetikzlibrary{shapes.geometric}\n" +
                "\\usetikzlibrary{shapes.misc}\n" +
                "\\usetikzlibrary{calc}\n" +
                "\\usetikzlibrary{chains}\n" +
                "\\usetikzlibrary{matrix}\n" +
                "\\usetikzlibrary{decorations.text}\n" +
                "\\usepackage{fontspec}\n" +
                "\\usetikzlibrary{backgrounds}\n\n");

        if (backgroundColor == null) {
            backgroundColor = Color.WHITE;
        }

        getColor(backgroundColor);

        result.append("\\begin{document}\n");
        //if (includeComments)
        //    result.append("\n% При формировании документа могли возникнуть некоторые неточности при расчёте, поэтому я пихнул ещё и resizebox, чтоб уж наверняка\n");

        if (includeComments) {
            result.append("% Блок-схема\n");
            result.append("% Если Вы хотите добавить блок схему в свой документ, скопируйте код между комментариями\n");
            result.append("% С линияи и вставьте в документ.\n\n");
            result.append("% --------------------------\n");
        }

        result.append("\\newsavebox{\\dbg" + id + "}\n" +
                "\\begin{lrbox}{\\dbg" + id + "}");

        result.append(String.format(Locale.ENGLISH, "\\begin{tikzpicture}[every node/.style={inner sep=0,outer sep=0}, background rectangle/.style={opacity=%f, fill=%s}, show background rectangle]\n", backgroundColor.getOpacity(), getColor(this.backgroundColor)));

        result.append("\\makeatletter\n\\newcommand{\\verbatimfont}[1]{\\def\\verbatim@font{#1}}\n\\makeatother\n");

        if (includeComments)
            result.append("% Шрифты\n");
        // Adding fonts
        for (Map.Entry<Font, String> fontEntity : fonts.entrySet()) {
            result.append(String.format(Locale.ENGLISH, "\\newfontfamily\\%s[Scale=%f, SizeFeatures={Size=%f}]{%s}\n", fontEntity.getValue(), fontEntity.getKey().getSize(), NORMAL_FONT_SIZE_TEX, fontEntity.getKey().getName()));
        }

        if (includeComments)
            result.append("\n% Цвета\n");
        // Adding colors
        for (Map.Entry<Color, String> colorEntity : colors.entrySet()) {
            result.append(String.format(Locale.ENGLISH, "\\definecolor{%s}{rgb}{%f,%f,%f}\n", colorEntity.getValue(),
                    colorEntity.getKey().getRed(), colorEntity.getKey().getGreen(), colorEntity.getKey().getBlue()));
        }

        result.append(doc);

        result.append("\\end{tikzpicture}\n");
        result.append("\\end{lrbox}\n");

        if (includeComments) {
            result.append("% Здесь Вы можете поменять размер блок схемы. Оношение ширина/высота будет сохранено.\n" +
                    "% Для изменения размеров блок схемы Вы можете изменять первый параметр resizebox, он задаёт желаемую ширину.\n");
        }

        result.append(String.format(Locale.ENGLISH, "\\resizebox{%fcm}{!}{\\usebox{%s}}\n", maxWidth, "\\dbg" + id));

        if (includeComments) {
            result.append("% --------------------------\n\n");
            result.append("% Конец блок схемы\n");
        }

        result.append("\\end{document}");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
                StandardCharsets.UTF_8))) {
            writer.write(result.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
