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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.UserModel;

public class LoginFormController {
    private static final int MAX_ATTEMPTS = 3;
    private static int failedAttempts = 0;

    @FXML
    public StackPane root;

    @FXML
    private JFXTextField userNameTxt;

    @FXML
    private JFXPasswordField passwordTxt;

    @FXML
    private JFXButton loginBtn;

    @FXML
    void loginBtnOnAction(ActionEvent event) {
        if (failedAttempts >= MAX_ATTEMPTS) {
            new Alert(Alert.AlertType.ERROR, "Too many failed attempts. Session locked. Restart the application.", ButtonType.OK).show();
            loginBtn.setDisable(true);
            return;
        }

        try {
            UserModel user = UserModel.search(userNameTxt.getText());

            if (user == null || !user.checkPassword(passwordTxt.getText())) {
                failedAttempts++;
                new Alert(Alert.AlertType.ERROR, "Invalid username or password. Attempts remaining: " + (MAX_ATTEMPTS - failedAttempts), ButtonType.OK).show();
                return;
            }

            if (!user.isActive()) {
                new Alert(Alert.AlertType.ERROR, "This account is disabled. Contact the manager.", ButtonType.OK).show();
                return;
            }

            failedAttempts = 0;
            DashboardFormController.currentUser = user;

            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"))));
            primaryStage.setTitle("Sunrise Dental Clinic");
            primaryStage.show();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Login Failed !", ButtonType.OK).show();
            e.printStackTrace();
        }

    }

    @FXML
    void passwordTxtOnAction(ActionEvent event) {
        loginBtnOnAction(event);
    }

    @FXML
    void userNameTxtOnAction(ActionEvent event) {
        loginBtnOnAction(event);
    }

    @FXML
    void forgotPasswordBtnOnAction(ActionEvent event) {
        try {
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/forgot_password_form.fxml"))));
            primaryStage.setTitle("Sunrise Dental Clinic");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
