package rchat.info.blockdiagramgenerator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.dialog.FontSelectorDialog;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    final long[] frameTimes = new long[100];
    int frameTimeIndex = 0;
    boolean arrayFilled = false;

    public static EventHandler<KeyEvent> rewriteKeyPressedEvent;
    public static ResourceBundle rb = null;
    public static Stage stage = null;

    @Override
    public void start(Stage stage) throws IOException {
        Style.init();
        this.stage = stage;

        Style currentStyle = Style.getCurrentStyle();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("layouts/hello-view.fxml"));
        rb = ResourceBundle.getBundle("rchat/info/blockdiagramgenerator/bundles/languages");
        fxmlLoader.setResources(rb);
        Scene scene = new Scene(fxmlLoader.load());
        scene.setOnKeyPressed(event -> {
            if (rewriteKeyPressedEvent != null) {
                rewriteKeyPressedEvent.handle(event);
            }
        });
        stage.setScene(scene);
        stage.show();
        if (currentStyle.isDebugModeEnabled()) {
            if (currentStyle.isDebugShowFps()) {
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
