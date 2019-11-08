/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.maintenance;

/**
 *
 * @author kuzkir
 */
public interface Maintenance<T> {
    
    T get() throws Exception;
    T set(T entity) throws Exception;
    void save() throws Exception;
    boolean isNull();
}
