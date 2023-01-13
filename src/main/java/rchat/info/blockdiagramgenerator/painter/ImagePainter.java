package rchat.info.blockdiagramgenerator.painter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Arrays;

public class ImagePainter extends AbstractPainter {
    private static final double INCH_2_CM = 2.54;
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private double scale;
    public static final String DENSITY_UNITS_NO_UNITS = "00";
    public static final String DENSITY_UNITS_PIXELS_PER_INCH = "01";
    public static final String DENSITY_UNITS_PIXELS_PER_CM = "02";
    // When scale equals 1.0
    private static final double DPI_DEFAULT = 72;
    public ImagePainter(double originWidth, double originHeight,
                        double scale) {
        this.canvas = new Canvas(originWidth * scale, originHeight * scale);
        this.graphicsContext = this.canvas.getGraphicsContext2D();
        this.scale = scale;
    }

    @Override
    public void setFill(Color p) {
        graphicsContext.setFill(p);
    }

    @Override
    public void setLineDashes(double... dashes) {
        graphicsContext.setLineDashes(dashes);
    }

    @Override
    public void setStroke(Color p) {
        graphicsContext.setStroke(p);
    }

    @Override
    public void setStrokeWidth(double lw) {
        graphicsContext.setLineWidth(lw);
    }

    @Override
    public void setFont(Font f) {
        Font fixedFont = new Font(f.getName(), f.getSize() * scale);
        graphicsContext.setFont(fixedFont);
    }

    @Override
    public void setLineWidth(double lineWidth) {
        graphicsContext.setLineWidth(lineWidth);
    }

    @Override
    public void fillRect(double x, double y, double width, double height) {
        graphicsContext.fillRect(x * scale, y * scale, width * scale, height * scale);
    }

    @Override
    public void strokeRect(double x, double y, double width, double height) {
        graphicsContext.strokeRect(x * scale, y * scale, width * scale, height * scale);
    }

    @Override
    public void fillText(String text, double x, double y) {
        graphicsContext.fillText(text, x * scale, y * scale);
    }

    @Override
    public void strokeLine(double x1, double y1, double x2, double y2) {
        graphicsContext.strokeLine(x1 * scale, y1 * scale, x2 * scale, y2 * scale);
    }

    @Override
    public void fillPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        graphicsContext.fillPolygon(Arrays.stream(xPoints).map(el -> el * scale).toArray(),
                Arrays.stream(yPoints).map(el -> el * scale).toArray(), nPoints);
    }

    @Override
    public void strokePolygon(double[] xPoints, double[] yPoints, int nPoints) {
        graphicsContext.strokePolygon(Arrays.stream(xPoints).map(el -> el * scale).toArray(),
                Arrays.stream(yPoints).map(el -> el * scale).toArray(), nPoints);
    }

    @Override
    public void strokePolyline(double[] xPoints, double[] yPoints, int nPoints) {
        graphicsContext.strokePolyline(Arrays.stream(xPoints).map(el -> el * scale).toArray(),
                Arrays.stream(yPoints).map(el -> el * scale).toArray(), nPoints);
    }

    @Override
    public void fillRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {
        graphicsContext.fillRoundRect(x * scale, y * scale, w * scale, h * scale,
                arcWidth * scale, arcHeight * scale);
    }

    @Override
    public void strokeRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {
        graphicsContext.strokeRoundRect(x * scale, y * scale, w * scale, h * scale,
                arcWidth * scale, arcHeight * scale);
    }

    @Override
    public void clearRect(double x, double y, double w, double h) {
        graphicsContext.clearRect(x * scale, y * scale, w * scale, h * scale);
    }

    @Override
    public void drawBackground(Color backgroundColor) {
        graphicsContext.setFill(backgroundColor);
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
    }

    public boolean saveAsPNG(File writeDirectory) {
        try {
            WritableImage bufferedImage = canvas.snapshot(null, null);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(bufferedImage, null);
            return ImageIO.write(
                    renderedImage,
                    "png",
                    writeDirectory);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveAsJPG(File writeDirectory) {
        try {
            WritableImage bufferedImage = canvas.snapshot(null, null);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(bufferedImage, null);
            return ImageIO.write(
                    renderedImage,
                    "jpg",
                    writeDirectory);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setPngDPI(IIOMetadata metadata) throws IIOInvalidTreeException {

        // for PMG, it's dots per millimeter
        double dotsPerMilli = this.scale * DPI_DEFAULT / 10 / INCH_2_CM;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }

    private void setJpegDPI(IIOMetadata metadata) throws IIOInvalidTreeException {
        String metadataFormat = "javax_imageio_jpeg_image_1.0";
        IIOMetadataNode root = new IIOMetadataNode(metadataFormat);
        IIOMetadataNode jpegVariety = new IIOMetadataNode("JPEGvariety");
        IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");

        IIOMetadataNode app0JFIF = new IIOMetadataNode("app0JFIF");
        app0JFIF.setAttribute("majorVersion", "1");
        app0JFIF.setAttribute("minorVersion", "2");
        app0JFIF.setAttribute("thumbWidth", "0");
        app0JFIF.setAttribute("thumbHeight", "0");
        app0JFIF.setAttribute("resUnits", DENSITY_UNITS_PIXELS_PER_INCH);
        app0JFIF.setAttribute("Xdensity", String.valueOf((int) (DPI_DEFAULT * this.scale)));
        app0JFIF.setAttribute("Ydensity", String.valueOf((int) (DPI_DEFAULT * this.scale)));

        root.appendChild(jpegVariety);
        root.appendChild(markerSequence);
        jpegVariety.appendChild(app0JFIF);

        metadata.mergeTree(metadataFormat, root);
    }


}
