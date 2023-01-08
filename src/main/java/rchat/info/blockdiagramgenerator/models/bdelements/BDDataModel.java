package rchat.info.blockdiagramgenerator.models.bdelements;

import javafx.geometry.Dimension2D;
import javafx.util.Pair;

import java.util.Arrays;

public class BDDataModel extends BDElementModel {
    public BDDataModel(String data) {
        super(Arrays.asList(data.split("\n")));
    }
    @Override
    public Pair<Double, Double> getLeftConnector(Pair<Double, Double> pos) {
        Dimension2D size = getSize();
        return new Pair<>(pos.getKey(), pos.getValue() + size.getHeight() / 2);
    }

    @Override
    public Pair<Double, Double> getRightConnector(Pair<Double, Double> pos) {
        Dimension2D size = getSize();
        return new Pair<>(pos.getKey() + size.getWidth(), pos.getValue() + size.getHeight() / 2);
    }

    @Override
    public Pair<Double, Double> getTopConnector(Pair<Double, Double> pos) {
        Dimension2D size = getSize();
        return new Pair<>(pos.getKey() + size.getWidth() / 2, pos.getValue());
    }

    @Override
    public Pair<Double, Double> getBottomConnector(Pair<Double, Double> pos) {
        Dimension2D size = getSize();
        return new Pair<>(pos.getKey() + size.getWidth() / 2, pos.getValue() + size.getHeight());
    }


}
