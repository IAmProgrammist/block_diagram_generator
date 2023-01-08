package rchat.info.blockdiagramgenerator.models.bdelements;

import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;

import java.util.Arrays;

public class BDProcessModel extends BDElementModel {
    public BDProcessModel(String data) {
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
