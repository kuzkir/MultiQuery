/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.entity;

import java.sql.Connection;
import java.util.Objects;

/**
 *
 * @author kuzkir
 */
public class Database {

    private boolean isAvaliable;
    private DatabaseStatus status;
    private String title;
    private Connection connection;
    
    public Database() {
        
    }

    public boolean isIsAvaliable() {
        return this.isAvaliable;
    }

    public DatabaseStatus getStatus() {
        return this.status;
    }

    public String getTitle() {
        return this.title;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Database setIsAvaliable(boolean isAvaliable) {
        this.isAvaliable = isAvaliable;
        return this;
    }

    public Database setStatus(DatabaseStatus status) {
        this.status = status;
        return this;
    }

    public Database setTitle(String title) {
        this.title = title;
        return this;
    }

    public Database setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.title);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Database other = (Database) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return title;
    }

}
