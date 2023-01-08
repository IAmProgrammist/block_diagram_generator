package rchat.info.blockdiagramgenerator.models.bdelements;

import javafx.geometry.Dimension2D;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;

import java.util.Arrays;

public class BDCycleNotFixedModel extends BDElementModel {
    public BDContainerController body;
    public Dimension2D rhombusSize;
    public Dimension2D rhombusTextSize;

    public BDCycleNotFixedModel(String data, BDContainerController body) {
        super(Arrays.asList(data.split("\n")));
        this.body = body;
    }

    @Override
    public Pair<Double, Double> getLeftConnector(Pair<Double, Double> pos) {
        throw new RuntimeException();
    }

    @Override
    public Pair<Double, Double> getRightConnector(Pair<Double, Double> pos) {
        throw new RuntimeException();
    }

    @Override
    public Pair<Double, Double> getTopConnector(Pair<Double, Double> pos) {
        return new Pair<>(pos.getKey() + getDistanceToLeftBound(), pos.getValue());
    }

    public Pair<Double, Double> getLeftRhombusConnector(Pair<Double, Double> pos) {
        Dimension2D size = getRhombusSize();
        return new Pair<>(pos.getKey(), pos.getValue() + size.getHeight() / 2);
    }

    public Pair<Double, Double> getRightRhombusConnector(Pair<Double, Double> pos) {
        Dimension2D size = getRhombusSize();
        return new Pair<>(pos.getKey() + size.getWidth(), pos.getValue() + size.getHeight() / 2);
    }

    public Pair<Double, Double> getBottomRhombusConnector(Pair<Double, Double> pos) {
        Dimension2D size = getRhombusSize();
        return new Pair<>(pos.getKey() + size.getWidth() / 2, pos.getValue() + size.getHeight());
    }

    public Pair<Double, Double> getTopRhombusConnector(Pair<Double, Double> pos) {
        Dimension2D size = getRhombusSize();
        return new Pair<>(pos.getKey() + size.getWidth() / 2, pos.getValue());
    }

    @Override
    public Pair<Double, Double> getBottomConnector(Pair<Double, Double> pos) {
        return new Pair<>(pos.getKey() + getDistanceToLeftBound(), pos.getValue() + getSize().getHeight());
    }

    public void setMeasurements(Dimension2D size, double leftBound, double rightBound,
                                Dimension2D rhombusSize, Dimension2D rhombusTextSize) {
        super.setMeasurements(size, leftBound, rightBound);
        this.rhombusSize = rhombusSize;
        this.rhombusTextSize = rhombusTextSize;
    }

    public Dimension2D getRhombusSize() {
        return rhombusSize;
    }

    public Dimension2D getRhombusTextSize() {
        return rhombusTextSize;
    }
}
