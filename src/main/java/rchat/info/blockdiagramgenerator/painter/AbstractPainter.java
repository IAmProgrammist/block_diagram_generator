package rchat.info.blockdiagramgenerator.painter;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class AbstractPainter {
    protected Color fill = Color.BLACK;
    protected Color stroke = Color.BLACK;
    protected double strokeWidth = 1;
    protected double[] lineDashes = new double[]{};
    protected Font font = Font.getDefault();

    public void setFill(Color p) {
        this.fill = p;
    }

    public void setLineDashes(double... dashes) {
        this.lineDashes = dashes;
    }

    public void setStroke(Color p) {
        this.stroke = p;
    }

    public void setStrokeWidth(double lw) {
        this.strokeWidth = lw;
    }

    public void setFont(Font f) {
        this.font = f;
    }

    public void setLineWidth(double lineWidth) {
        this.strokeWidth = lineWidth;
    }

    public abstract void fillRect(double x, double y, double width, double height);

    public abstract void strokeRect(double x, double y, double width, double height);

    public abstract void fillText(String text, double x, double y);

    public abstract void strokeLine(double x1, double y1, double x2, double y2);

    public abstract void fillPolygon(double xPoints[], double yPoints[], int nPoints);

    public abstract void strokePolygon(double xPoints[], double yPoints[], int nPoints);

    public abstract void strokePolyline(double xPoints[], double yPoints[], int nPoints);

    public abstract void fillRoundRect(double x, double y, double w, double h,
                                       double arcWidth, double arcHeight);

    public abstract void strokeRoundRect(double x, double y, double w, double h,
                                         double arcWidth, double arcHeight);

    public abstract void clearRect(double x, double y, double w, double h);

    public abstract void drawBackground(Color backgroundColor);
}
