/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.maintenance;

import com.github.kuzkir.multiquery.entity.Database;
import com.github.kuzkir.multiquery.entity.DatabaseGroup;
import java.util.List;

/**
 *
 * @author kuzkir
 */
public interface ConnectionSource {
    
    List<String> getGroupList() throws Exception;
    DatabaseGroup getGroupByTitle(String title) throws Exception;
    DatabaseGroup addGroup(DatabaseGroup group) throws Exception;
    DatabaseGroup editGroup(String title, DatabaseGroup group) throws Exception;
    boolean removeGroup(String title) throws Exception;
    
//    List<Database> getBaseList(String groupTitle) throws Exception;
    Database addBase(String groupTitle, Database base) throws Exception;
    Database editBase(String groupTitle, String baseTitle, Database base) throws Exception;
    boolean removeBase(String groupTitle, String baseTitle) throws Exception;
    
    
}
