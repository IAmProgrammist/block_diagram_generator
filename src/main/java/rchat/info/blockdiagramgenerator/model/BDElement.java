package rchat.info.blockdiagramgenerator.model;

import javafx.geometry.Dimension2D;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.DiagramBlockController;

import java.util.List;

public abstract class BDElement {
    static enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }

    List<String> data;
    DiagramBlockController context;
    protected Dimension2D size;
    protected double leftBound;
    protected double rightBound;

    public BDElement(DiagramBlockController context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    public abstract void drawElement(Pair<Double, Double> drawPoint);

    public Dimension2D getSize() {
        return size;
    }

    //returns a distance between top connector and left bound
    public double getDistanceToLeftBound() {
        return leftBound;
    }

    //returns a distance between top connector and right bound
    public double getDistanceToRightBound() {
        return rightBound;
    }

    // This method should be called every time data changes. Recalculates field size,
    // leftBorder (distance from topconnector to left border) and rightBorder
    // (distance from topconnector to right border) from data
    public abstract void recalculateSizes();

    protected void recalculateSizes(Dimension2D size, double leftBound, double rightBound) {
        this.size = size;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public abstract Pair<Double, Double> getLeftConnector(Pair<Double, Double> pos);

    public abstract Pair<Double, Double> getRightConnector(Pair<Double, Double> pos);

    public abstract Pair<Double, Double> getTopConnector(Pair<Double, Double> pos);

    public abstract Pair<Double, Double> getBottomConnector(Pair<Double, Double> pos);
}
