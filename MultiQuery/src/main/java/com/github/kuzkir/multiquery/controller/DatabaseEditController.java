/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.fxcontrol.message.ErrorProvider;
import com.github.kuzkir.fxcontrol.message.MessageBox;
import com.github.kuzkir.multiquery.entity.Database;
import com.github.kuzkir.multiquery.entity.DatabaseGroup;
import com.github.kuzkir.multiquery.entity.DatabaseStatus;
import com.github.kuzkir.multiquery.helper.ConnectionHelper;
import com.github.kuzkir.multiquery.helper.EncodeHelper;
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
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class DatabaseEditController implements Initializable {

    private int defaultPort;

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
            defaultPort = 5432;
        }
        if (group.getDriver() instanceof SQLServerDriver) {
            defaultPort = 1433;
        }

        sPort.getValueFactory().setValue(defaultPort);
        if (base == null) {
            return;
        }
        this.base = base;

        tfTitle.setText(base.getTitle());
        tfHost.setText(base.getHost());
        sPort.getValueFactory().setValue(base.getPort());
        tfUser.setText(base.getUser());
        pfPassword.setText(EncodeHelper.decode(base.getPassword()));
        cbBase.getSelectionModel().select(base.getBase());
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sPort.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 65535));
        sPort.getEditor().setTextFormatter(new TextFormatter(new IntegerStringConverter()));
        sPort.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                sPort.increment(0);
            }
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
        String txt = tfTitle.getText().trim();
        String host = tfHost.getText();
        int port = sPort.getValue();
        String user = tfUser.getText();
        String pswd = pfPassword.getText();
        String base = cbBase.getValue();

        if (verify()) {
            try {
                this.base = new Database()
                    .setIsActive(true)
                    .setTitle(txt)
                    .setStatus(DatabaseStatus.DISCONNECT)
                    .setHost(host)
                    .setPort(port)
                    .setBase(base)
                    .setUser(user)
                    .setPassword(EncodeHelper.encode(pswd));
                isSave = true;
                stage.close();
            } catch (Exception e) {
                MessageBox.showException("Создение подключения", e);
            }

        }
    }

    private boolean verify() {
        int LENGTH = 40;

        String txt = tfTitle.getText().trim();

        boolean result = epTitle.verify(() -> (!txt.isEmpty()), "Заголовок не может быть пустым");
        result = result && epTitle.verify(() -> txt.length() < LENGTH, String.format("Длина заголовка не должна превышать %d символ%s", LENGTH, StringHelper.endAfterInt(LENGTH)));
        
        result = epBase.verify(() -> tryConnect(), "Не удается подключиться к указанной базе данных") && result;
        return result;
    }

    private void getCatalogs() {
        String host = tfHost.getText();
        int port = sPort.getValue();
        String user = tfUser.getText();
        String pswd = pfPassword.getText();

        cbBase.getItems().clear();
        if (group.getDriver() instanceof org.postgresql.Driver) {
            String url = ConnectionHelper.getPgConnectionURL(host, port);
            try (Connection con = DriverManager.getConnection(url, user, pswd)) {
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
            String url = ConnectionHelper.getMsConnectionURL(host, port);
            try (Connection con = DriverManager.getConnection(url, user, pswd)) {
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
        String host = tfHost.getText();
        int port = sPort.getValue();
        String user = tfUser.getText();
        String pswd = pfPassword.getText();
        String base = cbBase.getValue();

        String url = ConnectionHelper.getMsConnectionURL(host, port, base);
        try (Connection con = DriverManager.getConnection(url, user, pswd)) {
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
