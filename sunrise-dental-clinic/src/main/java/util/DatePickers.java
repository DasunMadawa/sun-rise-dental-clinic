package util;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickers {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void applyFormat(DatePicker picker) {
        picker.setPromptText("dd/MM/yyyy");
        picker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date == null ? "" : FORMAT.format(date);
            }

            @Override
            public LocalDate fromString(String text) {
                return (text == null || text.isBlank()) ? null : LocalDate.parse(text, FORMAT);
            }
        });
    }

}
