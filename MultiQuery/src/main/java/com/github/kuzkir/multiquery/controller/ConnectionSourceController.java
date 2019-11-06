/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.fxmessagebox.MessageBox;
import com.github.kuzkir.multiquery.Main;
import com.github.kuzkir.multiquery.entity.DatabaseGroup;
import com.github.kuzkir.multiquery.maintenance.SourceMaintenance;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class ConnectionSourceController implements Initializable {

    private final SourceMaintenance maintenance;
    private Scene frmDatabaseGroupLoader;

    @FXML
    private ComboBox<String> cbConnectionGroup;

    public ConnectionSourceController() {
        maintenance = new SourceMaintenance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateGroup();
    }

    @FXML
    private void btnAddGroup_onAction() {
        try {
            DatabaseGroup group = connectionGroupEditForm(true);
            if (group != null) {
                maintenance.addGroup(group);
                updateGroup();
                cbConnectionGroup.getSelectionModel().select(group.getTitle());
            }
        } catch (Exception e) {
            MessageBox.showException("Создание группы", e);
        }

    }

    @FXML
    private void btnDeleteGroup_onAction() {
        if(!MessageBox.askQuestion("Удаление группы", "При удалении группы будут удалены все ее подключения.\r\nВы хотите продолжить?"))
            return;
        
        try {
            String group = cbConnectionGroup.getSelectionModel().getSelectedItem();
            if (maintenance.removeGroup(group)) {
                updateGroup();
            }
        } catch (Exception e) {
            MessageBox.showException("Удаление группы", e);
        }
    }

    private DatabaseGroup connectionGroupEditForm(boolean isCreate) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/DatabaseGroupEdit.fxml"));
        Parent parent = loader.load();

        Stage stage = new Stage();
        stage.setTitle(isCreate ? "Создание группы" : "Изменение группы");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.getPrimaryStage());
        stage.setScene(new Scene(parent));

        DatabaseGroupEditController groupEdit = loader.getController();
        groupEdit.setStage(stage);

        stage.showAndWait();
        if (groupEdit.isSave()) {
            return groupEdit.getDatabaseGroup();
        }
        return null;
    }

    private void updateGroup() {
        try {
            cbConnectionGroup.getItems().clear();
            maintenance.getGroups()
                .stream()
                .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                .forEach(a -> cbConnectionGroup.getItems().add(a.getTitle()));
            if (!cbConnectionGroup.getItems().isEmpty()) {
                cbConnectionGroup.getSelectionModel().select(0);
            }
        } catch (Exception e) {
            MessageBox.showException("Обновление списка групп", e);
        }
    }
}
