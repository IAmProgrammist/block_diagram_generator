module rchat.info.blockdiagramgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.jfree.svg;
    requires java.desktop;
    requires org.controlsfx.controls;
    requires org.json;
    requires java.prefs;

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
    opens rchat.info.blockdiagramgenerator.elements to javafx.fxml;
    exports rchat.info.blockdiagramgenerator.elements;
    opens rchat.info.blockdiagramgenerator.painter to java.desktop;
}