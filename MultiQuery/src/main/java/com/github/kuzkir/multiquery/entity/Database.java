/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.entity;

import java.sql.Connection;

/**
 *
 * @author kuzkir
 */
public class Database {
    
    private boolean isAvaliable;
    private DatabaseStatus status;
    private String title;
    private Connection connection;
    
}
