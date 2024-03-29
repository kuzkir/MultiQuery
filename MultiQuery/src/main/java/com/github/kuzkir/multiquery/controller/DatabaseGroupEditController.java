/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.fxcontrol.message.ErrorProvider;
import com.github.kuzkir.multiquery.entity.DatabaseGroup;
import com.github.kuzkir.multiquery.helper.StringHelper;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
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
    
    private final String POSTGRESQL = "PostgreSQL Server";
    private final String SQLSERVER = "Microsoft SQL Server";

    public boolean isSave() {
        return this.isSave;
    }

    public DatabaseGroup getDatabaseGroup() {
        return this.group;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setGroup(DatabaseGroup group) {
        if(group == null) {
            return;
        }
        
        this.group = group;
        
        tfTitle.setText(group.getTitle());
        if(group.getDriver() instanceof org.postgresql.Driver)
            cbDriver.getSelectionModel().select(POSTGRESQL);
        
        if(group.getDriver() instanceof SQLServerDriver)
            cbDriver.getSelectionModel().select(SQLSERVER);        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbDriver.getItems().add(POSTGRESQL);
        cbDriver.getItems().add(SQLSERVER);
        cbDriver.getSelectionModel().select(0);
    }

    @FXML
    public void btnCancel_onAction() {
        stage.close();
    }

    @FXML
    public void btnSave_onAction() {
        String txt = tfTitle.getText().trim();
        
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
                    .setTitle(txt)
                    .setDriver(driver);
                isSave = true;
                stage.close();
            } catch (Exception e) {
                epDriver.verify(() -> false, "Ошибка загрузки драйвера JDBC");
            }

        }
    }

    private boolean verify() {
        int LENGTH = 40;
        
        String txt = tfTitle.getText().trim();

        boolean result = epTitle.verify(() -> (!txt.isEmpty()), "Заголовок не может быть пустым");
        result = result && epTitle.verify(() -> txt.length() < LENGTH, String.format("Длина заголовка не должна превышать %d символ%s", LENGTH, StringHelper.endAfterInt(LENGTH)));
        return result;
    }

}
