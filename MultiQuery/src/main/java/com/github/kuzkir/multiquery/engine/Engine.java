/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.engine;

import com.github.kuzkir.multiquery.entity.Database;
import com.github.kuzkir.multiquery.entity.DatabaseStatus;
import com.github.kuzkir.multiquery.helper.ConnectionHelper;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.application.Platform;

/**
 *
 * @author kuzkir
 */
class Engine implements Executable {

    private final Queryable query;
    private final Connectable connection;
    private final Resultable result;

    private final Map<String, Connection> connectionMap;
    private final Map<String, Thread> threadMap;
    private final Map<String, ResultSet> resultMap;

    private Thread currentThread;

    Engine(Queryable query, Connectable connection, Resultable result) {
        this.query = query;
        this.connection = connection;
        this.result = result;

        this.connectionMap = new HashMap<>();
        this.threadMap = new HashMap<>();
        this.resultMap = new HashMap<>();
    }

    @Override
    public void close() throws Exception {
        for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
            entry.getValue().close();
        }
    }

    @Override
    public void execute() throws Exception {

        currentThread = new Thread(() -> {
            try {
                //очищаем результат и запускаем форму ожидания
                startExecute();

                //обрабатываем подключения
                formConnections();

                //анализируем запрос
                analyzeQuery();

                //вызываем запрос по всем подключениям 
                multiThreadExecute();

                //агрегируем и формируем результат
                formResult();

                //отображаем результат
                endExecute();
            } catch (Exception e) {
                throw e;
            } finally {
                endExecute();
            }

        });

        currentThread.start();
    }

    private void startExecute() {

        threadMap.clear();
        resultMap.clear();

        Platform.runLater(() -> {
            result.setStatus("Очистка результатов");
        });
        Platform.runLater(() -> {
            result.clear();
        });
        Platform.runLater(() -> {
            result.setStatus("Обработка подключений");
        });
    }

    private void endExecute() {
        Platform.runLater(() -> {
            result.setStatus(null);
        });
    }

    private void formConnections() {

        List<Database> list = connection.getDatabases();
        List<String> titles = list.stream().map(a -> a.getTitle()).collect(Collectors.toList());
        Driver driver = connection.getDriver();
        List<String> forRemove = new ArrayList<>();

        for (Map.Entry<String, Connection> e : connectionMap.entrySet()) {
            try {
                if (!titles.contains(e.getKey()) || e.getValue().isClosed()) {

                    forRemove.add(e.getKey());
                    e.getValue().close();
                }
                Platform.runLater(() -> {
                    connection.setStatus(e.getKey(), DatabaseStatus.DISCONNECT);
                });
            } catch (Exception ex) {
                forRemove.add(e.getKey());
            }
        }
        for (String r : forRemove) {
            connectionMap.remove(r);
        }

        for (Database db : list) {
            if (!connectionMap.containsKey(db.getTitle())) {
                String url = ConnectionHelper.getConnectionURL(driver, db.getHost(), db.getPort(), db.getBase());
                try {
                    Connection con = DriverManager.getConnection(url, db.getUser(), db.getPassword());
                    connectionMap.put(db.getTitle(), con);
                    Platform.runLater(() -> {
                        connection.setStatus(db.getTitle(), DatabaseStatus.CONNECT);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        connection.setStatus(db.getTitle(), DatabaseStatus.ERROR);
                    });
                }
            }
        }
    }

    private void analyzeQuery() {

        Platform.runLater(() -> {
            result.setStatus("Анализ запроса");
        });
    }

    private void multiThreadExecute() {
        Platform.runLater(() -> {
            result.setStatus("Формирование стека запросов");
        });

        for (Map.Entry<String, Connection> con : connectionMap.entrySet()) {
            Thread th = new Thread(() -> {
                try {
                    Statement st = con.getValue().createStatement();
                    resultMap.put(con.getKey(), st.executeQuery(query.getQuery()));
                    Platform.runLater(() -> {
                        connection.setStatus(con.getKey(), DatabaseStatus.COMPLETE);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        connection.setStatus(con.getKey(), DatabaseStatus.ERROR);
                        connection.setInfo(con.getKey(), e.getMessage());
                    });
                }
            });

            threadMap.put(con.getKey(), th);
            th.start();

            Platform.runLater(() -> {
                connection.setStatus(con.getKey(), DatabaseStatus.LOAD);
            });
        }

    }

    private void formResult() {

        boolean done = false;

        while (!done) {
            done = true;
            for (Map.Entry<String, Thread> tm : threadMap.entrySet()) {
                done = !tm.getValue().isAlive() && done;
            }
        }
        Platform.runLater(() -> {
            result.setStatus("Формирование результатов");
        });
        
        Concur

        
        
//        try {
//                String BASE_TITLE = "_base";
//
//                if (tableView.getColumns().isEmpty()) {
//                    TableColumn<Map<String, Object>, Object> baseCol = new TableColumn(BASE_TITLE);
//                    baseCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().get(BASE_TITLE)));
//                    tableView.getColumns().add(baseCol);
//                }
//
//                int cc = resultSet.getMetaData().getColumnCount();
//                for (int i = 1; i <= cc; i++) {
//                    String title = resultSet.getMetaData().getColumnName(i);
//
//                    if (!columnList.contains(title)) {
//                        columnList.add(title);
//
//                        TableColumn<Map<String, Object>, Object> col = new TableColumn<>(title);
//                        col.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().get(title)));
//
//                        tableView.getColumns().add(col);
//                    }
//                }
//
//                while (resultSet.next()) {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put(BASE_TITLE, base);
//                    for (String t : columnList) {
//                        try {
//                            map.put(t, resultSet.getObject(t));
//                        } catch (Exception ex) {
//                            map.put(t, null);
//                        }
//                    }
//                    tableView.getItems().add(map);
//                    data.add(map);
//                }
//
//                tableView.scrollTo(0);
//                tableView.scrollToColumnIndex(0);
//            } catch (Exception e) {
//            }
//        }
        

    }
}
