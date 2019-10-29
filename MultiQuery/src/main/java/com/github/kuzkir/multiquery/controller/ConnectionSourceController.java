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
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private Button btnAddGroup;

    public ConnectionSourceController() {
        maintenance = new SourceMaintenance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
    }

    @FXML
    private void btnAddGroup_onAction() {
        try {            
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/DatabaseGroupEdit.fxml"));
            Parent parent = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Создание группы");
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(Main.getPrimaryStage());
            stage.setScene(new Scene(parent));
            
            DatabaseGroupEditController controller = loader.getController();
            controller.setStage(stage);
            
            stage.showAndWait();
            if(controller.isSave()) {
                DatabaseGroup dg = controller.getDatabaseGroup();
            }
            
                        
        } catch (Exception e) {
            MessageBox.showException(e);
        }

    }


}
