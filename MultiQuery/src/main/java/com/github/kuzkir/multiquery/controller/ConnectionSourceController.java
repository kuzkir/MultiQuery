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
import com.github.kuzkir.multiquery.maintenance.ConnectionSource;
import com.github.kuzkir.multiquery.engine.Connectable;
import com.github.kuzkir.multiquery.entity.DatabaseStatus;
import java.io.IOException;
import java.net.URL;
import java.sql.Driver;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class ConnectionSourceController implements Initializable, Connectable {

    private final ConnectionSource source;

    @FXML
    private ComboBox<String> cbConnectionGroup;
    @FXML
    private TableView<Database> tbDatabase;
    @FXML
    private TableColumn<Database, Boolean> tcActive;
    @FXML
    private TableColumn<Database, ImageView> tcStatus;
    @FXML
    private TableColumn<Database, String> tcTitle;

    public ConnectionSourceController() {
        source = new ConnectionSourceMaintenance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateGroup(null);

        tcActive.setCellValueFactory((TableColumn.CellDataFeatures<Database, Boolean> param) -> {
            Database db = param.getValue();
            SimpleBooleanProperty bp = new SimpleBooleanProperty(db.getIsActive());

            bp.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                try {
                    db.setIsActive(newValue);
                    String group = cbConnectionGroup.getValue();
                    source.editBase(group, db.getTitle(), db);
                } catch (Exception e) {
                    MessageBox.showException("Изменение активности", e);
                }
            });

            return bp;
        });

        tcActive.setCellFactory((TableColumn<Database, Boolean> param) -> {
            return new CheckBoxTableCell<>();
        });

        tcStatus.setCellValueFactory(new PropertyValueFactory<>("image"));
        tcTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
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
        String group = cbConnectionGroup.getValue();
        if (group == null) {
            return;
        }

        if (!MessageBox.askQuestion("Удаление группы", "При удалении группы будут удалены все ее подключения.\r\nВы хотите продолжить?")) {
            return;
        }

        try {
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
            String group = cbConnectionGroup.getValue();
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
            DatabaseGroup group = source.getGroupByTitle(cbConnectionGroup.getValue());
            if (group == null) {
                return;
            }

            Database base = databaseEditForm(group, null);

            if (base != null) {
                source.addBase(group.getTitle(), base);
            }

            updateBase();
        } catch (Exception e) {
            MessageBox.showException("Создание подключения", e);
        }
    }

    @FXML
    private void btnDeleteConnection_onAction() {
        String group = cbConnectionGroup.getValue();
        Database db = tbDatabase.getSelectionModel().getSelectedItem();
        if (group == null || db == null) {
            return;
        }

        if (!MessageBox.askQuestion("Удаление подключения", "Вы уверены что хотите удалить подключение?")) {
            return;
        }

        try {
            String base = db.getTitle();
            if (source.removeBase(group, base)) {
                updateBase();
            }
        } catch (Exception e) {
            MessageBox.showException("Удаление подключения", e);
        }
    }

    @FXML
    private void btnEditConnection_onAction() {
        try {
            String group = cbConnectionGroup.getValue();
            Database db = tbDatabase.getSelectionModel().getSelectedItem();
            DatabaseGroup dg = source.getGroupByTitle(group);
            if (dg != null && db != null) {
                Database ndb = databaseEditForm(dg, db);

                if (ndb != null) {
                    ndb = source.editBase(group, db.getTitle(), ndb);
                    updateBase();
                }
            }
        } catch (Exception e) {
            MessageBox.showException("Изменение подключения", e);
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
        stage.getIcons().addAll(Main.getPrimaryStage().getIcons());
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

    private Database databaseEditForm(DatabaseGroup group, Database base) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/DatabaseEdit.fxml"));
        Parent parent = loader.load();

        Stage stage = new Stage();
        stage.setTitle(base == null ? "Создание подключения" : "Изменение подключения");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.getPrimaryStage());
        stage.getIcons().addAll(Main.getPrimaryStage().getIcons());
        stage.setScene(new Scene(parent));

        DatabaseEditController baseEdit = loader.getController();
        baseEdit.setStage(stage);
        baseEdit.setBase(group, base);

        stage.showAndWait();
        if (baseEdit.isSave()) {
            return baseEdit.getDatabase();
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
            DatabaseGroup group = source.getGroupByTitle(cbConnectionGroup.getValue());
            if (group != null) {
                tbDatabase.getItems().clear();
                tbDatabase.getItems().addAll(group.getDatabases());
            }
        } catch (Exception e) {
            MessageBox.showException("Обновление списка групп", e);
        }
    }

    @Override
    public List<Database> getDatabases() {
        try {
            DatabaseGroup group = source.getGroupByTitle(cbConnectionGroup.getValue());
            return group.getDatabases().stream()
                .filter(a -> a.getIsActive())
                .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Driver getDriver() {
        try {
            DatabaseGroup group = source.getGroupByTitle(cbConnectionGroup.getValue());
            return group.getDriver();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setStatus(String base, DatabaseStatus status) {
        try {
            DatabaseGroup group = source.getGroupByTitle(cbConnectionGroup.getValue());
            group.getDatabases().stream()
                .filter(a -> a.getTitle().equals(base))
                .findAny()
                .ifPresent(a -> a.setStatus(status));
            updateBase();
        } catch (Exception e) {}
    }
}
