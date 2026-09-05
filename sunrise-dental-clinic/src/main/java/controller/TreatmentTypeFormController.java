package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.TreatmentTypeModel;

import java.util.Optional;

public class TreatmentTypeFormController {

    @FXML
    private TableView<TreatmentTypeModel> table;

    @FXML
    private TableColumn<?, ?> codeCol;

    @FXML
    private TableColumn<?, ?> nameCol;

    @FXML
    private TableColumn<?, ?> unitCostCol;

    @FXML
    private TableColumn<?, ?> perToothCol;

    @FXML
    private TableColumn<?, ?> durationCol;

    @FXML
    private JFXTextField codeTxt;

    @FXML
    private JFXTextField nameTxt;

    @FXML
    private JFXTextField unitCostTxt;

    @FXML
    private JFXCheckBox perToothCheckBox;

    @FXML
    private JFXTextField durationTxt;

    @FXML
    private JFXButton addBtn;

    @FXML
    private JFXButton updateBtn;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private JFXButton clearBtn;

    TreatmentTypeModel selected;

    @FXML
    public void initialize() {
        setCellValueFactory();
        loadTableValues();
        setBtnsVisible(false);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow != null) {
                load(newRow);
            }
        });
    }

    private void setCellValueFactory() {
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        unitCostCol.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        perToothCol.setCellValueFactory(new PropertyValueFactory<>("perTooth"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
    }

    private void loadTableValues() {
        ObservableList<TreatmentTypeModel> obList = FXCollections.observableArrayList();
        try {
            obList.addAll(TreatmentTypeModel.getAll());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Data Loading Error !").show();
            e.printStackTrace();
        }
        table.setItems(obList);
    }

    private void load(TreatmentTypeModel type) {
        selected = type;
        codeTxt.setText(type.getCode());
        codeTxt.setEditable(false);
        nameTxt.setText(type.getName());
        unitCostTxt.setText(String.valueOf(type.getUnitCost()));
        perToothCheckBox.setSelected(type.isPerTooth());
        durationTxt.setText(String.valueOf(type.getDurationMinutes()));
        setBtnsVisible(true);
    }

    @FXML
    void addBtnOnAction(ActionEvent event) {
        try {
            if (codeTxt.getText().isEmpty() || nameTxt.getText().isEmpty() || unitCostTxt.getText().isEmpty() || durationTxt.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Enter valid Data").show();
                return;
            }

            TreatmentTypeModel type = new TreatmentTypeModel(
                    codeTxt.getText().trim().toUpperCase().replace(" ", "_"), nameTxt.getText(),
                    Double.parseDouble(unitCostTxt.getText()), perToothCheckBox.isSelected(), Integer.parseInt(durationTxt.getText())
            );

            type.save();
            new Alert(Alert.AlertType.INFORMATION, "Treatment Type Added !").show();
            clear();
            loadTableValues();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Treatment Type Not Added ! (code may already exist)").show();
            e.printStackTrace();
        }

    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        try {
            if (nameTxt.getText().isEmpty() || unitCostTxt.getText().isEmpty() || durationTxt.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Enter valid Data").show();
                return;
            }

            selected.setName(nameTxt.getText());
            selected.setUnitCost(Double.parseDouble(unitCostTxt.getText()));
            selected.setPerTooth(perToothCheckBox.isSelected());
            selected.setDurationMinutes(Integer.parseInt(durationTxt.getText()));

            selected.update();
            new Alert(Alert.AlertType.INFORMATION, "Treatment Type Updated !").show();
            clear();
            loadTableValues();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Treatment Type Not Updated !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete treatment type " + codeTxt.getText() + "? Appointments already booked with this code will no longer resolve correctly.", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.YES) {
            return;
        }

        try {
            TreatmentTypeModel.delete(codeTxt.getText());
            new Alert(Alert.AlertType.INFORMATION, "Treatment Type Deleted !").show();
            clear();
            loadTableValues();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Treatment Type Not Deleted ! (it may be in use by existing appointments)").show();
            e.printStackTrace();
        }

    }

    @FXML
    void clearBtnOnAction(ActionEvent event) {
        table.getSelectionModel().clearSelection();
        table.getFocusModel().focus(-1);
        clear();
    }

    private void clear() {
        selected = null;
        codeTxt.clear();
        codeTxt.setEditable(true);
        nameTxt.clear();
        unitCostTxt.clear();
        perToothCheckBox.setSelected(false);
        durationTxt.clear();
        setBtnsVisible(false);
    }

    public void setBtnsVisible(boolean ok) {
        updateBtn.setVisible(ok);
        deleteBtn.setVisible(ok);
    }

}
