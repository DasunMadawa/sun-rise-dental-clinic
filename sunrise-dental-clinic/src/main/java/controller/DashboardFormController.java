package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.UserModel;

import java.io.IOException;

public class DashboardFormController {
    public static UserModel currentUser;

    @FXML
    private VBox menuBox;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label welcomeLbl;

    JFXButton activeMenuBtn;

    @FXML
    public void initialize() {
        welcomeLbl.setText(currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        buildMenu();
        loadContent("Dashboard", null);
    }

    private void buildMenu() {
        for (String option : currentUser.getMenuOptions()) {
            JFXButton button = new JFXButton(option);
            button.setMaxWidth(Double.MAX_VALUE);
            button.getStyleClass().add("menu-btn");
            button.setOnAction(event -> loadContent(option, button));
            menuBox.getChildren().add(button);

            if (option.equals("Dashboard")) {
                activeMenuBtn = button;
                button.getStyleClass().add("menu-btn-active");
            }
        }
    }

    private void loadContent(String option, JFXButton source) {
        String fxml;
        switch (option) {
            case "Register Appointment": fxml = "registration_form.fxml"; break;
            case "Appointments": fxml = "appointments_form.fxml"; break;
            case "Patients": fxml = "patients_form.fxml"; break;
            case "Billing": fxml = "billing_form.fxml"; break;
            case "Users": fxml = "user_form.fxml"; break;
            case "Reports": fxml = "reports_form.fxml"; break;
            case "Treatments": fxml = "treatment_types_form.fxml"; break;
            case "Dashboard":
            default: fxml = "menu_form.fxml";
        }

        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/" + fxml));
            AnchorPane.setTopAnchor(parent, 0.0);
            AnchorPane.setBottomAnchor(parent, 0.0);
            AnchorPane.setLeftAnchor(parent, 0.0);
            AnchorPane.setRightAnchor(parent, 0.0);

            contentPane.getChildren().clear();
            contentPane.getChildren().add(parent);

            if (source != null) {
                if (activeMenuBtn != null) {
                    activeMenuBtn.getStyleClass().remove("menu-btn-active");
                }
                source.getStyleClass().add("menu-btn-active");
                activeMenuBtn = source;
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could Not Load Screen !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void logoutBtnOnAction(ActionEvent event) {
        try {
            currentUser = null;
            javafx.stage.Stage stage = (javafx.stage.Stage) menuBox.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(FXMLLoader.load(getClass().getResource("/view/login_form.fxml"))));
            stage.setTitle("Sunrise Dental Clinic");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
