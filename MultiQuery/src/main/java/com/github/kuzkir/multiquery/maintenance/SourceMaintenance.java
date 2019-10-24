/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.maintenance;

import com.github.kuzkir.multiquery.entity.Source;
import java.io.File;

/**
 *
 * @author kuzkir
 */
public class SourceMaintenance extends Maintenance<Source> {

    public SourceMaintenance() {
        super(Source.class, new File("source.json"));
    }
    
    

}
