/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery;

import com.github.kuzkir.fxmessagebox.MessageBox;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author kuzkir
 */
public class Main extends Application {
    
    
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MultiQuery");
        primaryStage.setMaximized(true);
        
        
        this.primaryStage = primaryStage;
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainPanel.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.getIcons().addAll(new Image(getClass().getResourceAsStream("/Omnichannel_16.png")),
                new Image(getClass().getResourceAsStream("/Omnichannel_32.png")),
                new Image(getClass().getResourceAsStream("/Omnichannel_48.png")),
                new Image(getClass().getResourceAsStream("/Omnichannel_64.png")));
            primaryStage.show();
        } catch (Exception e) {
            MessageBox.showException("Запуск программы", e);
        }
        
        
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
