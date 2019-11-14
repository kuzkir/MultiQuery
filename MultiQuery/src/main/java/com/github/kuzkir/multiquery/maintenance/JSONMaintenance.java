/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.maintenance;

import com.github.kuzkir.multiquery.helper.JsonHelper;
import java.io.File;

/**
 *
 * @author kuzkir
 */
public abstract class JSONMaintenance<T> implements Maintenance<T> {

    private final Class<T> classOfT;
    private final File entityStorage;
    private T entity;

    public JSONMaintenance(Class<T> classOfT, File entityStorage) {
        this.classOfT = classOfT;
        this.entityStorage = entityStorage;
    }

    @Override
    public boolean isNull() {
        return this.entity == null;
    }

    @Override
    public T get() throws Exception {
        if (isNull()) {
            JsonHelper jh = new JsonHelper();
            entity = createNew();
            entity = jh.get(entityStorage, classOfT);
        }

        synchronized (this) {
            return entity;
        }
    }

    @Override
    public T set(T entity) throws Exception {
        synchronized (this) {
            this.entity = entity;
            save();
            return this.entity;
        }
    }

    @Override
    public void save() throws Exception {
        JsonHelper jh = new JsonHelper();
        jh.set(entityStorage, this.entity);
    }

    public abstract T createNew();

}
