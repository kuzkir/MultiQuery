/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.engine;

import com.github.kuzkir.multiquery.entity.Database;
import com.github.kuzkir.multiquery.entity.DatabaseStatus;
import com.github.kuzkir.multiquery.helper.ConnectionHelper;
import com.github.kuzkir.multiquery.helper.EncodeHelper;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
                    Connection con = DriverManager.getConnection(url, db.getUser(), EncodeHelper.decode(db.getPassword()));
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
                    st.setQueryTimeout(Integer.MAX_VALUE);
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

        threadWait();

        Platform.runLater(() -> {
            result.setStatus("Определение состава результатов");
        });

        String SYS = "*";
        String BASE = "base" + SYS;

        Map<String, Integer> col = new LinkedHashMap<>();
        col.put(BASE, Types.NVARCHAR);

        for (Map.Entry<String, ResultSet> e : resultMap.entrySet()) {
            ResultSet rs = e.getValue();
            try {
                int c = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= c; i++) {
                    String cn = rs.getMetaData().getColumnLabel(i);
                    int ct = rs.getMetaData().getColumnType(i);
                    if (cn.isEmpty()) {
                        cn = String.format("%d%s", i, SYS);
                    }
                    if (col.containsKey(cn)) {
                        if (col.get(cn).equals(ct)) {
                        } else {
                            rs.close();
                            Platform.runLater(() -> {
                                connection.setStatus(e.getKey(), DatabaseStatus.ERROR);
                                connection.setInfo(e.getKey(), "Структура ответа отличается от базовой");
                            });
                        }
                    } else {
                        col.put(cn, ct);
                    }
                }
            } catch (SQLException ex) {
                connection.setStatus(e.getKey(), DatabaseStatus.ERROR);
                connection.setInfo(e.getKey(), ex.getMessage());
            }

        }

        Platform.runLater(() -> {
            result.setStatus("Формирование результатов");
        });

        List<Map<String, Object>> data = new ArrayList<>();

        for (Map.Entry<String, ResultSet> e : resultMap.entrySet()) {
            Thread t = new Thread(() -> {
                ResultSet rs = e.getValue();
                String base = e.getKey();

                try {
                    if (rs.isClosed()) {
                        connection.setStatus(e.getKey(), DatabaseStatus.ERROR);
                        connection.setInfo(e.getKey(), "Соединение закрыто");
                    } else {
                        while (rs.next()) {
                            Map<String, Object> map = new HashMap<>();
                            for (Map.Entry<String, Integer> c : col.entrySet()) {
                                String key = c.getKey();
                                try {
                                    String s = key.substring(0, key.length() - SYS.length());
                                    Object o = ((key.endsWith(SYS) && !key.equals(BASE))
                                        ? rs.getObject(Integer.parseInt(key.substring(0, key.length() - SYS.length())))
                                        : rs.getObject(key));
                                    map.put(key, o);
                                } catch (Exception ex) {
                                    map.put(key, null);
                                }
                            }
                            map.put(BASE, base);
                            synchronized (data) {
                                data.add(map);
                            }
                        }
                    }
                } catch (Exception exc) {
                }
            });

            threadMap.put(e.getKey(), t);
            t.start();
        }

        threadWait();

        Platform.runLater(() -> {
            result.setResult(col.keySet(), data);
        });
    }

    private boolean threadWait() {
        boolean done = false;

        while (!done) {
            done = true;
            for (Map.Entry<String, Thread> tm : threadMap.entrySet()) {
                done = !tm.getValue().isAlive() && done;
            }
        }

        return done;
    }
}
