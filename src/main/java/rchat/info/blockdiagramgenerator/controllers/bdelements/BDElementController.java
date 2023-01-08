package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.util.Pair;
import rchat.info.blockdiagramgenerator.models.bdelements.BDElementModel;

public abstract class BDElementController {
    public abstract void update(Pair<Double, Double> position);

    public abstract void recalculateSizes();

    public abstract BDElementModel getModel();
}
