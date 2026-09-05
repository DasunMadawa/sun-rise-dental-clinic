package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UserModel;
import util.OtpService;
import util.PasswordUtil;
import util.mail.MailService;

public class ForgotPasswordFormController {

    @FXML
    public StackPane root;

    @FXML
    private VBox requestPane;

    @FXML
    private VBox resetPane;

    @FXML
    private JFXTextField usernameOrEmailTxt;

    @FXML
    private JFXButton sendOtpBtn;

    @FXML
    private Label maskedEmailLbl;

    @FXML
    private JFXTextField otpTxt;

    @FXML
    private JFXPasswordField newPasswordTxt;

    @FXML
    private JFXPasswordField confirmPasswordTxt;

    @FXML
    private JFXButton resetPasswordBtn;

    String pendingUserId;

    @FXML
    public void initialize() {
        resetPane.setVisible(false);
        resetPane.setManaged(false);
    }

    @FXML
    void sendOtpBtnOnAction(ActionEvent event) {
        try {
            if (usernameOrEmailTxt.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Enter your username or email").show();
                return;
            }

            UserModel user = UserModel.search(usernameOrEmailTxt.getText());
            if (user == null) {
                new Alert(Alert.AlertType.ERROR, "No account found for that username or email").show();
                return;
            }

            if (user.getEmail() == null || user.getEmail().isBlank()) {
                new Alert(Alert.AlertType.ERROR, "No email on file for this account. Contact your manager.").show();
                return;
            }

            String otp = OtpService.generate(user.getUserID());
            MailService.sendAsync(
                    user.getEmail(),
                    "Sunrise Dental Clinic - Password Reset OTP",
                    "Your one-time password reset code is: " + otp + "\n\nThis code expires in 5 minutes.\n\nIf you did not request this, you can safely ignore this email."
            );

            pendingUserId = user.getUserID();
            maskedEmailLbl.setText("OTP sent to " + mask(user.getEmail()));

            requestPane.setVisible(false);
            requestPane.setManaged(false);
            resetPane.setVisible(true);
            resetPane.setManaged(true);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could Not Send OTP !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void resetPasswordBtnOnAction(ActionEvent event) {
        try {
            if (otpTxt.getText().isEmpty() || newPasswordTxt.getText().isEmpty() || confirmPasswordTxt.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Enter valid Data").show();
                return;
            }

            if (!newPasswordTxt.getText().equals(confirmPasswordTxt.getText())) {
                new Alert(Alert.AlertType.ERROR, "Passwords do not match").show();
                return;
            }

            if (!OtpService.verify(pendingUserId, otpTxt.getText())) {
                new Alert(Alert.AlertType.ERROR, "Invalid or expired OTP").show();
                return;
            }

            UserModel.resetPassword(pendingUserId, PasswordUtil.hash(newPasswordTxt.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Password reset. Please log in with your new password.", ButtonType.OK).show();
            backToLoginBtnOnAction(event);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could Not Reset Password !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void backToLoginBtnOnAction(ActionEvent event) {
        try {
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/login_form.fxml"))));
            primaryStage.setTitle("Sunrise Dental Clinic");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String mask(String email) {
        int at = email.indexOf('@');
        if (at <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(at - 1);
    }

}
