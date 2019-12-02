/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.fxcontrol.datetime.StopwatchVirtual;
import com.github.kuzkir.fxcontrol.message.MessageBox;
import com.github.kuzkir.multiquery.Main;
import com.github.kuzkir.multiquery.engine.Executable;
import com.github.kuzkir.multiquery.engine.ExecutableFactory;
import javafx.scene.input.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
    private StopwatchVirtual stopwatch;

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

            ConnectionSourceController cs = (ConnectionSourceController) sourceLoader.getController();
            QueryEditorController qe = (QueryEditorController) queryLoader.getController();
            ResultSetController rs = (ResultSetController) resultLoader.getController();
            
            exe = ExecutableFactory.getInstance()
                .setConnection(cs)
                .setQuery(qe)
                .setResulte(rs)
                .build();
            qe.setExecutable(exe);

            Main.getPrimaryStage().addEventHandler(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode().equals(KeyCode.F5)) {
                    qe.btnExecute_onAction();            
                }
            });
            
            Main.getPrimaryStage().addEventHandler(KeyEvent.KEY_PRESSED, key -> {
                if (key.isControlDown() && key.getCode().equals(KeyCode.O)) {
                    qe.btnOpen_onAction();          
                }
            });
            
            Main.getPrimaryStage().addEventHandler(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode().equals(KeyCode.F3)) {
                    qe.btnOpen_onAction();
                }
            });
            
            Main.getPrimaryStage().addEventHandler(KeyEvent.KEY_PRESSED, key -> {
                if (key.isControlDown() && key.getCode().equals(KeyCode.S)) {
                    qe.btnSave_onAction();
                }
            });
            
            Main.getPrimaryStage().addEventHandler(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode().equals(KeyCode.F4)) {
                    qe.btnSave_onAction();
                }
            });
            
            Main.getPrimaryStage().addEventHandler(KeyEvent.KEY_PRESSED, key -> {
                if (key.isControlDown() && key.getCode().equals(KeyCode.F4)) {
                    qe.btnSaveAs_onAction();
                }
            });

        } catch (IOException e) {
            MessageBox.showException("Загрузка главной формы", e);
            Platform.exit();
        } catch (Exception e) {
            MessageBox.showException("Загрузка движка", e);
            Platform.exit();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        exe.close();
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
    }

}
