package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.DentistModel;
import model.ManagerModel;
import model.ReceptionistModel;
import model.UserModel;
import model.tm.UserTM;
import util.PasswordUtil;
import util.Validations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserFormController {

    @FXML
    public ToggleGroup role;

    @FXML
    public JFXRadioButton receptionistRBtn;

    @FXML
    public JFXRadioButton dentistRBtn;

    @FXML
    public JFXRadioButton managerRBtn;

    @FXML
    public VBox staffPane;

    @FXML
    public VBox dentistPane;

    @FXML
    private TableView<UserTM> table;

    @FXML
    private TableColumn<?, ?> userIdCol;

    @FXML
    private TableColumn<?, ?> usernameCol;

    @FXML
    private TableColumn<?, ?> roleCol;

    @FXML
    private TableColumn<?, ?> detailCol;

    @FXML
    private TableColumn<?, ?> contactCol;

    @FXML
    private TableColumn<?, ?> activeCol;

    @FXML
    private JFXTextField searchTxt;

    @FXML
    private JFXTextField userIdTxt;

    @FXML
    private JFXTextField usernameTxt;

    @FXML
    private JFXPasswordField passwordTxt;

    @FXML
    private JFXTextField designationTxt;

    @FXML
    private JFXTextField staffContactTxt;

    @FXML
    private JFXTextField emailTxt;

    @FXML
    private JFXTextField dentistNameTxt;

    @FXML
    private JFXTextField specializationTxt;

    @FXML
    private JFXTextField dentistContactTxt;

    @FXML
    private JFXTextField feeTxt;

    @FXML
    private JFXTextField daysTxt;

    @FXML
    private JFXButton searchBtn;

    @FXML
    private JFXButton addBtn;

    @FXML
    private JFXButton updateBtn;

    @FXML
    private JFXButton deleteBtn;

    UserModel selectedUser;

    @FXML
    public void initialize() {
        setCellValueFactory();
        loadTableValues();
        setBtnsVisible(false);
        userIdTxt.setEditable(false);
        setTextFieldValidations();
        rolePaneOnAction(null);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow != null) {
                loadUser(newRow.getUserId());
            }
        });
    }

    private void setTextFieldValidations() {
        Validations.setFocus(usernameTxt, Validations.namePattern);
        Validations.setFocus(staffContactTxt, Validations.mobilePattern);
        Validations.setFocus(dentistContactTxt, Validations.mobilePattern);
        Validations.setFocus(searchTxt, Validations.namePattern);
    }

    private void setCellValueFactory() {
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        detailCol.setCellValueFactory(new PropertyValueFactory<>("detail"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
    }

    private void loadTableValues() {
        ObservableList<UserTM> obList = FXCollections.observableArrayList();
        try {
            List<UserModel> users = UserModel.getAll();
            for (UserModel user : users) {
                obList.add(toTM(user));
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Data Loading Error !").show();
            e.printStackTrace();
        }
        table.setItems(obList);
    }

    private UserTM toTM(UserModel user) {
        if (user instanceof ReceptionistModel) {
            ReceptionistModel r = (ReceptionistModel) user;
            return new UserTM(r.getUserID(), r.getUsername(), r.getRole().name(), r.getDesignation(), r.getContactNo(), r.isActive());
        } else if (user instanceof DentistModel) {
            DentistModel d = (DentistModel) user;
            return new UserTM(d.getUserID(), d.getUsername(), d.getRole().name(), d.getSpecialization(), d.getContactNo(), d.isActive());
        } else {
            return new UserTM(user.getUserID(), user.getUsername(), user.getRole().name(), "-", "-", user.isActive());
        }
    }

    @FXML
    public void rolePaneOnAction(ActionEvent event) {
        boolean isReceptionist = receptionistRBtn.isSelected();
        boolean isDentist = dentistRBtn.isSelected();
        staffPane.setVisible(isReceptionist);
        staffPane.setManaged(isReceptionist);
        dentistPane.setVisible(isDentist);
        dentistPane.setManaged(isDentist);
    }

    @FXML
    void addBtnOnAction(ActionEvent event) {
        try {
            if (usernameTxt.getText().isEmpty() || passwordTxt.getText().isEmpty() || role.getSelectedToggle() == null) {
                new Alert(Alert.AlertType.ERROR, "Enter valid Data").show();
                return;
            }

            String userId = UserModel.generateNextUserId();
            String passwordHash = PasswordUtil.hash(passwordTxt.getText());

            UserModel user;
            if (receptionistRBtn.isSelected()) {
                user = new ReceptionistModel(userId, usernameTxt.getText(), passwordHash, true, null,
                        ReceptionistModel.generateNextStaffId(), designationTxt.getText(), staffContactTxt.getText(), emailTxt.getText());
            } else if (dentistRBtn.isSelected()) {
                user = new DentistModel(userId, usernameTxt.getText(), passwordHash, true, null,
                        DentistModel.generateNextDentistId(), dentistNameTxt.getText(), specializationTxt.getText(),
                        dentistContactTxt.getText(), Double.parseDouble(feeTxt.getText()), Arrays.asList(daysTxt.getText().split(",")));
            } else {
                user = new ManagerModel(userId, usernameTxt.getText(), passwordHash, true, null);
            }

            user.save();
            new Alert(Alert.AlertType.INFORMATION, "User Added !").show();
            clearTxtFields();
            loadTableValues();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "User Not Added !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void searchBtnOnAction(ActionEvent event) {
        if (searchTxt.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Enter a User Id or Username to search").show();
            return;
        }
        loadUser(searchTxt.getText());
    }

    private void loadUser(String idOrUsername) {
        try {
            selectedUser = UserModel.search(idOrUsername);
            if (selectedUser == null) {
                new Alert(Alert.AlertType.ERROR, "Data Missing On This Id !").show();
                return;
            }

            searchTxt.setText(selectedUser.getUserID());
            userIdTxt.setText(selectedUser.getUserID());
            usernameTxt.setText(selectedUser.getUsername());

            if (selectedUser instanceof ReceptionistModel) {
                receptionistRBtn.setSelected(true);
                ReceptionistModel r = (ReceptionistModel) selectedUser;
                designationTxt.setText(r.getDesignation());
                staffContactTxt.setText(r.getContactNo());
                emailTxt.setText(r.getEmail());
            } else if (selectedUser instanceof DentistModel) {
                dentistRBtn.setSelected(true);
                DentistModel d = (DentistModel) selectedUser;
                dentistNameTxt.setText(d.getDentistName());
                specializationTxt.setText(d.getSpecialization());
                dentistContactTxt.setText(d.getContactNo());
                feeTxt.setText(d.getConsultationFee() + "");
                daysTxt.setText(String.join(",", d.getAvailableDays()));
            } else {
                managerRBtn.setSelected(true);
            }
            rolePaneOnAction(null);
            setBtnsVisible(true);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Data Missing On This Id !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        try {
            selectedUser.setUsername(usernameTxt.getText());

            if (selectedUser instanceof ReceptionistModel) {
                ReceptionistModel r = (ReceptionistModel) selectedUser;
                r.setDesignation(designationTxt.getText());
                r.setContactNo(staffContactTxt.getText());
                r.setEmail(emailTxt.getText());
            } else if (selectedUser instanceof DentistModel) {
                DentistModel d = (DentistModel) selectedUser;
                d.setDentistName(dentistNameTxt.getText());
                d.setSpecialization(specializationTxt.getText());
                d.setContactNo(dentistContactTxt.getText());
                d.setConsultationFee(Double.parseDouble(feeTxt.getText()));
                d.setAvailableDays(Arrays.asList(daysTxt.getText().split(",")));
            }

            selectedUser.update();
            new Alert(Alert.AlertType.INFORMATION, "User Updated !").show();
            clearTxtFields();
            loadTableValues();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "User Not Updated !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user " + userIdTxt.getText() + "? This cannot be undone.", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.YES) {
            return;
        }

        try {
            UserModel.delete(userIdTxt.getText());
            new Alert(Alert.AlertType.INFORMATION, "User Deleted !").show();
            clearTxtFields();
            loadTableValues();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "User Not Deleted !").show();
            e.printStackTrace();
        }

    }

    private void clearTxtFields() {
        userIdTxt.clear();
        usernameTxt.clear();
        passwordTxt.clear();
        designationTxt.clear();
        staffContactTxt.clear();
        emailTxt.clear();
        dentistNameTxt.clear();
        specializationTxt.clear();
        dentistContactTxt.clear();
        feeTxt.clear();
        daysTxt.clear();
        searchTxt.clear();
        setBtnsVisible(false);

        RadioButton roleRBtn = (RadioButton) role.getSelectedToggle();
        if (roleRBtn != null) {
            roleRBtn.setSelected(false);
        }
        rolePaneOnAction(null);
    }

    public void setBtnsVisible(boolean ok) {
        updateBtn.setVisible(ok);
        deleteBtn.setVisible(ok);
    }

}
