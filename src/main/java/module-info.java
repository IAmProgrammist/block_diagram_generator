module rchat.info.blockdiagramgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens rchat.info.blockdiagramgenerator to javafx.fxml;
    exports rchat.info.blockdiagramgenerator;
}