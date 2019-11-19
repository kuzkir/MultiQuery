/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.engine;

import java.sql.ResultSet;

/**
 *
 * @author kuzkir
 */
public interface Resultable {
    
    void setResult(String base, ResultSet resultSet);
    void clear();
    void setStatus(String status);
}
