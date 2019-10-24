/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.entity;

import java.util.List;

/**
 *
 * @author kuzkir
 */
public interface Crudable<T> extends Comparable<T>{
    
    T get();
    List<T> getAll();
    T create(T entity);
    T update(T entity);
    void delete(T entity);
}
