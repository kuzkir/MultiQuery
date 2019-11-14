/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.engine;

/**
 *
 * @author kuzkir
 */
public class EngineFactory {

    private static EngineFactory instance;

    private Queryable query;
    private Connectable connection;
    private Resultable result;

    private Engine engine;

    private EngineFactory() {
    }

    public static EngineFactory getInstance() {
        if (instance == null) {
            instance = new EngineFactory();
        }
        return instance;
    }

    public EngineFactory setQuery(Queryable query) {
        this.query = query;
        return this;
    }

    public EngineFactory setConnection(Connectable connection) {
        this.connection = connection;
        return this;
    }

    public EngineFactory setResulte(Resultable result) {
        this.result = result;
        return this;
    }

    public Engine build() throws Exception {
        if (engine == null) {
            if (query == null || connection == null || result == null) {
                throw new Exception("Все элементы фабрики должны быть переданы");
            }
            engine = new Engine(query, connection, result);
        }
        return engine;
    }
}
