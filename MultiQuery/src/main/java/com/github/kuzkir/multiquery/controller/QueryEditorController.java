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
import com.github.kuzkir.multiquery.engine.Queryable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * FXML Controller class
 *
 * @author kuzkir
 */
public class QueryEditorController implements Initializable, Queryable {

    @FXML
    private BorderPane borderPane;

    private CodeArea codeArea;
    private Executable exe;
    private File file;

    private StopwatchVirtual stopwatch;

    private static final String[] KEYWORDS = new String[]{
        "select", "top", "with", "as", "from", "join", "inner", "full", "left", "right", "on",
        "where", "and", "not", "in", "like", "order", "by", "asc", "desc", "group", "having",
        "begin", "end", "case", "when", "then", "else", "if", "while", "create", "table", "declare",
        "int", "smallint", "bigint", "char", "varchar", "nchar", "nvarchar", "float", "real", "double",
        "bit", "bool", "over", "partition", "primary", "key", "insert", "into", "exec", "execute",
        "identity", "update", "set", "values", "distinct", "drop", "alter", "delete", "union", "all",
        "or"
    };

    private static final String[] FUNCTION = new String[]{
        "min", "max", "avg", "ascii", "row_number", "rank", "dense_rank", "getdate", "cast", "count"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String FUNCTION_PATTERN = "\\b(" + String.join("|", FUNCTION) + ")\\b";
    private static final String STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";
    private static final String NUMBER_PATTERN = "\\b(\\d+)\\b";
    private static final String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String TITLE_PATTERN = "\\[([^\\[\\\\]|\\\\.)*\\]";
    private static final String TITLE_PATTERN_2 = "\"([^\"\\\\]|\\\\.)*\"";

    private final Pattern whiteSpace = Pattern.compile("^\\s+");

    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
        + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
        + "|(?<STRING>" + STRING_PATTERN + ")"
        + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        + "|(?<TITLE>" + TITLE_PATTERN + ")"
        + "|(?<TITLE2>" + TITLE_PATTERN_2 + ")"
    );

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.getStylesheets().add(getClass().getResource("/styles/query.css").toExternalForm());
        VirtualizedScrollPane scrollPane = new VirtualizedScrollPane<>(codeArea);
        borderPane.setCenter(scrollPane);

        codeArea.multiPlainChanges().successionEnds(Duration.ofMillis(100))
            .subscribe(a -> codeArea.setStyleSpans(0, computeHighlighting()));

        codeArea.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                int caretPosition = codeArea.getCaretPosition();
                int currentParagraph = codeArea.getCurrentParagraph();
                Matcher m = whiteSpace.matcher(codeArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m.find()) {
                    Platform.runLater(() -> codeArea.insertText(caretPosition, m.group()));
                }
            }
        });

        codeArea.onDragOverProperty().set(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });

        codeArea.onDragDroppedProperty().set(event -> {
            List<File> files = event.getDragboard().getFiles();
            if (files.isEmpty()) {
                return;
            }
            File f = files.get(0);
            openFile(f);
        });
    }

    @FXML
    void btnExecute_onAction() {
        stopwatch.start(Main.TITLE);
        try {
            exe.execute();
        } catch (Exception e) {
            MessageBox.showException("Выполнение запроса", e);
        }
    }

    @FXML
    void btnOpen_onAction() {
        FileChooser fc = new FileChooser();
        if (file != null) {
            fc.setInitialDirectory(file.getParentFile());
        }
        File f = fc.showOpenDialog(Main.getPrimaryStage());
        if (f == null) {
            return;
        }
        openFile(f);
    }

    @FXML
    void btnSaveAs_onAction() {
        FileChooser fc = new FileChooser();
        if (file != null) {
            fc.setInitialDirectory(file.getParentFile());
        }
        File f = fc.showSaveDialog(Main.getPrimaryStage());
        if (f == null) {
            return;
        }
        saveFile(f);
    }

    @FXML
    void btnSave_onAction() {
        if (file == null) {
            btnSaveAs_onAction();
            return;
        }
        saveFile(file);
    }

    void setExecutable(Executable exe) {
        this.exe = exe;
    }
    
    void setStopwatch(StopwatchVirtual stopwatch) {
        this.stopwatch = stopwatch;
    }

    @Override
    public String getQuery() {
        String selected = codeArea.getSelectedText();
        return selected.isEmpty() ? codeArea.getText() : selected;
    }

    private StyleSpans<Collection<String>> computeHighlighting() {
        Matcher m = PATTERN.matcher(codeArea.getText().toLowerCase());
        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();

        int end = 0;
        while (m.find()) {
            String styleClass
                = m.group("KEYWORD") != null ? "sql-keyword"
                : m.group("FUNCTION") != null ? "sql-function"
                : m.group("STRING") != null ? "sql-string"
                : m.group("NUMBER") != null ? "sql-number"
                : m.group("COMMENT") != null ? "sql-comment"
                : m.group("TITLE") != null ? "sql-title"
                : m.group("TITLE2") != null ? "sql-title"
                : null;

            builder.add(Collections.emptyList(), m.start() - end);
            builder.add(Collections.singleton(styleClass), m.end() - m.start());
            end = m.end();
        }

        builder.add(Collections.emptyList(), codeArea.getText().length() - end);
        return builder.create();
    }

    private void saveFile(File file) {
        try (FileWriter fw = new FileWriter(file);) {
            fw.write(codeArea.getText());
            this.file = file;
        } catch (Exception e) {
            MessageBox.showException("Сохранение файла", e);
        }

    }

    private void openFile(File file) {
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");

            while ((line = r.readLine()) != null) {
                sb.append(line);
                sb.append(ls);
            }
            sb.deleteCharAt(sb.length() - 1);

            codeArea.replaceText(sb.toString());
            this.file = file;
        } catch (Exception e) {
            MessageBox.showException("Открытие файла", e);
        }
    }
}
