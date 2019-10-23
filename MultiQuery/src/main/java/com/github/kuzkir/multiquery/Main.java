/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery;

import com.github.kuzkir.multiquery.maintenance.SourceMaintenance;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 *
 * @author kuzkir
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MultiuQuery");
        primaryStage.setMaximized(true);
        
        SourceMaintenance sm = new SourceMaintenance();
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainPanel.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Ошибка загрузки формы");
            message.setHeaderText(e.getMessage());
            message.show();
        }
        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
