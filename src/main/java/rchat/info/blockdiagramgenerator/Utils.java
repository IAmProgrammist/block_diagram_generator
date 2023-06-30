package rchat.info.blockdiagramgenerator;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.util.Pair;

import java.awt.*;

public class Utils {
    public static Dimension2D computeTextWidth(Font font, String text) {
        Text helper = new Text(text);
        helper.setBoundsType(TextBoundsType.VISUAL);
        helper.setFont(font);
        //helper.setWrappingWidth(0);
        //helper.setLineSpacing(0);
        //double w = helper.prefWidth(-1);
        //helper.setWrappingWidth((int)Math.ceil(w));
        Bounds bound = helper.getLayoutBounds();
        return new Dimension2D(Math.ceil(bound.getWidth()), Math.ceil(bound.getHeight()));
    }

    public static boolean isPointInBounds(Pair<Double, Double> pointPos, Pair<Double, Double> leftTopBoxPoint,
                                   Dimension2D boxSize) {
        return pointPos.getKey() > leftTopBoxPoint.getKey() &&
               pointPos.getKey() < leftTopBoxPoint.getKey() + boxSize.getWidth() &&
               pointPos.getValue() > leftTopBoxPoint.getValue() &&
               pointPos.getValue() < leftTopBoxPoint.getValue() + boxSize.getHeight();
    }
}
