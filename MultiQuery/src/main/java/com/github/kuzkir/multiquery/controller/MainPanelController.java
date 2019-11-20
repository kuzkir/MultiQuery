/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.fxcontrol.MessageBox;
import com.github.kuzkir.multiquery.Main;
import com.github.kuzkir.multiquery.engine.Executable;
import com.github.kuzkir.multiquery.engine.ExecutableFactory;
import javafx.scene.input.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class MainPanelController implements Initializable {

    private Executable exe;

    @FXML
    private AnchorPane sourcePane;
    @FXML
    private AnchorPane queryPane;
    @FXML
    private AnchorPane resultPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            FXMLLoader sourceLoader = new FXMLLoader();
            sourceLoader.setLocation(getClass().getResource("/fxml/ConnectionSource.fxml"));
            Node src = sourceLoader.load();
            sourcePane.getChildren().add(src);
            sourcePane.setTopAnchor(src, 0.0);
            sourcePane.setLeftAnchor(src, 0.0);
            sourcePane.setRightAnchor(src, 0.0);
            sourcePane.setBottomAnchor(src, 0.0);

            FXMLLoader resultLoader = new FXMLLoader();
            resultLoader.setLocation(getClass().getResource("/fxml/ResultSet.fxml"));
            Node rst = resultLoader.load();
            resultPane.getChildren().add(rst);
            resultPane.setTopAnchor(rst, 0.0);
            resultPane.setLeftAnchor(rst, 0.0);
            resultPane.setRightAnchor(rst, 0.0);
            resultPane.setBottomAnchor(rst, 0.0);

            FXMLLoader queryLoader = new FXMLLoader();
            queryLoader.setLocation(getClass().getResource("/fxml/QueryEditor.fxml"));
            Node qre = queryLoader.load();
            queryPane.getChildren().add(qre);
            queryPane.setTopAnchor(qre, 0.0);
            queryPane.setLeftAnchor(qre, 0.0);
            queryPane.setRightAnchor(qre, 0.0);
            queryPane.setBottomAnchor(qre, 0.0);

            exe = ExecutableFactory.getInstance()
                .setConnection((ConnectionSourceController) sourceLoader.getController())
                .setQuery((QueryEditorController) queryLoader.getController())
                .setResulte((ResultSetController) resultLoader.getController())
                .build();
            
            Main.getPrimaryStage().addEventHandler(KeyEvent.KEY_PRESSED, key -> {
                if(key.getCode().equals(KeyCode.F5))
                    btnExecute_onAction();
            });
            
            
        } catch (IOException e) {
            MessageBox.showException("Загрузка главной формы", e);
            Main.getPrimaryStage().close();
        } catch (Exception e) {
            MessageBox.showException("Загрузка движка", e);
            Main.getPrimaryStage().close();
        }
    }

    @FXML
    private void btnExecute_onAction() {
        try {
            exe.execute();
        } catch (Exception e) {
            MessageBox.showException("Выполнение запроса", e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        exe.close();
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
    }

}
