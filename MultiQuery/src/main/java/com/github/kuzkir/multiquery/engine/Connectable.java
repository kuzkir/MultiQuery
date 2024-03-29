/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.engine;

import com.github.kuzkir.multiquery.entity.Database;
import com.github.kuzkir.multiquery.entity.DatabaseStatus;
import java.sql.Driver;
import java.util.List;

/**
 *
 * @author kuzkir
 */
public interface Connectable {
    
    List<Database> getDatabases();
    Driver getDriver();
    void setStatus(String base, DatabaseStatus status);
    void setInfo(String base, String info);
    
}
