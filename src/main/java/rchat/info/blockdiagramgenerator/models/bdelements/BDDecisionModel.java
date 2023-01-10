package rchat.info.blockdiagramgenerator.models.bdelements;

import javafx.geometry.Dimension2D;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDContainerController;

import java.util.Arrays;

public class BDDecisionModel extends BDElementModel {
    public BDContainerController positive;
    public BDDecisionModel.Branch positiveBranch;
    public BDContainerController negative;
    public BDDecisionModel.Branch negativeBranch;
    public Dimension2D rhombusSize;
    public Dimension2D rhombusTextSize;

    public static enum Branch {
        LEFT("dec_left"),
        CENTER("dec_center"),
        RIGHT("dec_right");

        public final String propName;
        Branch(String propName) {
            this.propName = propName;
        }
    }

    public BDDecisionModel(BDContainerController positive, BDDecisionModel.Branch positiveBranch, 
                           BDContainerController negative, BDDecisionModel.Branch negativeBranch,
                      String data) {
        super(Arrays.asList(data.split("\n")));

        if (positiveBranch == negativeBranch)
            throw new IllegalArgumentException("Branches can't be the same");

        this.positive = positive;
        this.positiveBranch = positiveBranch;
        this.negative = negative;
        this.negativeBranch = negativeBranch;
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

    public Dimension2D getRhombusSize() {
        return rhombusSize;
    }

    public Dimension2D getRhombusTextSize() {
        return rhombusTextSize;
    }

    public void setMeasurements(Dimension2D size, double leftBound, double rightBound,
                                Dimension2D rhombusSize, Dimension2D rhombusTextSize) {
        super.setMeasurements(size, leftBound, rightBound);
        this.rhombusSize = rhombusSize;
        this.rhombusTextSize = rhombusTextSize;
    }
}
