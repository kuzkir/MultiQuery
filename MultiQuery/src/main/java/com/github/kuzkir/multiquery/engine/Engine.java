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

    private final Map<String, Connection> map;

    private Thread currentThread;

    Engine(Queryable query, Connectable connection, Resultable result) {
        this.query = query;
        this.connection = connection;
        this.result = result;

        this.map = new HashMap<>();
    }

    @Override
    public void close() throws Exception {
        for (Map.Entry<String, Connection> entry : map.entrySet()) {
            entry.getValue().close();
        }
    }

    @Override
    public void execute() throws Exception {

        currentThread = new Thread(() -> {
            try {//очищаем результат и запускаем форму ожидания
                startExecute();

                //обрабатываем подключения
                formConnections();

                //анализируем запрос
                //вызываем запрос по всем подключениям 
                endExecute();
            } catch (Exception e) {
            } finally {
                endExecute();
            }

        });

        currentThread.start();
    }

    private void startExecute() {

        Platform.runLater(() -> {
            result.setStatus("Очистка результатов");
            result.clear();
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

        for (Map.Entry<String, Connection> e : map.entrySet()) {
            if (!titles.contains(e.getKey())) {
                try {
                    forRemove.add(e.getKey());
                    e.getValue().close();
                } catch (Exception ex) {
                }
                Platform.runLater(() -> {
                    connection.setStatus(e.getKey(), DatabaseStatus.DISCONNECT);
                });
            }
        }
        for(String r : forRemove) {
            map.remove(r);
        }

        for (Database db : list) {
            if (!map.containsKey(db.getTitle())) {
                String url = ConnectionHelper.getConnectionURL(driver, db.getHost(), db.getPort(), db.getBase());
                try {
                    Connection con = DriverManager.getConnection(url, db.getUser(), db.getPassword());
                    map.put(db.getTitle(), con);
                    Platform.runLater(() -> {
                        connection.setStatus(db.getTitle(), DatabaseStatus.CONNECTED);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        connection.setStatus(db.getTitle(), DatabaseStatus.ERROR);
                    });
                }
            }
        }
    }

}
