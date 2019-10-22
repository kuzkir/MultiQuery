/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class MainPanelController implements Initializable {

    @FXML
    private AnchorPane sourcePane;
    @FXML
    private AnchorPane requestPane;
    @FXML
    private AnchorPane resultPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            FXMLLoader conSourceLoader = new FXMLLoader();
            conSourceLoader.setLocation(getClass().getResource("/fxml/ConnectionSource.fxml"));
            Node cs = conSourceLoader.load();
            sourcePane.getChildren().add(cs);
            sourcePane.setTopAnchor(cs, 0.0);
            sourcePane.setLeftAnchor(cs, 0.0);
            sourcePane.setRightAnchor(cs, 0.0);
            sourcePane.setBottomAnchor(cs, 0.0);
        } catch (IOException e) {
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Ошибка загрузки формы");
            message.setHeaderText(e.getMessage());
            message.show();
        }

    }

}
