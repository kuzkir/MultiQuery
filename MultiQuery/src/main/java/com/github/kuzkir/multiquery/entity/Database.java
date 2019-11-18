/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.entity;

import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author kuzkir
 */
public class Database {

    private boolean isActive;
    private transient DatabaseStatus status;
    private String title;
    private String host;
    private int port;
    private String base;
    private String user;
    private String password;

    public Database() {
        this.status = DatabaseStatus.DISCONNECT;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public DatabaseStatus getStatus() {
        return this.status;
    }

    public String getTitle() {
        return this.title;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getBase() {
        return this.base;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public ImageView getImage() {
        if(this.status == null)
            return new ImageView(new Image(getClass().getResourceAsStream("/Disconnected-16.png")));
        
        switch(this.status) {
            case CONNECT:
                return new ImageView(new Image(getClass().getResourceAsStream("/Connected-16.png")));
                
            case LOAD:
                return new ImageView(new Image(getClass().getResourceAsStream("/Load.gif")));
                
            case COMPLETE:
                return new ImageView(new Image(getClass().getResourceAsStream("/Ok-16.png")));
                
            case ERROR:
                return new ImageView(new Image(getClass().getResourceAsStream("/High Importance-16.png")));
                
            case DISCONNECT:
            default:
                return new ImageView(new Image(getClass().getResourceAsStream("/Disconnected-16.png")));
        }
    }

    public Database setIsActive(boolean isActive) {
        this.isActive = isActive;
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

    public Database setHost(String host) {
        this.host = host;
        return this;
    }

    public Database setPort(int port) {
        this.port = port;
        return this;
    }

    public Database setBase(String base) {
        this.base = base;
        return this;
    }

    public Database setUser(String user) {
        this.user = user;
        return this;
    }

    public Database setPassword(String password) {
        this.password = password;
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
