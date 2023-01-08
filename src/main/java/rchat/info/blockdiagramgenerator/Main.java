package rchat.info.blockdiagramgenerator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;

import java.io.IOException;

public class Main extends Application {
    final long[] frameTimes = new long[100];
    int frameTimeIndex = 0;
    boolean arrayFilled = false;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("layouts/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), DiagramBlockModel.canvasWidth, DiagramBlockModel.canvasHeight);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        if (DiagramBlockModel.IS_DEBUG_MODE_ENABLED) {
            if (DiagramBlockModel.IS_DEBUG_SHOW_FPS) {
                AnimationTimer frameRateMeter = new AnimationTimer() {

                    @Override
                    public void handle(long now) {
                        long oldFrameTime = frameTimes[frameTimeIndex];
                        frameTimes[frameTimeIndex] = now;
                        frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                        if (frameTimeIndex == 0) {
                            arrayFilled = true;
                        }
                        if (arrayFilled) {
                            long elapsedNanos = now - oldFrameTime;
                            long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                            double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
                            stage.setTitle(String.format("Current frame rate: %.3f", frameRate));
                        }
                    }
                };

                frameRateMeter.start();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
