package util;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.paint.Paint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {
    public static final Pattern namePattern = Pattern.compile("^[a-zA-Z '.-]{3,}$");
    public static final Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    public static final Pattern datePattern = Pattern.compile("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$");
    public static final Pattern mobilePattern = Pattern.compile("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$");
    public static final Pattern nicPattern = Pattern.compile("^([0-9]{9}[vVxX]|[0-9]{12})$");
    public static final Pattern timePattern = Pattern.compile("^([01]\\d|2[0-3]):([0-5]\\d)$");

    public static final Pattern patientPattern = Pattern.compile("^(PT)[0-9]{3}$");
    public static final Pattern dentistPattern = Pattern.compile("^(DEN)[0-9]{3}$");
    public static final Pattern staffPattern = Pattern.compile("^(STF)[0-9]{3}$");
    public static final Pattern appointmentPattern = Pattern.compile("^(APT)[0-9]{4}$");

    public static final Pattern doublePattern62 = Pattern.compile("^[0-9]{1,6}+($|\\.[0-9]{1,2})$");
    public static final Pattern intPattern2 = Pattern.compile("^[1-9]{1}+[0-9]{0,1}$");


    public static void setFocus(JFXTextField textField, Pattern pattern) {
        textField.setOnKeyReleased(keyEvent -> {
            Matcher matcher = pattern.matcher(textField.getText());

            if (textField.getText().isEmpty() || textField.getText().isBlank() || !matcher.matches()) {
                textField.setFocusColor(Paint.valueOf("red"));
                textField.setUnFocusColor(Paint.valueOf("red"));
            } else {
                textField.setFocusColor(Paint.valueOf("blue"));
                textField.setUnFocusColor(Paint.valueOf("blue"));
            }

        });
    }
}
