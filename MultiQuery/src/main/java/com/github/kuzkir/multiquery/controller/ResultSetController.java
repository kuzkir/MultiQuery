/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.multiquery.engine.Resultable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class ResultSetController implements Initializable, Resultable {

    private final List<String> columnList;
    private final List<Map<String, Object>> data;
    private final Object monitor = new Object();

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField tfFilter;
    @FXML
    private TableView<Map<String, Object>> tableView;
    @FXML
    private VBox vbProgress;
    @FXML
    private Label lbStatus;

    public ResultSetController() {
        columnList = new ArrayList<>();
        data = new ArrayList<>();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void tfFilter_onKeyReleased() {
        String txt = tfFilter.getText().trim();

        tableView.getItems().clear();
        if (txt.isEmpty()) {
            tableView.getItems().addAll(data);
        } else {
            tableView.getItems().addAll(data.parallelStream()
                .filter(a -> a.values().parallelStream().anyMatch(b -> b.toString().contains(txt)))
                .collect(Collectors.toList()));
        }
    }

    @Override
    public void setResult(List<Map<String,Object>> result) {

        tableView.scrollTo(0);
        tableView.scrollToColumnIndex(0);
    }

    @Override
    public void clear() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        columnList.clear();
        data.clear();
    }

    @Override
    public void setStatus(String status) {
        if (status == null) {
            vbProgress.setVisible(false);
            scrollPane.setVisible(true);
            tfFilter.setVisible(true);
            return;
        }
        scrollPane.setVisible(false);
        tfFilter.setVisible(false);
        vbProgress.setVisible(true);
        lbStatus.setText(status);
    }
}
