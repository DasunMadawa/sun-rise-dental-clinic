package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimePicker extends HBox {
    private final ComboBox<String> hourBox = new ComboBox<>();
    private final ComboBox<String> minuteBox = new ComboBox<>();

    public TimePicker() {
        setSpacing(4);
        setAlignment(Pos.CENTER_LEFT);

        List<String> hours = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            hours.add(String.format("%02d", h));
        }
        hourBox.setItems(FXCollections.observableArrayList(hours));
        hourBox.setPrefWidth(75);
        hourBox.getStyleClass().add("combo-box");

        List<String> minutes = new ArrayList<>();
        for (int m = 0; m < 60; m += 5) {
            minutes.add(String.format("%02d", m));
        }
        minuteBox.setItems(FXCollections.observableArrayList(minutes));
        minuteBox.setPrefWidth(75);
        minuteBox.getStyleClass().add("combo-box");

        getChildren().addAll(hourBox, new Label(":"), minuteBox);
    }

    public LocalTime getValue() {
        String h = hourBox.getValue();
        String m = minuteBox.getValue();
        if (h == null || m == null) {
            return null;
        }
        return LocalTime.of(Integer.parseInt(h), Integer.parseInt(m));
    }

    public void setValue(LocalTime time) {
        if (time == null) {
            hourBox.setValue(null);
            minuteBox.setValue(null);
            return;
        }
        hourBox.setValue(String.format("%02d", time.getHour()));
        int minute = (time.getMinute() / 5) * 5;
        minuteBox.setValue(String.format("%02d", minute));
    }

}
