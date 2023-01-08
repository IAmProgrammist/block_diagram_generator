package rchat.info.blockdiagramgenerator.models.bdelements;

import javafx.geometry.Dimension2D;
import javafx.util.Pair;

import java.util.List;

public abstract class BDElementModel {
    static enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public List<String> data;
    protected Dimension2D size;
    protected double leftBound;
    protected double rightBound;

    public BDElementModel(List<String> data) {
        this.data = data;
    }

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

    public void setMeasurements(Dimension2D size, double leftBound, double rightBound) {
        this.size = size;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public abstract Pair<Double, Double> getLeftConnector(Pair<Double, Double> pos);

    public abstract Pair<Double, Double> getRightConnector(Pair<Double, Double> pos);

    public abstract Pair<Double, Double> getTopConnector(Pair<Double, Double> pos);

    public abstract Pair<Double, Double> getBottomConnector(Pair<Double, Double> pos);
}
