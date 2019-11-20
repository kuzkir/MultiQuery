/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.multiquery.engine.Queryable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class QueryEditorController implements Initializable, Queryable {

    private static final String[] KEYWORDS = new String[] {
        "select", "top", "with", "as", "from", "join", "inner", "full", "left", "right", "on",
        "where", "and", "not", "in", "like", "order", "by", "asc", "desc", "group", "having",
        "begin", "end", "case", "when", "then", "else", "if", "while"
    };
    
    private static final String[] FUNCTION = new String[] {
        "min", "max", "avg", "ascii", "row_number", "rank", "dense_rank", "getdate"
    };

    @FXML
    private CodeArea codeArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));        
    }

    @Override
    public String getQuery() {
        return codeArea.getText();
    }

}
