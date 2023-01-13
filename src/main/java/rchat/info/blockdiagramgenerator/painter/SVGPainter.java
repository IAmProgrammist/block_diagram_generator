package rchat.info.blockdiagramgenerator.painter;

import org.jfree.svg.SVGGraphics2D;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SVGPainter extends AbstractPainter {
    SVGGraphics2D gc;

    public SVGPainter(double width, double height) {
        gc = new SVGGraphics2D(width, height);
    }

    @Override
    public void fillRect(double x, double y, double width, double height) {
        gc.setPaint(new Color((float) fill.getRed(), (float) fill.getGreen(), (float) fill.getBlue(), (float) fill.getOpacity()));
        gc.fillRect((int) x, (int) y, (int) width, (int) height);
    }

    @Override
    public void strokeRect(double x, double y, double width, double height) {
        float[] cast = new float[lineDashes.length];
        for (int i = 0; i < cast.length; i++) {
            cast[i] = (float) lineDashes[i];
        }
        gc.setStroke(new BasicStroke((float) strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
                cast.length == 0 ? null : cast, 0));
        gc.setPaint(new Color((int) (stroke.getRed() * 255), (int) (stroke.getGreen() * 255),
                (int) (stroke.getBlue() * 255), (int) (stroke.getOpacity() * 255)));
        gc.drawRect((int) x, (int) y, (int) width, (int) height);
    }

    @Override
    public void fillText(String text, double x, double y) {
        gc.setStroke(new BasicStroke(0.0F));
        gc.setPaint(new Color((float) fill.getRed(), (float) fill.getGreen(), (float) fill.getBlue(), (float) fill.getOpacity()));
        Font f = new Font(font.getName(), Font.PLAIN, (int) font.getSize());
        gc.setFont(f);
        gc.drawString(text, (float) x, (float) y);
    }

    @Override
    public void strokeLine(double x1, double y1, double x2, double y2) {
        float[] cast = new float[lineDashes.length];
        for (int i = 0; i < cast.length; i++) {
            cast[i] = (float) lineDashes[i];
        }
        gc.setStroke(new BasicStroke((float) strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
                cast.length == 0 ? null : cast, 0));
        gc.setPaint(new Color((int) (stroke.getRed() * 255), (int) (stroke.getGreen() * 255),
                (int) (stroke.getBlue() * 255), (int) (stroke.getOpacity() * 255)));
        gc.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
    }

    @Override
    public void fillPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        gc.setPaint(new Color((float) fill.getRed(), (float) fill.getGreen(), (float) fill.getBlue(), (float) fill.getOpacity()));
        int[] cast1 = new int[nPoints], cast2 = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            cast1[i] = (int) xPoints[i];
            cast2[i] = (int) yPoints[i];
        }
        gc.fillPolygon(cast1, cast2, nPoints);
    }

    @Override
    public void strokePolygon(double[] xPoints, double[] yPoints, int nPoints) {
        float[] cast = new float[lineDashes.length];
        for (int i = 0; i < cast.length; i++) {
            cast[i] = (float) lineDashes[i];
        }
        gc.setStroke(new BasicStroke((float) strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
                cast.length == 0 ? null : cast, 0));
        gc.setPaint(new Color((int) (stroke.getRed() * 255), (int) (stroke.getGreen() * 255),
                (int) (stroke.getBlue() * 255), (int) (stroke.getOpacity() * 255)));
        int[] cast1 = new int[nPoints], cast2 = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            cast1[i] = (int) xPoints[i];
            cast2[i] = (int) yPoints[i];
        }
        gc.drawPolygon(cast1, cast2, nPoints);
    }

    @Override
    public void strokePolyline(double[] xPoints, double[] yPoints, int nPoints) {
        float[] cast = new float[lineDashes.length];
        for (int i = 0; i < cast.length; i++) {
            cast[i] = (float) lineDashes[i];
        }
        gc.setStroke(new BasicStroke((float) strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
                cast.length == 0 ? null : cast, 0));
        gc.setPaint(new Color((int) (stroke.getRed() * 255), (int) (stroke.getGreen() * 255),
                (int) (stroke.getBlue() * 255), (int) (stroke.getOpacity() * 255)));
        int[] cast1 = new int[nPoints], cast2 = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            cast1[i] = (int) xPoints[i];
            cast2[i] = (int) yPoints[i];
        }
        gc.drawPolyline(cast1, cast2, nPoints);
    }

    @Override
    public void fillRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {
        gc.setPaint(new Color((int) (fill.getRed() * 255), (int) (fill.getGreen() * 255),
                (int) (fill.getBlue() * 255), (int) (fill.getOpacity() * 255)));
        gc.fillRoundRect((int) x, (int) y, (int) w, (int) h, (int) arcWidth, (int) arcHeight);
    }

    @Override
    public void strokeRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {
        float[] cast = new float[lineDashes.length];
        for (int i = 0; i < cast.length; i++) {
            cast[i] = (float) lineDashes[i];
        }
        gc.setStroke(new BasicStroke((float) strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
                cast.length == 0 ? null : cast, 0));
        gc.setPaint(new Color((int) (stroke.getRed() * 255), (int) (stroke.getGreen() * 255),
                (int) (stroke.getBlue() * 255), (int) (stroke.getOpacity() * 255)));
        gc.drawRoundRect((int) x, (int) y, (int) w, (int) h, (int) arcWidth, (int) arcHeight);
    }

    @Override
    public void clearRect(double x, double y, double w, double h) {
        // Do nothing
    }

    private void clearRect() {
        gc.clearRect(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void drawBackground(javafx.scene.paint.Color backgroundColor) {
        gc.setBackground(new Color((int) (backgroundColor.getRed() * 255), (int) (backgroundColor.getGreen() * 255),
                (int) (backgroundColor.getBlue() * 255), (int) (backgroundColor.getOpacity() * 255)));
        clearRect();
    }

    public String getSVGDocument() {
        return gc.getSVGDocument();
    }

    public void saveAsSVG(File file) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
                StandardCharsets.UTF_8))) {
            writer.write(gc.getSVGDocument());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
