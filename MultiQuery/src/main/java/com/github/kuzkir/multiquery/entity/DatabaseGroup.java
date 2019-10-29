/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.entity;

import java.sql.Driver;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author kuzkir
 */
public class DatabaseGroup {

    private String title;
    private Driver driver;
    private Set<Database> databases;

    public DatabaseGroup() {
        databases = new HashSet<>();
    }
    
    public String getTitle() {
        return this.title;
    }

    public Driver getDriver() {
        return driver;
    }

    public Set<Database> getDatabases() {
        return databases;
    }

    public DatabaseGroup setTitle(String title) {
        this.title = title;
        return this;
    }

    public DatabaseGroup setDriver(Driver driver) {
        this.driver = driver;
        return this;
    }

    public DatabaseGroup setDatabases(Set<Database> databases) {
        this.databases = databases;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.title);
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
        final DatabaseGroup other = (DatabaseGroup) obj;
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
