/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.multiquery.engine.Resultable;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class ResultSetController implements Initializable, Resultable {

    private final List<String> columnList;

    @FXML
    StackPane stackPane;
    @FXML
    ScrollPane scrollPane;
    @FXML
    TableView<Map<String, Object>> tableView;
    @FXML
    VBox vbProgress;
    @FXML
    Label lbStatus;

    public ResultSetController() {
        columnList = new ArrayList<>();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void setResult(String base, ResultSet resultSet) {
        try {
            String BASE_TITLE = "_base";

            if (tableView.getColumns().isEmpty()) {
                TableColumn<Map<String, Object>, Object> baseCol = new TableColumn(BASE_TITLE);
                baseCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().get(BASE_TITLE)));
                tableView.getColumns().add(baseCol);
            }

            int cc = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= cc; i++) {
                String title = resultSet.getMetaData().getColumnName(i);

                if (!columnList.contains(title)) {
                    columnList.add(title);
                    
                    TableColumn<Map<String,Object>, Object> col = new TableColumn<>(title);
                    col.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().get(title)));

                    tableView.getColumns().add(col);
                }
            }

            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put(BASE_TITLE,base);
                for (String t : columnList) {
                    try {
                        map.put(t,resultSet.getObject(t));
                    } catch (Exception ex) {
                        map.put(t,null);
                    }
                }
                tableView.getItems().add(map);
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void clear() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        columnList.clear();
    }

    @Override
    public void setStatus(String status) {
        if (status == null) {
            vbProgress.setVisible(false);
            scrollPane.setVisible(true);
            return;
        }
        scrollPane.setVisible(false);
        vbProgress.setVisible(true);
        lbStatus.setText(status);
    }
}
