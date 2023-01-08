module rchat.info.blockdiagramgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens rchat.info.blockdiagramgenerator to javafx.fxml;
    exports rchat.info.blockdiagramgenerator;
    exports rchat.info.blockdiagramgenerator.models.bdelements;
    opens rchat.info.blockdiagramgenerator.models.bdelements to javafx.fxml;
    exports rchat.info.blockdiagramgenerator.controllers;
    opens rchat.info.blockdiagramgenerator.controllers to javafx.fxml;
    exports rchat.info.blockdiagramgenerator.models;
    opens rchat.info.blockdiagramgenerator.models to javafx.fxml;
    exports rchat.info.blockdiagramgenerator.views;
    opens rchat.info.blockdiagramgenerator.views to javafx.fxml;
    exports rchat.info.blockdiagramgenerator.views.bdelements;
    opens rchat.info.blockdiagramgenerator.views.bdelements to javafx.fxml;
}