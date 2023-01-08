package rchat.info.blockdiagramgenerator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), DiagramBlockModel.canvasWidth, DiagramBlockModel.canvasHeight);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
