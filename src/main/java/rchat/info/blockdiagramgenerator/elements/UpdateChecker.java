package rchat.info.blockdiagramgenerator.elements;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.json.JSONObject;
import rchat.info.blockdiagramgenerator.Main;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;

public class UpdateChecker {

    public static void checkForUpdates(String currentVersionTag) {
        try {
            HttpURLConnection con = (HttpURLConnection)
                    new URL("https://api.github.com/repos/IAmProgrammist/block_diagram_generator/releases/latest")
                            .openConnection();
            con.setRequestMethod("GET");

            StringBuilder content = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(),
                    StandardCharsets.UTF_8));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                content.append(inputLine);
            }

            reader.close();

            JSONObject latestRelease = new JSONObject(content.toString());

            if (!currentVersionTag.equals(latestRelease.getString("tag_name"))) {
                ResourceBundle rb = ResourceBundle.getBundle("rchat/info/blockdiagramgenerator/bundles/languages");

                ButtonType yes = new ButtonType(rb.getString("update_available_yes"), ButtonBar.ButtonData.YES);
                ButtonType no = new ButtonType(rb.getString("update_available_no"), ButtonBar.ButtonData.NO);
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        rb.getString("update_available_content"),
                        yes,
                        no);

                alert.setTitle(rb.getString("update_available_title"));
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get().getButtonData() == ButtonType.YES.getButtonData()) {
                    Desktop.getDesktop().browse(new URI(latestRelease.getString("html_url")));
                }
            }
        } catch (URISyntaxException | IOException ignored) {
        }
    }
}
