/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.multiquery.engine.Resultable;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class ResultSetController implements Initializable, Resultable {
    
    @FXML
    StackPane spPane;
    @FXML
    TableView tvTable;
    @FXML
    VBox vbProgress;
    @FXML
    Label lbStatus;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void setResult(ResultSet resultSet) {
        
    }
    
    @Override
    public void clear() {
        
    }

    @Override
    public void setStatus(String status) {
        if(status == null) {
            vbProgress.setVisible(false);
            tvTable.setVisible(true);
            return;
        }
        tvTable.setVisible(false);
        vbProgress.setVisible(true);
        lbStatus.setText(status);        
    }
}
