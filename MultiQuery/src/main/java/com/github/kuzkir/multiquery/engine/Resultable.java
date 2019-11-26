/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author kuzkir
 */
public interface Resultable {
    
    void setResult(Set<String> columnName, List<Map<String,Object>> data);
    void clear();
    void setStatus(String status);
}
