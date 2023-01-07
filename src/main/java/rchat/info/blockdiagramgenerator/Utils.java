package rchat.info.blockdiagramgenerator;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Utils {
    public static Dimension2D computeTextWidth(Font font, String text) {
        Text helper = new Text();
        helper.setFont(font);
        helper.setText(text);
        helper.setWrappingWidth(0);
        helper.setLineSpacing(0);
        double w = helper.prefWidth(-1);
        helper.setWrappingWidth((int)Math.ceil(w));
        Bounds bound = helper.getLayoutBounds();
        return new Dimension2D(Math.ceil(bound.getWidth()), Math.ceil(bound.getHeight()));
    }
}
