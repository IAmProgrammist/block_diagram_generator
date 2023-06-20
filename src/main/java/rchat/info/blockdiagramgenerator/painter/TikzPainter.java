package rchat.info.blockdiagramgenerator.painter;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.Map;

public class TikzPainter extends AbstractPainter {
    StringBuilder result = new StringBuilder();
    double width;
    double height;
    private Map<Color, String> colors;
    private int colorCount = 0;
    private List<DrawCommand> drawCommands;

    abstract class DrawCommand {

    }

    class Rectangle extends DrawCommand {

    }

    class FillRectangle extends DrawCommand {

    }

    public TikzPainter(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void fillRect(double x, double y, double width, double height) {

    }

    @Override
    public void strokeRect(double x, double y, double width, double height) {

    }

    @Override
    public void fillText(String text, double x, double y) {

    }

    @Override
    public void strokeLine(double x1, double y1, double x2, double y2) {

    }

    @Override
    public void fillPolygon(double[] xPoints, double[] yPoints, int nPoints) {

    }

    @Override
    public void strokePolygon(double[] xPoints, double[] yPoints, int nPoints) {

    }

    @Override
    public void strokePolyline(double[] xPoints, double[] yPoints, int nPoints) {

    }

    @Override
    public void fillRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {

    }

    @Override
    public void strokeRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {

    }

    @Override
    public void clearRect(double x, double y, double w, double h) {

    }

    @Override
    public void drawBackground(Color backgroundColor) {

    }

    private void saveColor(Color p) {
        if (colors.containsKey(p))
            colors.put(p, "color" + colorCount++);
    }

    private String getColor(Color p) {
        if (colors.containsKey(p))
            return colors.get(p);

        saveColor(p);
        return colors.get(p);
    }

    @Override
    public void setFill(Color p) {
        super.setFill(p);

        saveColor(p);
    }

    // TODO: add dashing line support later
    @Override
    public void setLineDashes(double... dashes) {
        super.setLineDashes(dashes);
    }

    @Override
    public void setStroke(Color p) {
        super.setStroke(p);

        saveColor(p);
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

    public String save() {
        return "";
    }
}
