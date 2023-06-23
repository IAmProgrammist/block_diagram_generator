package rchat.info.blockdiagramgenerator.controllers;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class TextareaUtils {
    public static TextFormatter<Integer> uIntTextFormatter() {
        Pattern validEditingState = Pattern.compile("(^$)|(\\d+)");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        StringConverter<Integer> converter = new StringConverter<>() {

            @Override
            public Integer fromString(String s) {
                if (s.isEmpty()) {
                    return 1;
                } else {
                    return Integer.valueOf(s);
                }
            }


            @Override
            public String toString(Integer d) {
                return String.format("%d", d);
            }
        };

        return new TextFormatter<>(converter, 0, filter);
    }
}
