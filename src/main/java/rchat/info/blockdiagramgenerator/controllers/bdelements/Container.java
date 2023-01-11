package rchat.info.blockdiagramgenerator.controllers.bdelements;

public interface Container {

    void removeFromContainer(BDElementController bdElementController);

    void replaceInContainer(BDElementController replacing, BDElementController replacer);
}
