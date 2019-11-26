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
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class ResultSetController implements Initializable, Resultable {

    private final List<String> columnList;
    private final List<Map<String, Object>> data;
    private final String rcFormat;
    private final String crFormat;
    private FilterMode fmode;

    @FXML
    private TextField tfFilter;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TableView<Map<String, Object>> tableView;
    @FXML
    private VBox vbProgress;
    @FXML
    private Label lbStatus;
    @FXML
    private ImageView ivEditFilter;
    @FXML
    private Tooltip tEditFilter;
    @FXML
    private Label lbRowCount;
    @FXML
    private Label lbCurrentRow;

    public ResultSetController() {
        columnList = new ArrayList<>();
        data = new ArrayList<>();
        rcFormat = "Число строк: %d / %d";
        crFormat = "Текущая строка: %d";
        fmode = FilterMode.CONTAINS;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ivEditFilter.setImage(new Image(getClass().getResourceAsStream("/Asterisk-16.png")));
        tEditFilter.setText("Фильтрация данных по частичному совпадению");
    }

    @FXML
    private void tfFilter_onKeyReleased() {
        String txt = tfFilter.getText().toLowerCase().trim();

        tableView.getItems().clear();
        if (txt.isEmpty()) {
            tableView.getItems().addAll(data);
        } else {
            switch (fmode) {
                case CONTAINS:
                    tableView.getItems().addAll(data.parallelStream()
                        .filter(a -> a.values().parallelStream().anyMatch(b -> b != null && b.toString().toLowerCase().contains(txt)))
                        .collect(Collectors.toList()));
                    break;

                case EQUALS:
                    tableView.getItems().addAll(data.parallelStream()
                        .filter(a -> a.values().parallelStream().anyMatch(b -> b != null && b.toString().toLowerCase().equals(txt)))
                        .collect(Collectors.toList()));
                    break;

                case STRATS_WITH:
                    tableView.getItems().addAll(data.parallelStream()
                        .filter(a -> a.values().parallelStream().anyMatch(b -> b != null && b.toString().toLowerCase().startsWith(txt)))
                        .collect(Collectors.toList()));
                    break;
            }
        }

        lbRowCount.setText(String.format(rcFormat, tableView.getItems().size(), data.size()));
        tableView.sort();
    }

    @FXML
    void btnClearFilter_onAction() {
        tfFilter.setText("");
        tfFilter_onKeyReleased();
    }

    @FXML
    void btnEditFilter_onAction() {
        switch (fmode) {
            case CONTAINS:
                ivEditFilter.setImage(new Image(getClass().getResourceAsStream("/Equal Sign-16.png")));
                tEditFilter.setText("Фильтрация данных по полному совпадению");
                fmode = FilterMode.EQUALS;
                break;

            case EQUALS:
                ivEditFilter.setImage(new Image(getClass().getResourceAsStream("/More Than-16.png")));
                tEditFilter.setText("Фильтрация данных по начальному совпадению");
                fmode = FilterMode.STRATS_WITH;
                break;

            case STRATS_WITH:
                ivEditFilter.setImage(new Image(getClass().getResourceAsStream("/Asterisk-16.png")));
                tEditFilter.setText("Фильтрация данных по частичному совпадению");
                fmode = FilterMode.CONTAINS;
                break;
        }
        tfFilter_onKeyReleased();
    }

    @FXML
    private void tableView_onMouseClicked() {
        int cr = tableView.getSelectionModel().getSelectedIndex() + 1;
        if (cr > 0) {
            lbCurrentRow.setText(String.format(crFormat, cr));
        } else {
            lbCurrentRow.setText(null);
        }
    }

    @Override
    public void setResult(Set<String> columnName, List<Map<String, Object>> data) {
        this.data.addAll(data);
        this.columnList.addAll(columnName);

        for (String cn : columnList) {
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(cn);
            col.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().get(cn)));

            tableView.getColumns().add(col);
        }

        tableView.getItems().addAll(data);

        lbRowCount.setText(String.format(rcFormat, tableView.getItems().size(), data.size()));

        tableView.getSortOrder().add(tableView.getColumns().get(0));
        tableView.scrollTo(0);
        tableView.scrollToColumnIndex(0);

    }

    @Override
    public void clear() {

        if (!tableView.getColumns().isEmpty()) {
            tableView.scrollTo(0);
            tableView.scrollToColumnIndex(0);

            tableView.getColumns().clear();
            tableView.getItems().clear();
            columnList.clear();
            data.clear();

        }
    }

    @Override
    public void setStatus(String status) {
        if (status == null) {
            vbProgress.setVisible(false);
            borderPane.setVisible(true);
            return;
        }
        lbStatus.setText(status);

        borderPane.setVisible(false);
        vbProgress.setVisible(true);
    }

    private enum FilterMode {
        CONTAINS, EQUALS, STRATS_WITH;
    }
}
