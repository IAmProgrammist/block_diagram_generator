package rchat.info.blockdiagramgenerator.painter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CanvasPainter extends AbstractPainter {
    GraphicsContext graphicsContext;

    public CanvasPainter(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
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
        graphicsContext.setFont(f);
    }

    @Override
    public void setLineWidth(double lineWidth) {
        graphicsContext.setLineWidth(lineWidth);
    }

    @Override
    public void fillRect(double x, double y, double width, double height) {
        graphicsContext.fillRect(x, y, width, height);
    }

    @Override
    public void strokeRect(double x, double y, double width, double height) {
        graphicsContext.strokeRect(x, y, width, height);
    }

    @Override
    public void fillText(String text, double x, double y) {
        graphicsContext.fillText(text, x, y);
    }

    @Override
    public void strokeLine(double x1, double y1, double x2, double y2) {
        graphicsContext.strokeLine(x1, y1, x2, y2);
    }

    @Override
    public void fillPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        graphicsContext.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void strokePolygon(double[] xPoints, double[] yPoints, int nPoints) {
        graphicsContext.strokePolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void strokePolyline(double[] xPoints, double[] yPoints, int nPoints) {
        graphicsContext.strokePolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {
        graphicsContext.fillRoundRect(x, y, w, h, arcWidth, arcHeight);
    }

    @Override
    public void strokeRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {
        graphicsContext.strokeRoundRect(x, y, w, h, arcWidth, arcHeight);
    }

    @Override
    public void clearRect(double x, double y, double w, double h) {
        graphicsContext.clearRect(x, y, w, h);
    }

    @Override
    public void drawBackground(Color backgroundColor) {
        graphicsContext.setFill(backgroundColor);
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
    }
}
