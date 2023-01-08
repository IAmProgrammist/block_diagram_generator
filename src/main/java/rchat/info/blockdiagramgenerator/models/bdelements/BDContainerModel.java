package rchat.info.blockdiagramgenerator.models.bdelements;

import javafx.geometry.Dimension2D;
import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.controllers.bdelements.BDElementController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BDContainerModel extends BDElementModel {
    public List<BDElementController> elements = new ArrayList<>();
    public BDContainerModel() {
        super(Collections.emptyList());
    }

    public BDContainerModel(BDElementController... elements) {
        super(Collections.emptyList());
        this.elements.addAll(Arrays.asList(elements));
    }

    @Override
    public Pair<Double, Double> getLeftConnector(Pair<Double, Double> pos) {
        throw new RuntimeException("Left connector is not available in container!");
    }

    // TODO: Rework as well!
    @Override
    public Pair<Double, Double> getRightConnector(Pair<Double, Double> pos) {
        throw new RuntimeException("Right connector is not available in container!");
    }

    @Override
    public Pair<Double, Double> getTopConnector(Pair<Double, Double> pos) {
        double maxLeftBound = 0;
        for (BDElementController element : elements) {
            double currLeftBound;
            if ((currLeftBound = element.getModel().getDistanceToLeftBound()) > maxLeftBound) {
                maxLeftBound = currLeftBound;
            }
        }

        return new Pair<>(pos.getKey() + maxLeftBound, pos.getValue());
    }

    @Override
    public Pair<Double, Double> getBottomConnector(Pair<Double, Double> pos) {
        if (elements.size() > 0) {
            double maxLeftBound = 0;
            double height = 0;
            for (BDElementController element : elements) {
                Dimension2D elementSize = element.getModel().getSize();
                double currLeftBound;
                if ((currLeftBound = element.getModel().getDistanceToLeftBound()) > maxLeftBound) {
                    maxLeftBound = currLeftBound;
                }
                height += elementSize.getHeight() + DiagramBlockModel.ELEMENTS_SPACING;
            }

            height -= DiagramBlockModel.ELEMENTS_SPACING;
            return new Pair<>(pos.getKey() + maxLeftBound, pos.getValue() + height);
        }
        return pos;
    }
}
