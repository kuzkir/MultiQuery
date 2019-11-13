/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.fxmessagebox.ErrorProvider;
import com.github.kuzkir.fxmessagebox.MessageBox;
import com.github.kuzkir.multiquery.entity.Database;
import com.github.kuzkir.multiquery.entity.DatabaseGroup;
import com.github.kuzkir.multiquery.entity.DatabaseStatus;
import com.github.kuzkir.multiquery.helper.ConnectionHelper;
import com.github.kuzkir.multiquery.helper.StringHelper;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class DatabaseEditController implements Initializable {

    private Stage stage;
    private boolean isSave;
    private DatabaseGroup group;
    private Database base;

    @FXML
    private TextField tfTitle;
    @FXML
    private TextField tfHost;
    @FXML
    private Spinner<Integer> sPort;
    @FXML
    private TextField tfUser;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private ComboBox<String> cbBase;
    @FXML
    private ErrorProvider epTitle;
    @FXML
    private ErrorProvider epBase;

    public boolean isSave() {
        return this.isSave;
    }

    public Database getDatabase() {
        return this.base;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setBase(DatabaseGroup group, Database base) {
        this.group = group;
        if (group.getDriver() instanceof org.postgresql.Driver) {
            sPort.getValueFactory().setValue(5432);
        }
        if (group.getDriver() instanceof SQLServerDriver) {
            sPort.getValueFactory().setValue(1433);
        }
        if (base == null) {
            return;
        }
        this.base = base;

        tfTitle.setText(base.getTitle());
        tfHost.setText(base.getHost());
        sPort.getValueFactory().setValue(base.getPort());
        tfUser.setText(base.getUser());
        pfPassword.setText(base.getPassword());
        cbBase.getSelectionModel().select(base.getBase());
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sPort.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 65535));
        sPort.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue)
                sPort.increment(0);
        });
    }

    @FXML
    private void cbBase_onShowing() {
        getCatalogs();
    }

    @FXML
    public void btnCancel_onAction() {
        stage.close();
    }

    @FXML
    public void btnSave_onAction() {
        if (verify()) {
            try {
                this.base = new Database()
                    .setIsActive(true)
                    .setTitle(tfTitle.getText().trim())
                    .setStatus(DatabaseStatus.DISCONNECT)
                    .setHost(tfHost.getText())
                    .setPort(sPort.getValueFactory().getValue())
                    .setBase(cbBase.getValue())
                    .setUser(tfUser.getText())
                    .setPassword(pfPassword.getText());
                isSave = true;
                stage.close();
            } catch (Exception e) {
                MessageBox.showException("Создение подключения", e);
            }

        }
    }

    private boolean verify() {
        int LENGTH = 20;

        boolean result = epTitle.verify(() -> (!tfTitle.getText().trim().isEmpty()), "Заголовок не может быть пустым");
        result = result && epTitle.verify(() -> tfTitle.getText().length() < LENGTH, String.format("Длина заголовка не должна превышать %d символ%s", LENGTH, StringHelper.endAfterInt(LENGTH)));
        result = epBase.verify(() -> tryConnect(), "Не удается подключиться к указанной базе данных") && result;
        return result;
    }

    private void getCatalogs() {
        cbBase.getItems().clear();
        if (group.getDriver() instanceof org.postgresql.Driver) {
            String url = ConnectionHelper.getPgConnectionURL(tfHost.getText(), sPort.getValueFactory().getValue());
            try (Connection con = DriverManager.getConnection(url, tfUser.getText(), pfPassword.getText())) {
                ResultSet rs = con.createStatement().executeQuery("SELECT datname FROM pg_database WHERE NOT datistemplate AND datallowconn ORDER BY datname");
                while (rs.next()) {
                    cbBase.getItems().add(rs.getString(1));
                }
            } catch (Exception e) {
                MessageBox.showException("Получение списка баз данных", e);
                return;
            }
            return;
        }
        if (group.getDriver() instanceof SQLServerDriver) {
            String url = ConnectionHelper.getMsConnectionURL(tfHost.getText(), sPort.getValueFactory().getValue());
            try (Connection con = DriverManager.getConnection(url, tfUser.getText(), pfPassword.getText())) {
                ResultSet rs = con.getMetaData().getCatalogs();
                while (rs.next()) {
                    cbBase.getItems().add(rs.getString(1));
                }
            } catch (Exception e) {
                MessageBox.showException("Получение списка баз данных", e);
                return;
            }
            return;
        }
    }

    private boolean tryConnect() {
        String url = ConnectionHelper.getMsConnectionURL(tfHost.getText(), sPort.getValueFactory().getValue(), cbBase.getValue());
        try (Connection con = DriverManager.getConnection(url, tfUser.getText(), pfPassword.getText())) {
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
