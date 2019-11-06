/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.fxmessagebox.ErrorProvider;
import com.github.kuzkir.multiquery.entity.DatabaseGroup;
import java.net.URL;
import java.sql.Driver;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class DatabaseGroupEditController implements Initializable {

    @FXML
    private TextField tfTitle;
    @FXML
    private ComboBox<String> cbDriver;
    @FXML
    private ErrorProvider epTitle;
    @FXML
    private ErrorProvider epDriver;

    private Stage stage;
    private boolean isSave;
    private DatabaseGroup group;

    public boolean isSave() {
        return this.isSave;
    }

    public DatabaseGroup getDatabaseGroup() {
        return this.group;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbDriver.getItems().add("PostgreSQL Server");
        cbDriver.getItems().add("Microsoft SQL Server");
        cbDriver.getSelectionModel().select(0);
    }

    @FXML
    public void btnCancel_click() {
        stage.close();
    }

    @FXML
    public void btnSave_click() {
        if (verify()) {
            try {
                Driver driver = null;
                switch (cbDriver.getSelectionModel().getSelectedIndex()) {
                    case 0:
                        driver = (Driver) Class.forName("org.postgresql.Driver").newInstance();
                        break;
                    case 1:
                        driver = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
                        break;
                }

                this.group = new DatabaseGroup()
                    .setTitle(tfTitle.getText().trim())
                    .setDriver(driver);
                isSave = true;
                stage.close();
            } catch (Exception e) {
                epDriver.verify(() -> false, "Ошибка загрузки драйвера JDBC");
            }

        }
    }

    private boolean verify() {
        int LENGTH = 15;

        boolean result = epTitle.verify(() -> (!tfTitle.getText().trim().isEmpty()), "Заголовок должен быть указан");
        result = result && epTitle.verify(() -> tfTitle.getText().length() < LENGTH, String.format("Длина заголовка не должна превышать %d символов", LENGTH));
        return result;
    }

}
