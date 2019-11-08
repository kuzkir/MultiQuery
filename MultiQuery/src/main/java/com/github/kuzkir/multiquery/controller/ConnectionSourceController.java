/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.fxmessagebox.MessageBox;
import com.github.kuzkir.multiquery.Main;
import com.github.kuzkir.multiquery.entity.Database;
import com.github.kuzkir.multiquery.entity.DatabaseGroup;
import com.github.kuzkir.multiquery.entity.DatabaseStatus;
import com.github.kuzkir.multiquery.maintenance.ConnectionSource;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class ConnectionSourceController implements Initializable {

    private final ConnectionSource source;

    @FXML
    private ComboBox<String> cbConnectionGroup;
    @FXML
    private TableView<Database> tbDatabase;
    @FXML
    private TableColumn<Database, Boolean> tcActive;

    public ConnectionSourceController() {
        source = new ConnectionSourceMaintenance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateGroup(null);

        tcActive.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Database, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Database, Boolean> param) {
                Database db = param.getValue();
                SimpleBooleanProperty bp = new SimpleBooleanProperty(db.getIsActive());

                bp.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        try {
                            db.setIsActive(newValue);
                            String group = cbConnectionGroup.getSelectionModel().getSelectedItem();
                            source.editBase(group, db.getTitle(), db);
                        } catch (Exception e) {
                            MessageBox.showException("Отключение подключения", e);
                        }
                    }
                });

                return bp;
            }
        });

        tcActive.setCellFactory(new Callback<TableColumn<Database, Boolean>, TableCell<Database, Boolean>>() {
            @Override
            public TableCell<Database, Boolean> call(TableColumn<Database, Boolean> param) {
                CheckBoxTableCell<Database, Boolean> cell = new CheckBoxTableCell<Database, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
    }

    @FXML
    private void btnAddGroup_onAction() {
        try {
            DatabaseGroup group = databaseGroupEditForm(null);
            if (group != null) {
                source.addGroup(group);
                updateGroup(group.getTitle());
            }
        } catch (Exception e) {
            MessageBox.showException("Создание группы", e);
        }

    }

    @FXML
    private void btnDeleteGroup_onAction() {
        if (!MessageBox.askQuestion("Удаление группы", "При удалении группы будут удалены все ее подключения.\r\nВы хотите продолжить?")) {
            return;
        }

        try {
            String group = cbConnectionGroup.getSelectionModel().getSelectedItem();
            if (source.removeGroup(group)) {
                updateGroup(null);
            }
        } catch (Exception e) {
            MessageBox.showException("Удаление группы", e);
        }
    }

    @FXML
    private void cbConnectionGroup_onAction() {
        updateBase();
    }

    @FXML
    private void btnEditGroup_onAction() {
        try {
            String group = cbConnectionGroup.getSelectionModel().getSelectedItem();
            DatabaseGroup dg = source.getGroupByTitle(group);
            if (dg != null) {
                DatabaseGroup ndg = databaseGroupEditForm(dg);

                if (ndg != null) {
                    ndg = source.editGroup(group, ndg);
                    updateGroup(ndg.getTitle());
                }
            }
        } catch (Exception e) {
            MessageBox.showException("Изменение группы", e);
        }
    }

    @FXML
    private void btnAddConnection_onAction() {
        try {
            String group = cbConnectionGroup.getSelectionModel().getSelectedItem();
            Database d = new Database()
                .setIsActive(true)
                .setStatus(DatabaseStatus.DISCONECT)
                .setTitle("TEST")
                //                    .setConnection(DriverManager.)
                //                    .setConnection(DriverManager.getConnection("jdbc:sqlserver://win2003;databaseName=TRANSNAVI_42", "trn-admin", "57835783"));
                .setHost("win2003")
                .setBase("TRANSNAVI_42")
                .setUser("trn-admin")
                .setPassword("57835783");

            source.addBase(group, d);
            updateBase();
        } catch (Exception e) {
            MessageBox.showException("Создание подключения", e);
        }
    }

    private DatabaseGroup databaseGroupEditForm(DatabaseGroup group) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/DatabaseGroupEdit.fxml"));
        Parent parent = loader.load();

        Stage stage = new Stage();
        stage.setTitle(group == null ? "Создание группы" : "Изменение группы");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.getPrimaryStage());
        stage.setScene(new Scene(parent));

        DatabaseGroupEditController groupEdit = loader.getController();
        groupEdit.setStage(stage);
        groupEdit.setGroup(group);

        stage.showAndWait();
        if (groupEdit.isSave()) {
            return groupEdit.getDatabaseGroup();
        }
        return null;
    }

    private void updateGroup(String focus) {
        try {
            cbConnectionGroup.getItems().clear();
            cbConnectionGroup.getItems().addAll(source.getGroupList());

            if (focus != null) {
                cbConnectionGroup.getSelectionModel().select(focus);
            } else {
                cbConnectionGroup.getSelectionModel().select(0);
            }
            updateBase();
        } catch (Exception e) {
            MessageBox.showException("Обновление списка групп", e);
        }
    }

    private void updateBase() {
        try {
            DatabaseGroup group = source.getGroupByTitle(cbConnectionGroup.getSelectionModel().getSelectedItem());
            if (group != null) {
                tbDatabase.getItems().clear();
                tbDatabase.getItems().addAll(group.getDatabases());
            }
        } catch (Exception e) {
            MessageBox.showException("Обновление списка групп", e);
        }
    }
}
