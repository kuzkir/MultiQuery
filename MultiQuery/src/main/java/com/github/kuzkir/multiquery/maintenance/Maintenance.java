/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.maintenance;

import com.github.kuzkir.multiquery.helper.JsonHelper;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author kuzkir
 */
public abstract class Maintenance<T> {
    
    private final Class<T> classOfT;
    private final File entityStorage;
    T entity;
    
    public Maintenance(Class<T> classOfT, File entityStorage) {
        this.classOfT = classOfT;
        this.entityStorage = entityStorage;
    }
    
    T get() throws IOException {
        if(entity == null) {
            JsonHelper jh = new JsonHelper();
            entity = jh.get(entityStorage, classOfT);
        }
                
        return entity;
    }
    
    void set(T entity) throws IOException {
        this.entity = entity;
        
        JsonHelper jh = new JsonHelper();
        jh.set(entityStorage, this.entity);
    }
}
