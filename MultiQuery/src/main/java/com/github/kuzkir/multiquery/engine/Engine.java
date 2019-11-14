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
class Engine {
    
    private final Queryable query;
    private final Connectable connection;
    private final Resultable result;
        
    Engine(Queryable query, Connectable connection, Resultable result) {
        this.query = query;
        this.connection = connection;
        this.result = result;
    }
    
    
}
